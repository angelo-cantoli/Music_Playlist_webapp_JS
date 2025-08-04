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

import it.polimi.tiw.progetto_tiw_js.dao.UserDAO;
import it.polimi.tiw.progetto_tiw_js.utils.ConnectionHandler;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/Registration")
@MultipartConfig
public class Registration extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    public Registration() {
        super();
    }

    public void init() {
        ServletContext context = getServletContext();

        try {
            connection = ConnectionHandler.getConnection(context);
        } catch (UnavailableException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = StringEscapeUtils.escapeJava(request.getParameter("userReg"));
        String password = StringEscapeUtils.escapeJava(request.getParameter("passwordReg"));
        String firstName = StringEscapeUtils.escapeJava(request.getParameter("firstNameReg"));
        String lastName = StringEscapeUtils.escapeJava(request.getParameter("lastNameReg"));

        String error = "";
        boolean result = false;

        System.out.println("Server is processing the registration request");

        if (userName == null || password == null || firstName == null || lastName == null ||
                userName.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
            response.getWriter().println("Missing parameters;");
            return;
        }


        if (!(password.contains("0") || password.contains("1") || password.contains("2") || password.contains("3") || password.contains("4") || password.contains("5") || password.contains("6") || password.contains("7") || password.contains("8") || password.contains("9"))
                || !(password.contains("#") || password.contains("@") || password.contains("_")) || password.length() < 4)
            error += "Password has to contain at least:4 character,1 number and 1 of the following @,# and _ ;";

        if (userName.length() > 45)
            error += "UserName too long;";

        if (firstName.length() > 100)
            error += "First name too long;";


        if (lastName.length() > 100)
            error += "Last name too long;";

        if (password.length() > 45)
            error += "Password too long;";

        if (!error.equals("")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
            response.getWriter().println(error);
            return;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        UserDAO userDao = new UserDAO(connection);

        try {
            result = userDao.addUser(userName, hashedPassword, firstName, lastName);

            if (result == true) {
                response.setStatus(HttpServletResponse.SC_OK);//Code 200
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//Code 400
                response.getWriter().println("Username not availabe");
            }
        } catch (SQLException e) {
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