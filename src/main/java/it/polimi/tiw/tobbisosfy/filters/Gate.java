package it.polimi.tiw.tobbisosfy.filters;

import it.polimi.tiw.tobbisosfy.beans.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/Gate")
public class Gate implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String loginpath = req.getServletContext().getContextPath() + "/index.html";
        HttpSession s = req.getSession();

        if (s.isNew() || s.getAttribute("user")==null) {
            res.sendRedirect(loginpath);
            return;
        }
        System.out.println("Welcome user "+((User)s.getAttribute("user")).getUsername());
        filterChain.doFilter(request, response);
    }
}
