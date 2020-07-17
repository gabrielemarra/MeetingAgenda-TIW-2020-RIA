
package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import it.polimi.tiw.beans.TempMeeting;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.InvitationDAO;
import it.polimi.tiw.dao.TempMeetingDAO;
import it.polimi.tiw.dao.UsersDAO;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@WebServlet("/GetInvitationList")
@MultipartConfig
public class GetInvitationList extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
        try {
            ServletContext context = getServletContext();
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);

        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Can't load database driver");
        } catch (SQLException e) {
            throw new UnavailableException("Couldn't get db connection");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        UsersDAO usersDAO = new UsersDAO(connection);

        try {
            List<User> allAvailableUsers = usersDAO.getUsersToInvite(user.getId()); //all the available users

            Gson gson = new Gson();
            String json = gson.toJson(allAvailableUsers);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        } catch (SQLException e) {
            //todo
        }
    }
}