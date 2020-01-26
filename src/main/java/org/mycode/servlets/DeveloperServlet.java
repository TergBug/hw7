package org.mycode.servlets;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.mycode.controller.DeveloperController;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Developer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "DeveloperServlet", urlPatterns = "/api/v1/developers")
public class DeveloperServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(DeveloperServlet.class);
    private Gson gson;
    private DeveloperController developerController;
    public DeveloperServlet() throws RepoStorageException {
        gson = new Gson();
        developerController = DeveloperController.getInstance();
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
            if(req.getParameter("firstName")==null
                    || req.getParameter("firstName").equals("")
                    || req.getParameter("lastName")==null
                    || req.getParameter("lastName").equals("")
                    || req.getParameter("skills")==null
                    || !req.getParameter("skills").matches("(\\d+,?)*")
                    || req.getParameter("account")==null
                    || !req.getParameter("account").matches("\\d+")){
                log.warn("POST request gives invalid firstName, lastName, skills, account parameters");
                resp.sendError(400, "Invalid parameter firstName, lastName, skills, account");
            } else {
                developerController.request("c|0|"+
                        req.getParameter("firstName")+"|"+
                        req.getParameter("lastName")+"|"+
                        req.getParameter("skills")+"|"+
                        req.getParameter("account"));
            }
        } catch (IncorrectRequestException e) {
            log.error("Incorrect request to controller", e);
            e.printStackTrace();
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET request to read");
        List<Developer> developers = new ArrayList<>();
        try {
            if(req.getParameter("id")==null || !req.getParameter("id").matches("\\d+")){
                log.debug("Request to get all");
                developers = developerController.request("g");
            } else {
                log.debug("Request to get by ID");
                developers = developerController.request("r|"+req.getParameter("id"));
            }
        } catch (IncorrectRequestException e) {
            log.error("Incorrect request to controller", e);
            e.printStackTrace();
        }
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.println(gson.toJson(developers));
        log.debug("Sand JSON response");
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("PUT request to update");
        try {
            if(req.getParameter("id")==null
                    || !req.getParameter("id").matches("\\d+")
                    || req.getParameter("firstName")==null
                    || req.getParameter("firstName").equals("")
                    || req.getParameter("lastName")==null
                    || req.getParameter("lastName").equals("")
                    || req.getParameter("skills")==null
                    || !req.getParameter("skills").matches("(\\d+,?)*")
                    || req.getParameter("account")==null
                    || !req.getParameter("account").matches("\\d+")){
                log.warn("PUT request gives invalid id, firstName, lastName, skills, account parameters");
                resp.sendError(400, "Invalid parameters id, firstName, lastName, skills, account");
            } else {
                developerController.request("u|"+
                        req.getParameter("id")+"|"+
                        req.getParameter("firstName")+"|"+
                        req.getParameter("lastName")+"|"+
                        req.getParameter("skills")+"|"+
                        req.getParameter("account"));
            }
        } catch (IncorrectRequestException e) {
            log.error("Incorrect request to controller", e);
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
                developerController.request("d|"+req.getParameter("id"));
            }
        } catch (IncorrectRequestException e) {
            log.error("Incorrect request to controller", e);
            e.printStackTrace();
        }
    }
}