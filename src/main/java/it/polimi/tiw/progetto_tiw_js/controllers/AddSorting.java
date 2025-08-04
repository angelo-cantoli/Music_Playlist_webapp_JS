package it.polimi.tiw.progetto_tiw_js.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.progetto_tiw_js.utils.Deserializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.progetto_tiw_js.beans.User;
import it.polimi.tiw.progetto_tiw_js.dao.PlaylistDAO;
import it.polimi.tiw.progetto_tiw_js.dao.SongDAO;
import it.polimi.tiw.progetto_tiw_js.utils.ConnectionHandler;


@WebServlet("/AddSorting")
@MultipartConfig
public class AddSorting extends HttpServlet {

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
        String playlistId = StringEscapeUtils.escapeJava(request.getParameter("playlistId"));
        int pId = -1;


        if(playlistId == null || playlistId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
            response.getWriter().println("PlayList not specified");
            return;
        }

        try {
            pId = Integer.parseInt(playlistId);
        }catch(NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
            response.getWriter().println("PlayList not specified");
            return;
        }

        // Verifica che la playlist appartenga all'utente
        User user = (User) request.getSession().getAttribute("user");
        PlaylistDAO pDao = new PlaylistDAO(connection);

        try {
            if (!pDao.findPlayListById(pId, user.getId())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("You don't have access to this playlist");
                return;
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Database error");
            return;
        }

        StringBuffer jb = new StringBuffer();
        String line = null;
        //Read the body of the request
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
            response.getWriter().println("Error reading the request body, retry later");
            return;
        }

        //Create the jSon with the sorting
        Gson gSon = new GsonBuilder().create();
        String newSorting = gSon.toJson(jb);

        if(newSorting == null || newSorting.length() <= 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
            response.getWriter().println("Add more songs to order you playlist!");
            return;
        }

        //Convert the String array in an arrayList of integer in order to make some checks
        ArrayList<Integer> sortedArray = Deserializer.fromJsonToArrayList(newSorting);
        SongDAO sDao = new SongDAO(connection);

        // Verifica se ci sono brani nella playlist
        if(sortedArray.size() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("No songs in the playlist to reorder");
            return;
        }

        int currentNumber = -1;


        for(int i = 0 ; i < sortedArray.size() ; i++) {
            currentNumber = sortedArray.get(i);
            for(int j = i + 1 ; j < sortedArray.size() ; j++) {
                if(sortedArray.get(j) == currentNumber) {
                    sortedArray.remove(j);
                    j--; // Aggiusta l'indice dopo la rimozione
                }
            }
        }


        ArrayList<Integer> invalidSongs = new ArrayList<>();
        for(Integer id : sortedArray) {
            try {
                if(!sDao.findSongByUser(id, user.getId()) || !pDao.findSongInPlaylist(pId, id)){
                    //Delete this id -> it doesn't belong to this user or isn't in playlist
                    invalidSongs.add(id);
                }
            }catch(SQLException e) {
                invalidSongs.add(id);
            }
        }


        for(Integer id : invalidSongs) {
            sortedArray.remove(id);
        }


        String updatedSorting = gSon.toJson(sortedArray);

        try {
            connection.setAutoCommit(false);

            boolean sortResult = pDao.addSorting(pId, updatedSorting);
            boolean customOrderResult = pDao.setCustomOrder(pId, true);

            if(sortResult && customOrderResult) {
                connection.commit();
                response.setStatus(HttpServletResponse.SC_OK); // Codice 200
            }else {
                connection.rollback();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
                response.getWriter().println("Internal server error, retry later");
            }
        }catch(SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                // Log dell'errore di rollback, se necessario
                e1.printStackTrace();
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
            response.getWriter().println("Internal server error, retry later");
        }finally {

            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
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
