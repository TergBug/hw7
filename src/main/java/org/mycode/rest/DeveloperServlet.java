package org.mycode.rest;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Developer;
import org.mycode.service.DeveloperService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "DeveloperServlet", urlPatterns = "/api/v1/developers")
public class DeveloperServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(DeveloperServlet.class);
    private Gson gson;
    private DeveloperService developerService;
    public DeveloperServlet() throws RepoStorageException {
        gson = new Gson();
        developerService = new DeveloperService();
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getParameter("type")!=null){
            log.debug("POST request with type parameter");
            switch (req.getParameter("type")){
                case "create":
                    break;
                case "read":
                    this.doGet(req, resp);
                    return;
                case "update":
                    this.doPut(req, resp);
                    return;
                case "delete":
                    this.doDelete(req, resp);
                    return;
                default:
                    log.warn("POST request gives invalid type parameter");
                    resp.sendError(400, "Invalid parameter type");
                    return;
            }
        }
        log.debug("POST request to create");
        try {
            developerService.create(gson.fromJson(req.getReader(), Developer.class));
        } catch (Exception e) {
            log.error("Cannot create entry", e);
            e.printStackTrace();
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET request to read");
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        try {
            if(req.getParameter("id")==null || !req.getParameter("id").matches("\\d+")){
                log.debug("Request to get all");
                writer.println(gson.toJson(developerService.getAll()));
                log.debug("Sand JSON response");
            } else {
                log.debug("Request to get by ID");
                writer.println(gson.toJson(developerService.getById(Long.parseLong(req.getParameter("id")))));
                log.debug("Sand JSON response");
            }
        } catch (Exception e) {
            log.error("Cannot read entry", e);
            e.printStackTrace();
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("PUT request to update");
        try {
            developerService.update(gson.fromJson(req.getReader(), Developer.class));
        } catch (Exception e) {
            log.error("Cannot update entry", e);
            e.printStackTrace();
        }
    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("DELETE request to delete");
        try {
            if(req.getParameter("id")==null || !req.getParameter("id").matches("\\d+")){
                log.warn("DELETE request gives invalid id parameter");
                resp.sendError(400, "Invalid parameter id");
            } else {
                developerService.delete(Long.parseLong(req.getParameter("id")));
            }
        } catch (Exception e) {
            log.error("Cannot delete entry", e);
            e.printStackTrace();
        }
    }
}