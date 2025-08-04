package it.polimi.tiw.progetto_tiw_js.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import it.polimi.tiw.progetto_tiw_js.beans.Song;
import it.polimi.tiw.progetto_tiw_js.beans.User;
import it.polimi.tiw.progetto_tiw_js.dao.SongDAO;
import it.polimi.tiw.progetto_tiw_js.utils.ConnectionHandler;

@WebServlet("/CreateSong")
@MultipartConfig
public class CreateSong extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private String imgFolderPath = "";
    private String mp3FolderPath = "";

    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        connection = ConnectionHandler.getConnection(servletContext);
        try {
            imgFolderPath = getServletContext().getInitParameter("albumImgPath");
            mp3FolderPath = getServletContext().getInitParameter("songFilePath");
        } catch (Exception e) {
            throw new ServletException("Error initializing file paths from web.xml", e);
        }
        if (imgFolderPath == null || mp3FolderPath == null || imgFolderPath.isEmpty() || mp3FolderPath.isEmpty()) {
            throw new ServletException("File paths for images or songs are not configured in web.xml. Ensure they end with a file separator.");
        }
        if (!imgFolderPath.endsWith(File.separator)) {
            imgFolderPath += File.separator;
        }
        if (!mp3FolderPath.endsWith(File.separator)) {
            mp3FolderPath += File.separator;
        }
    }

    private static String calculateFileHash(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] byteArray = new byte[1024];
        int bytesCount;
        while ((bytesCount = inputStream.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    private boolean areStringsEqualOrBothNullOrEmpty(String s1, String s2) {
        String str1 = (s1 == null || s1.trim().isEmpty()) ? null : s1.trim();
        String str2 = (s2 == null || s2.trim().isEmpty()) ? null : s2.trim();

        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            if ((str1 == null && "Unknown Album".equals(str2)) || ("Unknown Album".equals(str1) && str2 == null)) {
                return true;
            }
            return false;
        }
        return str1.equals(str2);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("User not logged in.");
            return;
        }

        String songTitle = request.getParameter("title");
        String genre = request.getParameter("genre");
        String albumTitleParam = request.getParameter("albumTitle");
        String singer = request.getParameter("artist");
        String publicationYearStr = request.getParameter("publicationYear");

        Part albumImgPart = request.getPart("imgFile");
        Part songFilePart = request.getPart("songFile");

        StringBuilder error = new StringBuilder();
        int publicationYear = 0;


        if (songTitle == null || songTitle.trim().isEmpty()) error.append("Song title is required; ");
        if (genre == null || genre.trim().isEmpty()) error.append("Genre is required; ");
        if (singer == null || singer.trim().isEmpty()) error.append("Artist is required; ");
        if (publicationYearStr == null || publicationYearStr.trim().isEmpty()) error.append("Publication year is required; ");

        if (error.length() == 0) {
            if (songTitle.length() > 45) error.append("Song title too long (max 45); ");
            if (genre.length() > 45) error.append("Genre too long (max 45); ");
            if (singer.length() > 45) error.append("Artist name too long (max 45); ");
            if (albumTitleParam != null && !albumTitleParam.trim().isEmpty() && albumTitleParam.trim().length() > 45) {
                error.append("Album title too long (max 45); ");
            }

            try {
                publicationYear = Integer.parseInt(publicationYearStr);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (publicationYear > currentYear || publicationYear < 1000) {
                    error.append("Invalid publication year; ");
                }
            } catch (NumberFormatException e) {
                error.append("Publication year must be a number; ");
            }

            if (!(genre.equals("Dance") || genre.equals("Pop") || genre.equals("Rock") || genre.equals("Rap")
                    || genre.equals("Classical") || genre.equals("Jazz") || genre.equals("Blues") || genre.equals("Metal"))) {
                error.append("Invalid genre;");
            }
        }


        String dbImageFileName = null;
        String diskImageFileDestPath = null;
        String existingAlbumImageFile = null;
        boolean newImageUploaded = false;
        SongDAO songDAO = new SongDAO(connection);


        String trimmedAlbumTitle = (albumTitleParam != null) ? albumTitleParam.trim() : "";
        String trimmedSinger = (singer != null) ? singer.trim() : "";

        if (!trimmedAlbumTitle.isEmpty() && !trimmedSinger.isEmpty()) {
            try {
                existingAlbumImageFile = songDAO.getAlbumImageByTitleAndArtist(trimmedAlbumTitle, trimmedSinger);
            } catch (SQLException e) {
                System.err.println("Error checking for existing album image: " + e.getMessage());

            }
        }

        if (existingAlbumImageFile != null) {
            dbImageFileName = existingAlbumImageFile;

        } else {

            if (albumImgPart != null && albumImgPart.getSize() > 0 && albumImgPart.getSubmittedFileName() != null && !albumImgPart.getSubmittedFileName().trim().isEmpty()) {
                String contentTypeImg = albumImgPart.getContentType();
                if (contentTypeImg == null || !contentTypeImg.startsWith("image")) {
                    error.append("Image file not valid (must be an image type); ");
                } else if (albumImgPart.getSize() > 2048000) { // 2MB
                    error.append("Image size is too big (max 2MB); ");
                } else {
                    String originalFileName = Paths.get(albumImgPart.getSubmittedFileName()).getFileName().toString();
                    originalFileName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_"); // Sanitize
                    dbImageFileName = user.getId() + "_" + System.currentTimeMillis() + "_img_" + originalFileName;
                    if (dbImageFileName.length() > 255) {
                        error.append("Generated image file name is too long; ");
                        dbImageFileName = null;
                    } else {
                        diskImageFileDestPath = imgFolderPath + dbImageFileName;
                        newImageUploaded = true; // Mark that a new image is to be saved to disk
                    }
                }
            }

        }


        String dbSongFileName = null;
        String diskSongFileDestPath = null;
        String audioFileHash = null;

        if (songFilePart == null || songFilePart.getSize() == 0 || songFilePart.getSubmittedFileName() == null || songFilePart.getSubmittedFileName().trim().isEmpty()) {
            error.append("Music file is mandatory; ");
        } else {
            String contentTypeMusic = songFilePart.getContentType();
            if (contentTypeMusic == null || !contentTypeMusic.startsWith("audio")) {
                error.append("Music file not valid (must be an audio type); ");
            } else if (songFilePart.getSize() > 10485760) { // 10MB
                error.append("Song file size is too big (max 10MB); ");
            } else {
                try (InputStream songFileStreamForHash = songFilePart.getInputStream()) {
                    audioFileHash = calculateFileHash(songFileStreamForHash);
                } catch (NoSuchAlgorithmException e) {
                    error.append("Error calculating file hash (algorithm not found); ");
                } catch (IOException e) {
                    error.append("Error reading song file for hashing; ");
                }

                if (audioFileHash != null) { // Proceed only if hash calculation was successful
                    String originalFileName = Paths.get(songFilePart.getSubmittedFileName()).getFileName().toString();
                    originalFileName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_"); // Sanitize
                    dbSongFileName = user.getId() + "_" + System.currentTimeMillis() + "_song_" + originalFileName;
                    if (dbSongFileName.length() > 255) {
                        error.append("Generated song file name is too long; ");
                        dbSongFileName = null;
                    } else {
                        diskSongFileDestPath = mp3FolderPath + dbSongFileName;
                    }
                }
            }
        }

        if (error.length() > 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(error.toString().trim());
            return;
        }


        if (audioFileHash != null && dbSongFileName != null) {
            try {
                Song existingSong = songDAO.getSongByAudioHashAndUser(audioFileHash, user.getId());
                if (existingSong != null) {
                    String finalAlbumTitleForNewSong = (trimmedAlbumTitle.isEmpty()) ? "Unknown Album" : trimmedAlbumTitle;
                    String existingAlbumTitle = (existingSong.getAlbumTitle() == null || existingSong.getAlbumTitle().isEmpty()) ? "Unknown Album" : existingSong.getAlbumTitle().trim();

                    boolean titleDiff = !areStringsEqualOrBothNullOrEmpty(songTitle, existingSong.getSongTitle());
                    boolean artistDiff = !areStringsEqualOrBothNullOrEmpty(trimmedSinger, existingSong.getAuthor());
                    boolean genreDiff = !areStringsEqualOrBothNullOrEmpty(genre, existingSong.getKindOf());
                    boolean yearDiff = (publicationYear != existingSong.getPublicationYear());
                    boolean albumTitleDiff = !areStringsEqualOrBothNullOrEmpty(finalAlbumTitleForNewSong, existingAlbumTitle);
                    boolean imgFileDiff = !areStringsEqualOrBothNullOrEmpty(dbImageFileName, existingSong.getImgFile());


                    if (titleDiff || artistDiff || genreDiff || yearDiff || albumTitleDiff || imgFileDiff) {
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        response.getWriter().println("Error: This audio content already exists in your library but with different song information (title, artist, album, etc.). Please ensure all details match or upload distinct audio content.");
                        return;
                    } else {
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        response.getWriter().println("Error: This exact song (audio content and all details) already exists in your library.");
                        return;
                    }
                }
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Database error during duplicate check: " + e.getMessage());
                return;
            }
        } else if (audioFileHash == null || dbSongFileName == null) { // Should have been caught by earlier errors, but as a safeguard
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Could not process song file or calculate its hash.");
            return;
        }


        File savedImageFileOnDisk = null;
        File savedSongFileOnDisk = null;

        try {

            if (newImageUploaded && dbImageFileName != null && diskImageFileDestPath != null) {
                savedImageFileOnDisk = new File(diskImageFileDestPath);
                try (InputStream fileContent = albumImgPart.getInputStream()) {
                    Files.copy(fileContent, savedImageFileOnDisk.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            savedSongFileOnDisk = new File(diskSongFileDestPath);
            try (InputStream fileContent = songFilePart.getInputStream()) {
                Files.copy(fileContent, savedSongFileOnDisk.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {

            if (newImageUploaded && savedImageFileOnDisk != null && savedImageFileOnDisk.exists()) {
                savedImageFileOnDisk.delete();
            }
            if (savedSongFileOnDisk != null && savedSongFileOnDisk.exists()) {
                savedSongFileOnDisk.delete();
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error saving files: " + e.getMessage());
            return;
        }


        try {
            String finalAlbumTitleForDB = (trimmedAlbumTitle.isEmpty()) ? "Unknown Album" : trimmedAlbumTitle;

            boolean result = songDAO.createSongAndAlbum(user.getId(), songTitle.trim(), genre, finalAlbumTitleForDB,
                    trimmedSinger, publicationYear, dbImageFileName,
                    dbSongFileName, audioFileHash);

            if (result) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("Song created successfully!");
            } else {

                if (newImageUploaded && savedImageFileOnDisk != null && savedImageFileOnDisk.exists()) {
                    savedImageFileOnDisk.delete();
                }
                if (savedSongFileOnDisk != null && savedSongFileOnDisk.exists()) {
                    savedSongFileOnDisk.delete();
                }
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Impossible to upload file data to the database, try later.");
            }
        } catch (SQLException e) {

            if (newImageUploaded && savedImageFileOnDisk != null && savedImageFileOnDisk.exists()) {
                savedImageFileOnDisk.delete();
            }
            if (savedSongFileOnDisk != null && savedSongFileOnDisk.exists()) {
                savedSongFileOnDisk.delete();
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("An error occurred with the database: " + e.getMessage());
        }
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}