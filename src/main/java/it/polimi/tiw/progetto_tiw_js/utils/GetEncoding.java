package it.polimi.tiw.progetto_tiw_js.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;

import jakarta.servlet.ServletContext;

import org.apache.commons.io.FileUtils;

import it.polimi.tiw.progetto_tiw_js.beans.User;
import it.polimi.tiw.progetto_tiw_js.dao.SongDAO;

public class GetEncoding {


    public static String getImageEncoding(String fileName , ServletContext context , Connection connection , User user) throws IOException {

        String folderPath = context.getInitParameter("albumImgPath");
        SongDAO sDao = new SongDAO(connection);

        try {
            if(!sDao.findImageByUser(fileName, user.getId())) {
                return null;
            }
        }catch(SQLException e) {
            return null;
        }

        //Open the file
        File file = new File(folderPath, fileName);

        if (!file.exists() || file.isDirectory()) {
            return null;
        }

        //Take the byte array of the image
        byte[] fileContent = FileUtils.readFileToByteArray(file);
        //Take the base64 string
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        //Take the right type of each file (png , jpeg ...)
        encodedString = "data:" + context.getMimeType(fileName) + ";base64," + encodedString ;

        return encodedString;
    }

    /**
     * Static method that take from the local storage the song and encode it into a base64 string
     * @param fileName is the name of the song file
     * @param context is the context
     * @param connection is a connection with the data base
     * @param user is the user who is doing the request
     * @return a string containing the base64 encoding of the image
     * @throws IOException
     */
    public static String getSongEncoding(String fileName , ServletContext context , Connection connection , User user) throws IOException {

        //System.out.println("GetSong called, name of the song: " + fileName);

        String folderPath = context.getInitParameter("songFilePath");
        SongDAO sDao = new SongDAO(connection);

        try {
            if(!sDao.findSongByUserId(fileName, user.getId())){
                return null;
            }
        }catch(SQLException e) {
            return null;
        }

        //Open the file
        File file = new File(folderPath, fileName);

        if (!file.exists() || file.isDirectory()) {
            return null;
        }

        //Take the byte array of the image
        byte[] fileContent = FileUtils.readFileToByteArray(file);
        //Take the base64 string
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        //Take the right type of the file (mp3)
        encodedString = "data:" + context.getMimeType(fileName) + ";base64," + encodedString ;

        return encodedString;
    }
}
