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
import y_lab.service.serviceImpl.HabitServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet(urlPatterns ={"/api/habit/all", "/api/habit/*"})
public class HabitController extends HttpServlet {
    private HabitServiceImpl habitService;
    private HabitMapper habitMapper;

    @Override
    public void init() throws ServletException {
        habitService = (HabitServiceImpl) getServletContext().getAttribute("habitService");
        habitMapper = new HabitMapperImpl();

        if (habitService == null) {
            throw new ServletException("HabitService не инициализированы");
        }
    }

    @Override
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

            // Отправляем JSON-ответ
            resp.getWriter().write(jsonResponse);
        } else if (req.getParameter("userId").matches("\\d+")) { // Проверка, что userId содержит Id
            Long userId = Long.parseLong(req.getParameter("userId"));
            String name = req.getParameter("name");

            Optional<Habit> habit = habitService.getHabit(name, userId);
            if (habit.isEmpty())
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Habit not found");
            else {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(
                        habitMapper.habitToHabitResponseDto(
                                habit.get()));
                resp.getWriter().write(jsonResponse);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Path not found");
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long userId = Long.parseLong(req.getParameter("userId"));

        // Чтение JSON из тела запроса
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        // Преобразование JSON в объект Habit
        String json = sb.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        HabitRequestDto habitRequestDto = objectMapper.readValue(json, HabitRequestDto.class);

        Long habitId = habitService.createHabit(userId, habitMapper.habitRequestDtoToHabit(habitRequestDto));

        if (habitId != -1L) {
            resp.setStatus(HttpServletResponse.SC_CREATED); //201 Created
            PrintWriter out = resp.getWriter();
            out.print(habitId); // Отправляем id
            out.flush();
        } else {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Habit with such name is exist");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long habitId = Long.parseLong(req.getParameter("habitId"));

        // Чтение JSON из тела запроса
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        // Преобразование JSON в объект Habit
        String json = sb.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        HabitRequestDto habitRequestDto = objectMapper.readValue(json, HabitRequestDto.class);


        boolean upd = habitService.updateHabit(habitId, habitMapper.habitRequestDtoToHabit(habitRequestDto));

        if (upd) {
            resp.setStatus(HttpServletResponse.SC_OK); //200 ok
            PrintWriter out = resp.getWriter();
            out.print(true);
            out.flush();
        } else {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Habit was not update");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long habitId = Long.parseLong(req.getParameter("habitId"));

        boolean upd = habitService.deleteHabit(habitId);

        if (upd) {
            resp.setStatus(HttpServletResponse.SC_OK); //200 ok
            PrintWriter out = resp.getWriter();
            out.print(true);
            out.flush();
        } else {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Nothing delete");
        }
    }
}
