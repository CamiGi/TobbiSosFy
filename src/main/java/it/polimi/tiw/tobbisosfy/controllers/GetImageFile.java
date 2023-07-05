package it.polimi.tiw.tobbisosfy.controllers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet("/GetImageFile")
public class GetImageFile extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public GetImageFile() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = getServletContext().getInitParameter("imagepath");
        String fileName = request.getParameter("name");
        File image;

        if (fileName == null || fileName.equals("")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST, "Missing file name");
            return;
        }

        image = new File(path, fileName);

        if (!image.exists() || image.isDirectory()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        Files.copy(image.toPath(), response.getOutputStream());
    }
}
