package it.polimi.tiw.progetto_tiw_js.controllers;

import com.google.gson.Gson;
import it.polimi.tiw.progetto_tiw_js.beans.Song;
import it.polimi.tiw.progetto_tiw_js.beans.User;
import it.polimi.tiw.progetto_tiw_js.dao.SongDAO;
import it.polimi.tiw.progetto_tiw_js.utils.ConnectionHandler;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/GetUserSongsServlet")
public class GetUserSongsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        try {
            connection = ConnectionHandler.getConnection(servletContext);
        } catch (UnavailableException e) {
            throw new ServletException("Cannot get connection", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("User not logged in.");
            return;
        }

        SongDAO songDAO = new SongDAO(connection);
        List<Song> songs;
        try {
            songs = songDAO.getSongsByUserIdOrdered(user.getId());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error retrieving songs from the database.");
            e.printStackTrace();
            return;
        }

        String jsonSongs = new Gson().toJson(songs);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonSongs);
    }

    public void destroy() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
