package y_lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import y_lab.domain.User;
import y_lab.dto.UserRequestDto;
import y_lab.mapper.UserMapper;
import y_lab.mapper.UserMapperImpl;
import y_lab.service.serviceImpl.UserServiceImpl;
import y_lab.util.EmailValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet(urlPatterns = {"/api/user/all", "/api/user/*"})
public class UserController extends HttpServlet {

    UserServiceImpl userService;
    UserMapper userMapper;

    @Override
    public void init() throws ServletException {
        userMapper = new UserMapperImpl();
        userService = (UserServiceImpl) getServletContext().getAttribute("userService");

        if (userService == null) {
            throw new ServletException("userService не инициализированы");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if ("/api/user/all".equals(req.getServletPath())) {

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(
                    userMapper.usersToUserResponseDtos(
                            userService.getUsers()));

            // Отправляем JSON-ответ
            resp.getWriter().write(jsonResponse);
        } else if (EmailValidator.isValid(req.getParameter("email"))) { // валидация email
            String email = req.getParameter("email");

            Optional<User> user = userService.getUser(email);
            if (user.isEmpty())
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            else {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(
                        userMapper.userToUserResponseDto(
                                user.get()));
                resp.getWriter().write(jsonResponse);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Path not found");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long userId = Long.parseLong(req.getParameter("userId"));

        // Чтение JSON from req
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        // Преобразование JSON в user
        String json = sb.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        UserRequestDto userRequestDto = objectMapper.readValue(json, UserRequestDto.class);

        boolean upd = userService.editUser(userId, userMapper.userRequestDtoToUser(userRequestDto));

        if (upd) {
            resp.setStatus(HttpServletResponse.SC_OK); //200 ok
            PrintWriter out = resp.getWriter();
            out.print(true);
            out.flush();
        } else {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "User was not update");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long userId = Long.parseLong(req.getParameter("userId"));

        boolean upd = userService.deleteUser(userId);

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
