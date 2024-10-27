package y_lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import y_lab.domain.Habit;
import y_lab.dto.HabitRequestDto;
import y_lab.mapper.HabitMapper;
import y_lab.mapper.HabitMapperImpl;
import y_lab.out.audit.AuditAction;
import y_lab.out.audit.LogExecutionTime;
import y_lab.service.HabitService;
import y_lab.service.serviceImpl.HabitServiceImpl;
import y_lab.util.DtoValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * The {@code HabitController} class is a servlet that handles HTTP requests for managing habits.
 * It provides endpoints for retrieving, creating, updating, and deleting habits.
 * This class uses the {@link HabitService} to perform operations on habit data and the {@link HabitMapper}
 * to convert between data transfer objects and domain objects.
 */
@WebServlet(urlPatterns = {"/api/habit/all", "/api/habit/*"})
public class HabitController extends HttpServlet {
    private HabitService habitService;
    private final HabitMapper habitMapper = new HabitMapperImpl();

    /**
     * Initializes the servlet and retrieves the {@link HabitService} from the servlet context.
     *
     * @throws ServletException if the {@link HabitService} is not initialized in the servlet context.
     */
    @Override
    public void init() throws ServletException {
        habitService = (HabitServiceImpl) getServletContext().getAttribute("habitService");

        if (habitService == null) {
            throw new ServletException("HabitService is not initialized");
        }
    }

    /**
     * Handles HTTP GET requests to retrieve habits.
     * If the request path is "/api/habit/all", it retrieves all habits for a given user and optional filter.
     * If the request path is "/api/habit/{name}", it retrieves a specific habit by its name for a given user.
     *
     * @param req  the HttpServletRequest object that contains the request the client made to the servlet
     * @param resp the HttpServletResponse object that the servlet uses to return the response to the client
     * @throws ServletException if an error occurs during the processing of the request
     * @throws IOException      if an input or output error occurs while the servlet is handling the request
     */
    @Override
    @LogExecutionTime
    @AuditAction(action = "Attempt to retrieve habits")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if ("/api/habit/all".equals(req.getServletPath())) {
            Long userId = Long.parseLong(req.getParameter("userId"));
            String filter = req.getParameter("filter");

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(
                    habitMapper.habitsToHabitResponseDtos(
                            habitService.getHabits(userId, filter)));

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(jsonResponse);
        } else if (req.getParameter("userId").matches("\\d+")) {
            Long userId = Long.parseLong(req.getParameter("userId"));
            String name = req.getParameter("name");

            Optional<Habit> habit = habitService.getHabit(name, userId);
            if (habit.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Habit not found");
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(
                        habitMapper.habitToHabitResponseDto(habit.get()));
                resp.getWriter().write(jsonResponse);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Path not found");
        }
    }

    /**
     * Handles HTTP POST requests to create a new habit.
     * Expects a JSON body containing the habit details.
     *
     * @param req  the HttpServletRequest object that contains the request the client made to the servlet
     * @param resp the HttpServletResponse object that the servlet uses to return the response to the client
     * @throws ServletException if an error occurs during the processing of the request
     * @throws IOException      if an input or output error occurs while the servlet is handling the request
     */
    @Override
    @LogExecutionTime
    @AuditAction(action = "Attempt to create a habit")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long userId = Long.parseLong(req.getParameter("userId"));

        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String json = sb.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        HabitRequestDto habitRequestDto = objectMapper.readValue(json, HabitRequestDto.class);

        try {
            DtoValidator.validate(habitRequestDto);

            Long habitId = habitService.createHabit(userId, habitMapper.habitRequestDtoToHabit(habitRequestDto));

            if (habitId != -1L) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                PrintWriter out = resp.getWriter();
                out.print(habitId);
                out.flush();
            } else {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "Habit with such name already exists");
            }

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Handles HTTP PUT requests to update an existing habit.
     * Expects a JSON body containing the updated habit details.
     *
     * @param req  the HttpServletRequest object that contains the request the client made to the servlet
     * @param resp the HttpServletResponse object that the servlet uses to return the response to the client
     * @throws ServletException if an error occurs during the processing of the request
     * @throws IOException      if an input or output error occurs while the servlet is handling the request
     */
    @Override
    @LogExecutionTime
    @AuditAction(action = "Attempt to update a habit")
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long habitId = Long.parseLong(req.getParameter("habitId"));

        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String json = sb.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        HabitRequestDto habitRequestDto = objectMapper.readValue(json, HabitRequestDto.class);

        try {
            DtoValidator.validate(habitRequestDto);

            boolean updated = habitService.updateHabit(habitId, habitMapper.habitRequestDtoToHabit(habitRequestDto));

            if (updated) {
                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = resp.getWriter();
                out.print(true);
                out.flush();
            } else {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "Habit was not updated");
            }

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Handles HTTP DELETE requests to delete a habit.
     *
     * @param req  the HttpServletRequest object that contains the request the client made to the servlet
     * @param resp the HttpServletResponse object that the servlet uses to return the response to the client
     * @throws ServletException if an error occurs during the processing of the request
     * @throws IOException      if an input or output error occurs while the servlet is handling the request
     */
    @Override
    @LogExecutionTime
    @AuditAction(action = "Attempt to delete a habit")
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long habitId = Long.parseLong(req.getParameter("habitId"));

        boolean deleted = habitService.deleteHabit(habitId);

        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = resp.getWriter();
            out.print(true);
            out.flush();
        } else {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Nothing deleted");
        }
    }
}
