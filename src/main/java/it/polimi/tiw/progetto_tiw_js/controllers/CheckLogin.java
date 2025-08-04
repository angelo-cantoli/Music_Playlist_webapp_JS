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

import org.apache.commons.lang.StringEscapeUtils;
import com.google.gson.Gson;

import it.polimi.tiw.progetto_tiw_js.beans.User;
import it.polimi.tiw.progetto_tiw_js.dao.UserDAO;
import it.polimi.tiw.progetto_tiw_js.utils.ConnectionHandler;

@WebServlet("/CheckLogin")
@MultipartConfig
public class CheckLogin extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private Connection connection;

    public CheckLogin() {
        super();
    }

    public void init() throws ServletException {
        ServletContext context = getServletContext();

        try {
            connection = ConnectionHandler.getConnection(context);
        } catch (UnavailableException e) {
            throw new ServletException("Cannot get connection from context", e);
        }
    }

    public void doPost(HttpServletRequest request , HttpServletResponse response)throws ServletException,IOException{

        String userName = StringEscapeUtils.escapeJava(request.getParameter("user"));
        String password = StringEscapeUtils.escapeJava(request.getParameter("password"));


        if(userName == null || password == null || userName.isEmpty() || password.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
            response.getWriter().println("Username and password are required");
            return;
        }

        UserDAO userDao = new UserDAO(connection);
        User user = null;


        try {
            user = userDao.checkAuthentication(userName, password);
            if(user != null) {

                request.getSession().setAttribute("user", user);


                response.setStatus(HttpServletResponse.SC_OK);//Code 200
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");


                Gson gson = new Gson();
                String userJson = gson.toJson(new UserData(user.getUserName(),
                        user.getFirstName(),
                        user.getLastName()));
                response.getWriter().println(userJson);
            }else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//Code 401
                response.getWriter().println("Username and/or password are incorrect");
            }
        }catch(SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//Code 500
            response.getWriter().println("Internal server error, retry later");
        }
    }


    private class UserData {
        private String username;
        private String firstName;
        private String lastName;

        public UserData(String username, String firstName, String lastName) {
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
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
