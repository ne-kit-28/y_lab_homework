package y_lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import y_lab.service.ProgressService;
import y_lab.service.serviceImpl.ProgressServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns =
        {
                "/api/progress/create/*"
                , "/api/progress/*"
        })
public class ProgressController extends HttpServlet {

    ProgressService progressService;

    @Override
    public void init() throws ServletException {
        progressService = (ProgressServiceImpl) getServletContext().getAttribute("progressService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if ("/api/progress".equals(req.getServletPath())) {

            String message;

            long habitId = Long.parseLong(req.getParameter("habitId"));

            if (req.getParameter("type").equals("streak")) {
                message = progressService.calculateStreak(habitId);

                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = resp.getWriter();

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(message);

                out.print(jsonResponse);
                out.flush();
            } else if (req.getParameter("type").equals("statistic")) {

                String period = req.getParameter("period");

                message = progressService.generateProgressStatistics(habitId, period);

                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = resp.getWriter();

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(message);

                out.print(jsonResponse);
                out.flush();
            } else if (req.getParameter("type").equals("report")) {
                String period = req.getParameter("period");

                message = progressService.generateReport(habitId, period);

                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = resp.getWriter();

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(message);

                out.print(jsonResponse);
                out.flush();
            } else
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "type is incorrect");
        } else
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Path not found");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if ("/api/progress/create".equals(req.getServletPath())) {
            try {
                if (progressService.createProgress(Long.parseLong(req.getParameter("habitId"))))
                    resp.setStatus(HttpServletResponse.SC_OK);
                else
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Habit with such id not found");
            } catch (NumberFormatException ex) {
                resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "id is incorrect");
            }
        } else
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Path not found");
    }
}
