package org.mycode.rest;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.mycode.controller.SkillController;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Skill;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "SkillServlet", urlPatterns = "/api/v1/skills")
public class SkillServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(SkillServlet.class);
    private Gson gson;
    private SkillController skillController;
    public SkillServlet() throws RepoStorageException {
        gson = new Gson();
        skillController = SkillController.getInstance();
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
            if(req.getParameter("name")==null || req.getParameter("name").equals("")){
                log.warn("POST request gives invalid name parameter");
                resp.sendError(400, "Invalid parameter name");
            } else {
                skillController.request("c|0|"+req.getParameter("name"));
            }
        } catch (IncorrectRequestException e) {
            log.error("Incorrect request to controller", e);
            e.printStackTrace();
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET request to read");
        List<Skill> skills = new ArrayList<>();
        try {
            if(req.getParameter("id")==null || !req.getParameter("id").matches("\\d+")){
                log.debug("Request to get all");
                skills = skillController.request("g");
            } else {
                log.debug("Request to get by ID");
                skills = skillController.request("r|"+req.getParameter("id"));
            }
        } catch (IncorrectRequestException e) {
            log.error("Incorrect request to controller", e);
            e.printStackTrace();
        }
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.println(gson.toJson(skills));
        log.debug("Sand JSON response");
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("PUT request to update");
        try {
            if(req.getParameter("id")==null
                    || !req.getParameter("id").matches("\\d+")
                    || req.getParameter("name")==null
                    || req.getParameter("name").equals("")){
                log.warn("PUT request gives invalid id, name parameters");
                resp.sendError(400, "Invalid parameters id, name");
            } else {
                skillController.request("u|"+req.getParameter("id")+"|"+req.getParameter("name"));
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
                skillController.request("d|"+req.getParameter("id"));
            }
        } catch (IncorrectRequestException e) {
            log.error("Incorrect request to controller", e);
            e.printStackTrace();
        }
    }
}
