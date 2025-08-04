package it.polimi.tiw.progetto_tiw_js.controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/Logout")
public class Logout extends HttpServlet{
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);


        String requestedWith = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestedWith)) {

            response.setContentType("application/json");
            response.getWriter().write("{\"logout\": true}");
        } else {

            String path = getServletContext().getContextPath() +  "HTML/Login.html";
            response.sendRedirect(path);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        doGet(request , response);
    }

}
