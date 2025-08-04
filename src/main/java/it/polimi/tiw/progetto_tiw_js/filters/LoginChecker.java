package it.polimi.tiw.progetto_tiw_js.filters;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginChecker implements Filter {


    public void init(FilterConfig fConfig) throws ServletException {
        // TODO Auto-generated method stub
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession s = req.getSession();
        if (s.isNew() || s.getAttribute("user") == null) {
            // Check if this is an AJAX request
            String requestedWith = req.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestedWith)) {
                // For AJAX requests, return 401 status so JavaScript can handle it
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                // For direct page access (like browser back button), redirect to login
                String contextPath = req.getContextPath();
                String loginPath = contextPath + "/HTML/Login.html";
                res.sendRedirect(loginPath);
            }
            return;
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
        // TODO Auto-generated method stub
    }

}