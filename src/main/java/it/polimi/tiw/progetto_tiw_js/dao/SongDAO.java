package it.polimi.tiw.progetto_tiw_js.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import it.polimi.tiw.progetto_tiw_js.beans.Song;

public class SongDAO {
    private Connection connection;

    public SongDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Retrieves the image filename for an album given its title and author.
     *
     * @param albumTitle The title of the album.
     * @param author     The author/artist of the album.
     * @return The image filename if found, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    public String getAlbumImageByTitleAndArtist(String albumTitle, String author) throws SQLException {
        String query = "SELECT imgFile FROM album WHERE title = ? AND author = ? AND imgFile IS NOT NULL LIMIT 1";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, albumTitle);
            pStatement.setString(2, author);
            try (ResultSet rs = pStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("imgFile");
                }
            }
        }
        return null;
    }


    private int findAlbumId(String albumTitle , String author , int publicationYear)throws SQLException {
        String query = "SELECT id FROM album WHERE title = ? AND author = ? AND publicationYear = ?";
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;
        int result = 0;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, albumTitle);
            pStatement.setString(2, author);
            pStatement.setInt(3, publicationYear);

            resultSet = pStatement.executeQuery();
            if(resultSet.next())
                result = resultSet.getInt("id");
        }catch(SQLException e) {
            throw e;
        }finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            }catch(Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if(pStatement != null) {
                    pStatement.close();
                }
            }catch(Exception e2) {
                throw new SQLException(e2);
            }
        }
        return result;
    }


    public boolean createSongAndAlbum(int userId , String songTitle , String genre , String albumTitle , String author , int publicationYear , String imgFile , String songName, String audioFileHash)
            throws SQLException{

        boolean result = false;
        try {
            connection.setAutoCommit(false);

            int albumId = createAlbum(albumTitle , author , publicationYear , imgFile); // imgFile is passed but used only if new album is created
            result = createSong(songTitle, author, userId, genre, songName, imgFile, albumId, publicationYear, audioFileHash);

            connection.commit();
        }catch(SQLException e){
            connection.rollback();
            throw e;
        }finally {
            connection.setAutoCommit(true);
        }
        return result;
    }

    /**
     * Method that finds an existing album by title, author, and year, or creates a new one if not found.
     * If the album exists, its ID is returned. The provided filename (image) is only used if a new album is created.
     * @param albumTitle is the name of the album
     * @param author is the author/singer
     * @param publicationYear is the publication year of the album
     * @param filename is the name of the stored image (used only if creating a new album, can be null)
     * @return the id of the album (either existing or newly created)
     * @throws SQLException
     */
    private int createAlbum(String albumTitle , String author , int publicationYear , String filename) throws SQLException{

        int albumId = findAlbumId(albumTitle, author, publicationYear);


        if (albumId != 0) {
            return albumId;
        }


        String query = "INSERT INTO album (title , image , author , publicationYear) VALUES (? , ? , ? , ?)";
        PreparedStatement pStatement = null;
        int newAlbumId = 0;

        try {
            pStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, albumTitle);
            if (filename == null) {
                pStatement.setNull(2, Types.VARCHAR);
            } else {
                pStatement.setString(2, filename);
            }
            pStatement.setString(3, author);
            pStatement.setInt(4, publicationYear);

            int code = pStatement.executeUpdate();

            if (code > 0) {

                ResultSet generatedKeys = pStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    newAlbumId = generatedKeys.getInt(1);
                } else {

                    newAlbumId = findAlbumId(albumTitle, author, publicationYear);
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e1) {

                throw new SQLException("Error closing statement in createAlbum", e1);
            }
        }
        return newAlbumId; // Return the ID of the newly created album, or 0 if insertion failed
    }

  /**
     * Method that create a new song in the data base
     * @param userId is the id of the user who is updating the DB
     * @param songTitle is the title of the song
     * @param author is the author/singer
     * @param genre is the genre of the song
     * @param songFile is the name of the stored song file
     * @param imgFile is the name of the stored image file
     * @param albumId is the id of the album that contains this song
     * @param publicationYear is the publication year of the album that contains this song
     * @param audioFileHash is the hash of the audio file content
     * @return true if everything went okay, false otherwise
     * @throws SQLException
     */
    private boolean createSong(String songTitle, String author, int userId , String genre , String songFile, String imgFile ,int albumId, int publicationYear, String audioFileHash) throws SQLException{
        String query = "INSERT INTO song (title, author, userId , kindOf , songFile, imgFile,  idAlbum, publicationYear, audio_hash) VALUES (? , ? , ? , ?, ? , ? , ? , ?, ?)";
        PreparedStatement pStatement = null;
        int code = 0;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, songTitle);
            pStatement.setString(2, author);
            pStatement.setInt(3, userId);
            pStatement.setString(4, genre);
            pStatement.setString(5, songFile);
            if (imgFile == null) {
                pStatement.setNull(6, Types.VARCHAR);
            } else {
                pStatement.setString(6, imgFile);
            }
            if (albumId <= 0) {
                pStatement.setNull(7, Types.INTEGER);
            } else {
                pStatement.setInt(7, albumId);
            }
            pStatement.setInt(8, publicationYear);
            pStatement.setString(9, audioFileHash);
            code = pStatement.executeUpdate();
        }catch(SQLException e) {
            throw e;
        }finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
        }
        return (code > 0);
    }


    public ArrayList<Song> getSongTitleAndImg(int playlistId) throws SQLException{
        String query = "SELECT song.id, song.title, album.image " +
                       "FROM contains " +
                       "JOIN song ON contains.sId = song.id " +
                       "JOIN album ON song.idAlbum = album.id " +
                       "WHERE contains.pId = ? " +
                       "ORDER BY album.author ASC, album.publicationYear ASC";
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;
        ArrayList<Song> songs = new ArrayList<Song>();

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, playlistId);

            resultSet = pStatement.executeQuery();

            while(resultSet.next()) {
                Song song = new Song();


                song.setId(resultSet.getInt("song.id"));
                song.setSongTitle(resultSet.getString("song.title"));
                song.setImgFile(resultSet.getString("album.image"));//Set the name of the image file
                songs.add(song);
            }
        }catch(SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            }catch(Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if(pStatement != null) {
                    pStatement.close();
                }
            }catch(Exception e2) {
                throw new SQLException(e2);
            }
        }
        return songs;
    }

    public ArrayList<Song> getSongsNotInPlaylist(int playlistId , int userId) throws SQLException{
        String query = "SELECT s.id, s.title, s.author, s.publicationYear " +
                       "FROM song s " +
                       "LEFT JOIN album a ON s.idAlbum = a.id " +
                       "WHERE s.userId = ? AND s.id NOT IN (" +
                       "SELECT sId FROM contains WHERE pId = ?) " +
                       "ORDER BY COALESCE(a.author, s.author) ASC, s.publicationYear ASC";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;
        ArrayList<Song> songs = new ArrayList<Song>();

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, userId);
            pStatement.setInt(2, playlistId);

            resultSet = pStatement.executeQuery();

            while(resultSet.next()) {
                Song song = new Song();
                song.setId(resultSet.getInt("id"));
                song.setSongTitle(resultSet.getString("title"));
                song.setAuthor(resultSet.getString("author"));
                song.setPublicationYear(resultSet.getInt("publicationYear"));
                songs.add(song);
            }
        }catch(SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            }catch(Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if(pStatement != null) {
                    pStatement.close();
                }
            }catch(Exception e2) {
                throw new SQLException(e2);
            }
        }
        return songs;
    }

    public boolean findSongByUser(int sId , int userId) throws SQLException{
        String query = "SELECT * FROM song WHERE id = ? AND userId = ?";
        boolean result = false;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, sId);
            pStatement.setInt(2, userId);

            resultSet = pStatement.executeQuery();

            if(resultSet.next())
                result = true;

        }catch(SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            }catch(Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if(pStatement != null) {
                    pStatement.close();
                }
            }catch(Exception e2) {
                throw new SQLException(e2);
            }
        }
        return result;
    }


    public Song getSongDetails(int songId) throws SQLException{
        String query = "SELECT * FROM song JOIN album on song.idAlbum = album.id WHERE song.id = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;
        Song song = new Song();

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, songId);

            resultSet = pStatement.executeQuery();

            if(resultSet.next()) {
                song.setSongTitle(resultSet.getString("song.title"));
                song.setAlbumId(resultSet.getInt("album.id"));
                song.setAlbumTitle(resultSet.getString("album.title"));
                song.setAuthor(resultSet.getString("album.author"));
                song.setKindOf(resultSet.getString("song.kindOf"));
                song.setPublicationYear(resultSet.getInt("album.publicationYear"));
                song.setSongFile(resultSet.getString("song.songFile"));
                song.setImgFile(resultSet.getString("album.image"));
            }
        }catch(SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            }catch(Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if(pStatement != null) {
                    pStatement.close();
                }
            }catch(Exception e2) {
                throw new SQLException(e2);
            }
        }
        return song;
    }


    public boolean findImageByUser(String imageName , int userId) throws SQLException{
        String query = "SELECT 1 FROM song s JOIN album a ON s.idAlbum = a.id WHERE a.image = ? AND s.userId = ? LIMIT 1";
        boolean result = false;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, imageName);
            pStatement.setInt(2, userId);

            resultSet = pStatement.executeQuery();

            if(resultSet.next())
                result = true;

        }catch(SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            }catch(Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if(pStatement != null) {
                    pStatement.close();
                }
            }catch(Exception e2) {
                throw new SQLException(e2);
            }
        }
        return result;
    }


    public boolean findSongByUserId(String songName , int userId) throws SQLException{
        String query = "SELECT * FROM song WHERE songFile = ? AND userId = ?";
        boolean result = false;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, songName);
            pStatement.setInt(2, userId);

            resultSet = pStatement.executeQuery();

            if(resultSet.next())
                result = true;

        }catch(SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            }catch(Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if(pStatement != null) {
                    pStatement.close();
                }
            }catch(Exception e2) {
                throw new SQLException(e2);
            }
        }
        return result;
    }


    public ArrayList<Song> getSongsByUserId(int userId) throws SQLException {
        String query = "SELECT id, title FROM song WHERE userId = ? ORDER BY title ASC";
        ArrayList<Song> songs = new ArrayList<>();
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, userId);
            resultSet = pStatement.executeQuery();

            while (resultSet.next()) {
                Song song = new Song();
                song.setId(resultSet.getInt("id"));
                song.setSongTitle(resultSet.getString("title"));

                songs.add(song);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {

            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {

            }
        }
        return songs;
    }


    public ArrayList<Song> getSongsByUserIdOrdered(int userId) throws SQLException {
        String query = "SELECT s.id, s.title, s.author, s.publicationYear " +
                       "FROM song s " +
                       "LEFT JOIN album a ON s.idAlbum = a.id " +
                       "WHERE s.userId = ? " +
                       "ORDER BY COALESCE(a.author, s.author) ASC, s.publicationYear ASC";
        ArrayList<Song> songs = new ArrayList<>();
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, userId);
            resultSet = pStatement.executeQuery();

            while (resultSet.next()) {
                Song song = new Song();
                song.setId(resultSet.getInt("id"));
                song.setSongTitle(resultSet.getString("title"));
                song.setAuthor(resultSet.getString("author"));
                song.setPublicationYear(resultSet.getInt("publicationYear"));
                songs.add(song);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                // Log or handle resultSet close exception
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                // Log or handle pStatement close exception
            }
        }
        return songs;
    }

    /**
     * Retrieves a song's metadata by its audio file hash and user ID.
     * @param audioFileHash The hash of the audio file.
     * @param userId The ID of the user.
     * @return A Song bean with populated metadata if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public Song getSongByAudioHashAndUser(String audioFileHash, int userId) throws SQLException {
        String query = "SELECT s.title as songTitle, s.author as author, s.kindOf as kindOf, " +
                       "s.publicationYear as publicationYear, s.imgFile as imgFile, " +
                       "a.title as albumTitle, a.id as albumId " +
                       "FROM song s LEFT JOIN album a ON s.idAlbum = a.id " +
                       "WHERE s.audio_hash = ? AND s.userId = ?";
        Song song = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, audioFileHash);
            pStatement.setInt(2, userId);
            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                song = new Song();
                song.setSongTitle(resultSet.getString("songTitle"));
                song.setAuthor(resultSet.getString("author"));
                song.setKindOf(resultSet.getString("kindOf"));
                song.setPublicationYear(resultSet.getInt("publicationYear"));
                song.setImgFile(resultSet.getString("imgFile")); // Song's own image
                song.setAlbumTitle(resultSet.getString("albumTitle")); // Can be null
                int albumId = resultSet.getInt("albumId");
                if(resultSet.wasNull()){
                    song.setAlbumId(0); // Or handle as appropriate if 0 is a valid ID
                } else {
                    song.setAlbumId(albumId);
                }
            }
        } catch (SQLException e) {

            throw new SQLException("Error fetching song by audio hash and user: " + e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                // Log e
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (SQLException e) {
                // Log e
            }
        }
        return song;
    }
}
