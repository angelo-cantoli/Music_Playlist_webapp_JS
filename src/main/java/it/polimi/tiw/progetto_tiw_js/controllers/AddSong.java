package it.polimi.tiw.progetto_tiw_js.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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

@WebServlet("/AddSong")
@MultipartConfig
public class AddSong extends HttpServlet{

    private static final long serialVersionUID = 1L;
    private Connection connection;

    public void init(){
        try {
            connection = ConnectionHandler.getConnection(getServletContext());
        } catch (UnavailableException e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request , HttpServletResponse response)throws ServletException,IOException{

        String playlistId = StringEscapeUtils.escapeJava(request.getParameter("playlistId"));
        String[] selectedSongs = request.getParameterValues("selectedSongs");
        String error = "";
        int pId = -1;

        HttpSession s = request.getSession();
        User user = (User) s.getAttribute("user");

        if(playlistId == null || playlistId.isEmpty()) {
            error += "Missing playlist parameter";
        } else if(selectedSongs == null || selectedSongs.length == 0) {
            error += "No songs selected";
        }

        if(error.equals("")) {
            try {
                PlaylistDAO pDao = new PlaylistDAO(connection);
                SongDAO sDao = new SongDAO(connection);

                pId = Integer.parseInt(playlistId);

                if(!pDao.findPlayListById(pId, user.getId()))
                    error += "PlayList doesn't exist";
                else {
                    // Check all selected songs
                    for (String songIdStr : selectedSongs) {
                        int sId = Integer.parseInt(songIdStr);
                        if(!sDao.findSongByUser(sId, user.getId())) {
                            error += "One or more songs don't exist";
                            break;
                        }
                        if(pDao.findSongInPlaylist(pId, sId)) {
                            error += "One or more songs are already in this playlist";
                            break;
                        }
                    }
                }
            }catch(NumberFormatException e) {
                error += "Invalid parameters";
            }catch(SQLException e) {
                error += "Internal server error, retry later";
            }
        }

        if(!error.equals("")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
            response.getWriter().println(error);
            return;
        }

        PlaylistDAO pDao = new PlaylistDAO(connection);

        try {
            boolean allSuccess = true;
            
            // Add all selected songs
            for (String songIdStr : selectedSongs) {
                int sId = Integer.parseInt(songIdStr);
                boolean hasCustomOrder = pDao.hasCustomOrder(pId);
                boolean result;

                if (hasCustomOrder) {
                    result = pDao.addSongToCustomOrder(pId, sId);
                } else {
                    result = pDao.addSong(pId, sId);
                }
                
                if (!result) {
                    allSuccess = false;
                    break;
                }
            }

            if(allSuccess) {
                response.setStatus(HttpServletResponse.SC_OK);//Code 200
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
                response.getWriter().println("An error occurred while adding songs, retry later");
            }
        }catch(SQLException | NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
            response.getWriter().println("An error occurred with the db, retry later");
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
