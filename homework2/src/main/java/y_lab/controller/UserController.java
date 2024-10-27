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
import y_lab.out.audit.AuditAction;
import y_lab.out.audit.LogExecutionTime;
import y_lab.service.UserService;
import y_lab.service.serviceImpl.UserServiceImpl;
import y_lab.util.DtoValidator;
import y_lab.util.EmailValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * Controller for managing users.
 * <p>
 * This controller handles HTTP requests related to users, including retrieval,
 * updating, and deleting users.
 * </p>
 */
@WebServlet(urlPatterns = {"/api/user/all", "/api/user/*"})
public class UserController extends HttpServlet {

    private UserService userService;
    private UserMapper userMapper = new UserMapperImpl();

    @Override
    public void init() throws ServletException {
        userService = (UserServiceImpl) getServletContext().getAttribute("userService");

        if (userService == null) {
            throw new ServletException("userService is not initialized");
        }
    }

    /**
     * Handles GET requests to retrieve user information.
     *
     * @param req  the HttpServletRequest object representing the client's request
     * @param resp the HttpServletResponse object representing the server's response
     * @throws ServletException if an error occurs while processing the request
     * @throws IOException      if an error occurs while writing to the response
     */
    @Override
    @LogExecutionTime
    @AuditAction(action = "Attempt to retrieve user(s)")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if ("/api/user/all".equals(req.getServletPath())) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(
                    userMapper.usersToUserResponseDtos(
                            userService.getUsers()));

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(jsonResponse);
        } else if (EmailValidator.isValid(req.getParameter("email"))) { // Email validation
            String email = req.getParameter("email");

            Optional<User> user = userService.getUser(email);
            if (user.isEmpty())
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            else {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(
                        userMapper.userToUserResponseDto(
                                user.get()));
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(jsonResponse);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Path not found");
        }
    }

    /**
     * Handles PUT requests to update user information.
     *
     * @param req  the HttpServletRequest object representing the client's request
     * @param resp the HttpServletResponse object representing the server's response
     * @throws ServletException if an error occurs while processing the request
     * @throws IOException      if an error occurs while writing to the response
     */
    @Override
    @LogExecutionTime
    @AuditAction(action = "Attempt to update user")
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        UserRequestDto userRequestDto = objectMapper.readValue(json, UserRequestDto.class);

        try {
            DtoValidator.validate(userRequestDto);

            boolean updated = userService.editUser(userId, userMapper.userRequestDtoToUser(userRequestDto));

            if (updated) {
                resp.setStatus(HttpServletResponse.SC_OK); // 200 OK
                PrintWriter out = resp.getWriter();
                out.print(true);
                out.flush();
            } else {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "User was not updated");
            }

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Handles DELETE requests to remove a user.
     *
     * @param req  the HttpServletRequest object representing the client's request
     * @param resp the HttpServletResponse object representing the server's response
     * @throws ServletException if an error occurs while processing the request
     * @throws IOException      if an error occurs while writing to the response
     */
    @Override
    @LogExecutionTime
    @AuditAction(action = "Attempt to delete user")
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Long userId = Long.parseLong(req.getParameter("userId"));

        boolean deleted = userService.deleteUser(userId);

        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK); // 200 OK
            PrintWriter out = resp.getWriter();
            out.print(true);
            out.flush();
        } else {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Nothing deleted");
        }
    }
}
