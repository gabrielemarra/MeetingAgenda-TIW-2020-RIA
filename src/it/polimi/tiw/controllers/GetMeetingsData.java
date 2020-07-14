package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import it.polimi.tiw.aux.ConnectionManager;
import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.MeetingsDAO;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/GetMeetingsData")
public class GetMeetingsData extends HttpServlet {

    private Connection connection = null;

    public GetMeetingsData() {
        super();
    }

    public void init() throws ServletException {
        try {
            connection = ConnectionManager.tryConnection(getServletContext());
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Can't load database driver");
        } catch (SQLException e) {
            throw new UnavailableException("Couldn't get db connection");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //get meetings created by the user
        Integer userId = null;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");


        List<Meeting> meetings;
        try {
            meetings = new MeetingsDAO(connection).getMeetingListWithThisUserAsParticipant(user.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Not possible to recover invited at meetings");
            return;
        }

        // Redirect to the the webapp page and add meetingsto the parameters

        Gson gson = new Gson();
        String json = gson.toJson(meetings);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
