package it.polimi.tiw.progetto_tiw_js.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import it.polimi.tiw.progetto_tiw_js.beans.Song;
import it.polimi.tiw.progetto_tiw_js.beans.User;
import it.polimi.tiw.progetto_tiw_js.dao.PlaylistDAO;
import it.polimi.tiw.progetto_tiw_js.dao.SongDAO;
import it.polimi.tiw.progetto_tiw_js.utils.ConnectionHandler;
import it.polimi.tiw.progetto_tiw_js.utils.GetEncoding;

@WebServlet("/GetSongDetails")
@MultipartConfig
public class GetSongDetails extends HttpServlet{

    private static final long serialVersionUID = 1L;
    private Connection connection;

    public void init() {
        ServletContext context = getServletContext();

        try {
            connection = ConnectionHandler.getConnection(context);
        } catch (UnavailableException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request , HttpServletResponse response)throws ServletException,IOException{

        String songId = StringEscapeUtils.escapeJava(request.getParameter("songId"));
        String playlistId = StringEscapeUtils.escapeJava(request.getParameter("playlistId"));
        String error = "";
        int sId = -1;
        int pId = -1;

        HttpSession s = request.getSession();

        User user = (User) s.getAttribute("user");


        if(songId.isEmpty() || songId == null)
            error += "Song not defined;";

        if(playlistId.isBlank() || playlistId == null)
            error += "Playlist not defined;";


        if(error.equals("")) {
            try {

                SongDAO sDao = new SongDAO(connection);
                PlaylistDAO pDao = new PlaylistDAO(connection);


                sId = Integer.parseInt(songId);
                pId = Integer.parseInt(playlistId);


                if(!sDao.findSongByUser(sId, user.getId())) {
                    error += "Song doesn't exist";
                }

                if(!pDao.findPlayListById(pId, user.getId())) {
                    error += "Playlist doesn't exist;";
                }
            }catch(NumberFormatException e) {
                error += "Request with bad format;";
            }catch(SQLException e) {
                error += "Impossible to comunicate with the data base;";
            }
        }


        if(!error.equals("")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
            response.getWriter().println(error);
            return;
        }




        SongDAO sDao = new SongDAO(connection);

        try {
            Song song = sDao.getSongDetails(sId);

            JSONObject jSonObject = new JSONObject();

            jSonObject.put("songTitle" , song.getSongTitle());
            jSonObject.put("author" , song.getAuthor());
            jSonObject.put("albumTitle" , song.getAlbumTitle());
            jSonObject.put("publicationYear" , song.getPublicationYear());
            jSonObject.put("genre" , song.getKindOf());
            jSonObject.put("imgFile" , song.getImgFile());

            if (song.getImgFile() != null && !song.getImgFile().isEmpty()) {
                try {
                    String imageBase64 = GetEncoding.getImageEncoding(song.getImgFile(), getServletContext(), connection, user); // Assuming this method exists
                    jSonObject.put("imageBase64String", imageBase64);
                } catch (java.io.IOException e1) {

                    jSonObject.put("imageBase64String", "");
                }
            }

            try {
                jSonObject.put("base64String" , GetEncoding.getSongEncoding(song.getSongFile(),
                        getServletContext(), connection, user));

            }catch(IOException e2) {
                jSonObject.put("base64String" , "");
            }

            response.setStatus(HttpServletResponse.SC_OK);//Code 200
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(jSonObject);

        }catch(SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
            response.getWriter().println("An error occurred with the db, retry later");
            return;
        }catch(JSONException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
            response.getWriter().println("Internal server error, error during the creation of the response");
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

