package it.polimi.tiw.tobbisosfy.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class Gate implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException {
        System.out.println("Web entity approaching the gate...");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String loginpath = req.getServletContext().getContextPath() + "/index.html";
        HttpSession s = req.getSession();

        if (s.isNew() || s.getAttribute("user")==null) {
            res.sendRedirect(loginpath);
            return;
        }

        //filterChain.doFilter(request, response); per ora é inutile, non ci sono altri filtri
    }
}
