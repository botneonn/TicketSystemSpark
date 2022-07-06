/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.p.ticketsystemspark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static spark.Spark.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.Map;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;

/**
 *
 * @author Praktikant await fetch("http://localhost:8080/createuser", { "body":
 * "{\n\"Username\":\"a\",\n\"Password\":\"a\",\n\"Email\":\"a@a.aaa\",\n\"Phonenumber\":\"aaaaa\"\n}",
 * "method": "POST", });
 */
//DELETE FROM d1.employees WHERE idEmployees=3;
public class Main {

    public static void main(String[] args) throws SQLException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        port(8080);
        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        String sqlSelectAllEmployees = "SELECT * FROM d1.employees";
        String sqlSelectAllusers = "SELECT * FROM d1.users";
        before((request, response) -> {

        });
        after((request, response) -> {
            response.header("Content-Encoding", "gzip");
        });

        var templatingengine = new HandlebarsTemplateEngine();
        get("/", (rq, rs) -> {
            Map map = new HashMap();
            var session = rq.session();
            var uid = session.attribute("UserID");
            if (uid != null) {
                //user signed in, redirect to dash   
                  map.put("title", "Dashboard - TSS");
                  return new ModelAndView(map, "userdash.hbs");
            } else {
                var aid = session.attribute("AgentID");
                if (aid != null) {
                    map.put("title", "Dashboard - TSS");
                    return new ModelAndView(map, "agentdash.hbs");
                } else {
                    map.put("title", "Home page - TSS");
                    return new ModelAndView(map, "rootnotsignedin.hbs");
                }
            }
        }, templatingengine);
       get("/usersignin", (rq, rs) -> {
            Map map = new HashMap();
            var session = rq.session();
            var uid = session.attribute("UserID");
            if (uid != null) {
                rs.redirect("/");
                return null;
            } 
            map.put("title", "Sign in - TSS");
            return new ModelAndView(map, "usersignin.hbs");
        }, templatingengine);
       post("/usersignin", (rq, rs) -> {
            String inputbody = rq.body();
            System.out.println(inputbody);
            var session = rq.session();
            LoginFormContent t = gson.fromJson(inputbody, LoginFormContent.class);
            var user = User.GetFromUserName(t.username);
            var salt = user.GetUserSalt();
            if (SaltUtils.isExpectedPassword(t.password.toCharArray(), salt.Salt, user.UserPassword)) 
                {
                    session.attribute("UserID",user.UserID);
                     rs.redirect("/");
                     return null;
                }
                
            Map map = new HashMap();
            map.put("title", "Sign in - TSS");
            return new ModelAndView(map, "usersignin.hbs");
       }, templatingengine);
        get("/getemployees", (Request request, Response response) -> {
            List<Employee> Employees = new ArrayList<>();
            try ( Connection conn = DatabaseConnectionCreator.GetConnection();  PreparedStatement ps = conn.prepareStatement(sqlSelectAllEmployees);  ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employees.add(Employee.GetFromRs(rs));
                }
            } catch (SQLException e) {
                throw e;
            }
            response.type("text/json");
            return gson.toJson(Employees);
        });
        get("/getusers", (Request request, Response response) -> {
            List<User> Users = new ArrayList<>();
            try ( Connection conn = DatabaseConnectionCreator.GetConnection();  PreparedStatement ps = conn.prepareStatement(sqlSelectAllusers);  ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Users.add(User.GetFromRs(rs));
                }
            } catch (SQLException e) {
                throw e;
            }
            response.type("text/json");
            return gson.toJson(Users);
        });

//{
//"Username": "",
//"Password": "",
//"TicketContent": "",
//"TicketCategory": ""
//}
        post("/sendticket", (Request request, Response response) -> {
            String inputbody = request.body();
            TicketRequestBodyContent t = gson.fromJson(inputbody, TicketRequestBodyContent.class);
            var user = User.GetFromUserName(t.Username);
            var salt = user.GetUserSalt();
            if (SaltUtils.isExpectedPassword(t.Password.toCharArray(), salt.Salt, user.UserPassword)) {
                Ticket tck = new Ticket();
                tck.TicketCategory = t.TicketCategory;
                tck.TicketContent = t.TicketContent;
                tck.UserId = user.UserID;
                tck.TicketTitle = t.TicketTitle;
                var conn = DatabaseConnectionCreator.GetConnection();
                var ps = conn.prepareStatement("INSERT INTO `d1`.`tickets` (`TicketContent`, `UserId`, `TicketCategory`,`TicketTitle`) VALUES (?, ?, ?,?);");
                ps.setString(1, tck.TicketContent);
                ps.setInt(2, tck.UserId);
                ps.setString(3, tck.TicketCategory);
                ps.setString(4, tck.TicketTitle);
                response.type("text/json");
                ps.execute();
                return gson.toJson(42);

            }
            response.status(400);
            return "bad username or password";
        });

//{
//"Username":"",
//"Password":"",
//"Email":"",
//"Phonenumber":""
//} 
        post("/createuser", (Request request, Response response) -> {
            String inputbody = request.body();
            CreateUserBodyContent t = gson.fromJson(inputbody, CreateUserBodyContent.class);
            response.type("text/json");
            UserCreatedResponse r = new UserCreatedResponse();
            var i = User.CreateUser(t.Username, t.Password, t.Email, t.Phonenumber).UserID;
            r.info = String.valueOf(i);
            r.success = true;
            return gson.toJson(r);
        });

    }
}
