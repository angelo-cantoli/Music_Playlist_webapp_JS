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

import it.polimi.tiw.progetto_tiw_js.beans.Playlist;
import it.polimi.tiw.progetto_tiw_js.beans.User;
import it.polimi.tiw.progetto_tiw_js.dao.PlaylistDAO;
import it.polimi.tiw.progetto_tiw_js.utils.ConnectionHandler;

@WebServlet("/GetPlaylistList")
@MultipartConfig
public class GetPlaylistList extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection;

    public void init() throws ServletException {
        ServletContext context = getServletContext();

        try {
            connection = ConnectionHandler.getConnection(context);
        } catch (UnavailableException e) {
            throw new ServletException("Cannot get connection from context", e);
        }
    }

    public void doGet(HttpServletRequest request , HttpServletResponse response)throws ServletException,IOException{
        HttpSession s = request.getSession();
        User user = (User) s.getAttribute("user");
        ArrayList<Playlist> playlists = null;


        PlaylistDAO pDao = new PlaylistDAO(connection);


        try {
            playlists = pDao.findPlaylist(user.getId());
        }catch(SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
            response.getWriter().println("Internal server error, retry later");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);//Code 200

        Gson gSon = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
        String jSon = gSon.toJson(playlists);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jSon);
    }

    public void doPost(HttpServletRequest request , HttpServletResponse response)throws ServletException,IOException{
        doGet(request , response);
    }


    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

