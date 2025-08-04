package it.polimi.tiw.progetto_tiw_js.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.polimi.tiw.progetto_tiw_js.beans.Song;
import it.polimi.tiw.progetto_tiw_js.beans.User;
import it.polimi.tiw.progetto_tiw_js.dao.PlaylistDAO;
import it.polimi.tiw.progetto_tiw_js.dao.SongDAO;
import it.polimi.tiw.progetto_tiw_js.utils.ConnectionHandler;
import it.polimi.tiw.progetto_tiw_js.utils.GetEncoding;

@WebServlet("/GetSongsInPlaylist")
@MultipartConfig
public class GetSongsInPLaylist extends HttpServlet{

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

        String playlistId = request.getParameter("playlistId");

        String error = "";
        int id = -1;


        HttpSession s = request.getSession();


        User user = (User) s.getAttribute("user");


        if(playlistId == null || playlistId.isEmpty())
            error += "Playlist not defined;";


        if(error.equals("")) {

            PlaylistDAO pDao = new PlaylistDAO(connection);

            try {

                id = Integer.parseInt(playlistId);

                if(!pDao.findPlayListById(id, user.getId())) {
                    error += "PlayList doesn't exist";
                }

            }catch(NumberFormatException e) {
                error += "Playlist e/o section not defined;";
            }catch(SQLException e) {
                error += "Impossible comunicate with the data base;";
            }
        }

        if(!error.equals("")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
            response.getWriter().println(error);
            return;
        }

        SongDAO sDao = new SongDAO(connection);

        PlaylistDAO pDao = new PlaylistDAO(connection);


        try {

            ArrayList<Song> songsInPlaylist = sDao.getSongTitleAndImg(id);
            ArrayList<Integer> sorting = pDao.getSorting(id);


            JSONArray jArray = new JSONArray();
            JSONObject jSonObject;

            if(sorting != null) {

                for(Integer i : sorting) {
                    for(Song song : songsInPlaylist) {
                        if(song.getId() == i) {


                            jSonObject = new JSONObject();

                            jSonObject.put("songId", song.getId());
                            jSonObject.put("songTitle" , song.getSongTitle());
                            jSonObject.put("albumImage" , song.getImgFile());
                            try {
                                jSonObject.put("base64String" , GetEncoding.getImageEncoding(song.getImgFile() ,
                                        getServletContext() , connection , user));
                            } catch(IOException e) {
                                jSonObject.put("base64String" , "");
                            }

                            jArray.put(jSonObject);


                            songsInPlaylist.remove(song);
                            break;
                        }
                    }
                }

                if(songsInPlaylist.size() > 0) {
                    for(Song song : songsInPlaylist) {

                        jSonObject = new JSONObject();

                        jSonObject.put("songId", song.getId());
                        jSonObject.put("songTitle" , song.getSongTitle());
                        jSonObject.put("albumImage" , song.getImgFile());
                        try {
                            jSonObject.put("base64String" , GetEncoding.getImageEncoding(song.getImgFile() ,
                                    getServletContext() , connection , user));
                        } catch(IOException e) {
                            jSonObject.put("base64String" , "");
                        }

                        jArray.put(jSonObject);
                    }
                }
            }
            else {

                for(Song song : songsInPlaylist) {
                    System.out.println("Title: " + song.getSongTitle());


                    jSonObject = new JSONObject();

                    jSonObject.put("songId", song.getId());
                    jSonObject.put("songTitle" , song.getSongTitle());
                    jSonObject.put("albumImage" , song.getImgFile());

                    try {
                        jSonObject.put("base64String" , GetEncoding.getImageEncoding(song.getImgFile() ,
                                getServletContext() , connection , user));
                    } catch(IOException e) {
                        jSonObject.put("base64String" , "");
                    }

                    jArray.put(jSonObject);
                }
            }

            response.setStatus(HttpServletResponse.SC_OK);//Code 200
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(jArray);

        }catch(SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
            response.getWriter().println("Internal server error, retry later");
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
