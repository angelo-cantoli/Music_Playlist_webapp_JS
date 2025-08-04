package it.polimi.tiw.progetto_tiw_js.controllers;

import java.sql.Date;
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

import it.polimi.tiw.progetto_tiw_js.beans.User;
import it.polimi.tiw.progetto_tiw_js.dao.PlaylistDAO;
import it.polimi.tiw.progetto_tiw_js.dao.SongDAO;
import it.polimi.tiw.progetto_tiw_js.utils.ConnectionHandler;

@WebServlet("/CreatePlaylist")
@MultipartConfig
public class CreatePlaylist extends HttpServlet{

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
        doPost(request , response);
    }

    public void doPost(HttpServletRequest request , HttpServletResponse response)throws ServletException,IOException{
        String title = StringEscapeUtils.escapeJava(request.getParameter("name"));
        Date creationDate = new Date(System.currentTimeMillis());
        String[] selectedSongs = request.getParameterValues("selectedSongs");
        String error = "";

        HttpSession s = request.getSession();
        User user = (User) s.getAttribute("user");

        if(title == null || title.isEmpty())
            error += "Title is empty";
        else if(title.length() > 45)
            error += "Title is too long";

        if(!error.equals("")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
            response.getWriter().println(error);
            return;
        }

        PlaylistDAO pDao = new PlaylistDAO(connection);
        SongDAO sDao = new SongDAO(connection);

        try {
            connection.setAutoCommit(false);
            

            int playlistId = pDao.createPlaylist(title, creationDate, user.getId());

            if(playlistId >= 0) {

                if(selectedSongs != null && selectedSongs.length > 0) {
                    for(String songIdStr : selectedSongs) {
                        try {
                            int songId = Integer.parseInt(songIdStr);

                            if(sDao.findSongByUser(songId, user.getId())) {
                                pDao.addSong(playlistId, songId);
                            }
                        } catch(NumberFormatException e) {
                            continue;
                        }
                    }
                }
                connection.commit();
                response.setStatus(HttpServletResponse.SC_OK);//Code 200
            }
            else {
                connection.rollback();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
                response.getWriter().println("PlayList name already used or creation failed");
            }
        }catch(SQLException e) {
            try {
                connection.rollback();
            } catch(SQLException rollbackException) {

            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
            response.getWriter().println("Internal server error, retry later");
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch(SQLException e) {
                //log
            }
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
