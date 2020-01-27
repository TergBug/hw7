package org.mycode.rest;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.mycode.controller.AccountController;
import org.mycode.exceptions.IncorrectRequestException;
import org.mycode.exceptions.RepoStorageException;
import org.mycode.model.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AccountServlet", urlPatterns = "/api/v1/accounts")
public class AccountServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AccountServlet.class);
    private Gson gson;
    private AccountController accountController;
    public AccountServlet() throws RepoStorageException {
        gson = new Gson();
        accountController = AccountController.getInstance();
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
            if(req.getParameter("name")==null
                    || req.getParameter("name").equals("")
                    || req.getParameter("status")==null
                    || !req.getParameter("status").matches("(a)|(b)|(d)")){
                log.warn("POST request gives invalid name, status parameters");
                resp.sendError(400, "Invalid parameter name, status");
            } else {
                accountController.request("c|0|"+req.getParameter("name")+"|"+req.getParameter("status"));
            }
        } catch (IncorrectRequestException e) {
            log.error("Incorrect request to controller", e);
            e.printStackTrace();
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET request to read");
        List<Account> accounts = new ArrayList<>();
        try {
            if(req.getParameter("id")==null || !req.getParameter("id").matches("\\d+")){
                log.debug("Request to get all");
                accounts = accountController.request("g");
            } else {
                log.debug("Request to get by ID");
                accounts = accountController.request("r|"+req.getParameter("id"));
            }
        } catch (IncorrectRequestException e) {
            log.error("Incorrect request to controller", e);
            e.printStackTrace();
        }
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.println(gson.toJson(accounts));
        log.debug("Sand JSON response");
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("PUT request to update");
        try {
            if(req.getParameter("id")==null
                    || !req.getParameter("id").matches("\\d+")
                    || req.getParameter("name")==null
                    || req.getParameter("name").equals("")
                    || req.getParameter("status")==null
                    || !req.getParameter("status").matches("(a)|(b)|(d)")){
                log.warn("PUT request gives invalid id, name, status parameters");
                resp.sendError(400, "Invalid parameters id, name, status");
            } else {
                accountController.request("u|"+
                        req.getParameter("id")+"|"+
                        req.getParameter("name")+"|"+
                        req.getParameter("status"));
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
                accountController.request("d|"+req.getParameter("id"));
            }
        } catch (IncorrectRequestException e) {
            log.error("Incorrect request to controller", e);
            e.printStackTrace();
        }
    }
}
