package y_lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import y_lab.dto.LoginInDto;
import y_lab.dto.LoginResetDto;
import y_lab.dto.LoginResponseDto;
import y_lab.dto.LoginUpDto;
import y_lab.mapper.LoginMapper;
import y_lab.mapper.LoginMapperImpl;
import y_lab.out.audit.AuditAction;
import y_lab.out.audit.LogExecutionTime;
import y_lab.service.LoginService;
import y_lab.service.serviceImpl.LoginServiceImpl;
import y_lab.util.DtoValidator;
import y_lab.util.JwtUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Controller for managing user authentication and registration.
 * <p>
 * This controller handles HTTP requests for signing in, signing up, and managing
 * password reset requests and actions.
 * </p>
 */
@WebServlet(urlPatterns = {
        "/api/login/signIn",
        "/api/login/signUp",
        "/api/login/password/request/*",
        "/api/login/password/reset/*"
})
public class LoginController extends HttpServlet {

    private final LoginMapper loginMapper = new LoginMapperImpl();
    private LoginService loginService;

    @Override
    public void init() throws ServletException {
        loginService = (LoginServiceImpl) getServletContext().getAttribute("loginService");
    }

    /**
     * Handles POST requests for user authentication and registration.
     *
     * @param req  the HttpServletRequest object representing the client's request
     * @param resp the HttpServletResponse object representing the server's response
     * @throws ServletException if an error occurs while processing the request
     * @throws IOException      if an error occurs while reading from the request or writing to the response
     */
    @Override
    @LogExecutionTime
    @AuditAction(action = "Attempt to authorize")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if ("/api/login/signIn".equals(req.getServletPath())) {
            // Read JSON from the request
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String json = sb.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            LoginInDto loginInDto = objectMapper.readValue(json, LoginInDto.class);

            try {
                DtoValidator.validate(loginInDto);

                LoginResponseDto loginResponseDto = loginService.login(loginMapper.loginInDtoToUser(loginInDto));

                if (loginResponseDto.id() != -1L) {
                    String token = JwtUtil.generateToken(loginResponseDto.id(), loginResponseDto.role());
                    resp.setHeader("Authorization", "Bearer " + token);

                    resp.setStatus(HttpServletResponse.SC_OK); // 200 OK
                    PrintWriter out = resp.getWriter();
                    out.print(loginResponseDto.id());
                    out.flush();
                } else {
                    resp.sendError(HttpServletResponse.SC_CONFLICT, loginResponseDto.message());
                }
            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            }

        } else if ("/api/login/signUp".equals(req.getServletPath())) {
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String json = sb.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            LoginUpDto loginUpDto = objectMapper.readValue(json, LoginUpDto.class);

            try {
                DtoValidator.validate(loginUpDto);

                LoginResponseDto loginResponseDto = loginService.register(loginMapper.loginUpDtoToUser(loginUpDto));

                if (loginResponseDto.id() != -1L) {
                    resp.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                    PrintWriter out = resp.getWriter();

                    String jsonResponse = objectMapper.writeValueAsString(loginResponseDto);

                    out.print(jsonResponse);
                    out.flush();
                } else {
                    resp.sendError(HttpServletResponse.SC_CONFLICT, loginResponseDto.message());
                }

            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            }
        } else if ("/api/login/password/request".equals(req.getServletPath())) {
            String email = req.getParameter("email");

            boolean res = loginService.requestPasswordReset(email);

            if (res) {
                resp.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                PrintWriter out = resp.getWriter();
                out.print(true); // Send success response
                out.flush();
            } else {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "Error with email");
            }

        } else if ("/api/login/password/reset".equals(req.getServletPath())) {
            // Read JSON from the request
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String json = sb.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            LoginResetDto loginResetDto = objectMapper.readValue(json, LoginResetDto.class);

            try {
                DtoValidator.validate(loginResetDto);

                LoginResponseDto res = loginService.resetPassword(loginMapper.loginResetDtoToUser(loginResetDto));

                if (res.id() != -1L) {
                    resp.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                    PrintWriter out = resp.getWriter();

                    String jsonResponse = objectMapper.writeValueAsString(res);

                    out.print(jsonResponse);
                    out.flush();
                } else {
                    resp.sendError(HttpServletResponse.SC_CONFLICT, res.message());
                }

            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Path not found");
        }
    }
}
