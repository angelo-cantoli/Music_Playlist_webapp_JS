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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.progetto_tiw_js.beans.Song;
import it.polimi.tiw.progetto_tiw_js.beans.User;
import it.polimi.tiw.progetto_tiw_js.dao.PlaylistDAO;
import it.polimi.tiw.progetto_tiw_js.dao.SongDAO;
import it.polimi.tiw.progetto_tiw_js.utils.ConnectionHandler;

@WebServlet("/GetSongsNotInPlaylist")
@MultipartConfig
public class GetSongsNotInPlaylist extends HttpServlet{

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

        try {

            ArrayList<Song> songsNotInPlaylist = sDao.getSongsNotInPlaylist(id , user.getId());

            Gson gSon = new GsonBuilder().create();
            String jSon = gSon.toJson(songsNotInPlaylist);

            response.setStatus(HttpServletResponse.SC_OK);//Code 200
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(jSon);

        }catch(SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
            response.getWriter().println("Internal server error, retry later");
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


