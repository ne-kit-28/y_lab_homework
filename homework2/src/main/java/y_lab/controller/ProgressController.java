package y_lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import y_lab.domain.Habit;
import y_lab.out.audit.AuditAction;
import y_lab.out.audit.LogExecutionTime;
import y_lab.service.HabitService;
import y_lab.service.ProgressService;
import y_lab.service.serviceImpl.HabitServiceImpl;
import y_lab.service.serviceImpl.ProgressServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * Controller for managing progress related to habits.
 * <p>
 * This controller handles HTTP requests related to habit progress, including
 * retrieving progress information and marking habits as completed.
 * </p>
 */
@WebServlet(urlPatterns = {
        "/api/progress/create/*",
        "/api/progress/*"
})
public class ProgressController extends HttpServlet {

    private ProgressService progressService;
    private HabitService habitService;

    @Override
    public void init() throws ServletException {
        progressService = (ProgressServiceImpl) getServletContext().getAttribute("progressService");
        habitService = (HabitServiceImpl) getServletContext().getAttribute("habitService");
    }

    /**
     * Handles GET requests to retrieve progress information for a specific habit.
     *
     * @param req  the HttpServletRequest object representing the client's request
     * @param resp the HttpServletResponse object representing the server's response
     * @throws ServletException if an error occurs while processing the request
     * @throws IOException      if an error occurs while writing to the response
     */
    @Override
    @LogExecutionTime
    @AuditAction(action = "Attempt to retrieve progress information")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            long habitId = Long.parseLong(req.getParameter("habitId"));

            Optional<Habit> habit = habitService.getHabit(habitId);
            if (habit.isEmpty() || !(habit.get().getUserId() == Long.parseLong(req.getParameter("userId")))) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Habit not found");
                return;
            }

            if ("/api/progress".equals(req.getServletPath())) {

                String message;

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
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Type is incorrect");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Path not found");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid habitId");
        }
    }

    /**
     * Handles POST requests to mark a habit as completed.
     *
     * @param req  the HttpServletRequest object representing the client's request
     * @param resp the HttpServletResponse object representing the server's response
     * @throws ServletException if an error occurs while processing the request
     * @throws IOException      if an error occurs while writing to the response
     */
    @Override
    @LogExecutionTime
    @AuditAction(action = "Attempt to mark habit as completed")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if ("/api/progress/create".equals(req.getServletPath())) {
            try {
                if (progressService.createProgress(Long.parseLong(req.getParameter("habitId")))) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Habit with such id not found");
                }
            } catch (NumberFormatException ex) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Id is incorrect");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Path not found");
        }
    }
}
