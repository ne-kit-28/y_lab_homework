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

@WebServlet(urlPatterns =
        { "/api/login/signIn"
        , "/api/login/signUp"
        , "/api/login/password/request/*"
        , "/api/login/password/reset/*" })
public class LoginController extends HttpServlet {

    private final LoginMapper loginMapper = new LoginMapperImpl();;
    LoginService loginService;

    @Override
    public void init() throws ServletException {
        loginService = (LoginServiceImpl) getServletContext().getAttribute("loginService");
    }

    @Override
    @LogExecutionTime
    @AuditAction(action = "Попытка авторизации")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if ("/api/login/signIn".equals(req.getServletPath())) {

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
            LoginInDto loginInDto = objectMapper.readValue(json, LoginInDto.class);

            try {
                DtoValidator.validate(loginInDto);

                LoginResponseDto loginResponseDto = loginService.login(loginMapper.loginInDtoToUser(loginInDto));

                if (loginResponseDto.id() != -1L) {
                    String token = JwtUtil.generateToken(loginResponseDto.id(), loginResponseDto.role());
                    resp.setHeader("Authorization", "Bearer " + token);

                    resp.setStatus(HttpServletResponse.SC_OK); //200 ok
                    PrintWriter out = resp.getWriter();
                    out.print(loginResponseDto.id()); // Отправляем id
                    out.flush();
                } else {
                    resp.sendError(HttpServletResponse.SC_CONFLICT, loginResponseDto.message());
                }
            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            }

        } else if ("/api/login/signUp".equals(req.getServletPath())) { // Проверка, что userId содержит Id
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
            LoginUpDto loginUpDto = objectMapper.readValue(json, LoginUpDto.class);

            try {
                DtoValidator.validate(loginUpDto);

                LoginResponseDto loginResponseDto = loginService.register(loginMapper.loginUpDtoToUser(loginUpDto));

                if (loginResponseDto.id() != -1L) {
                    resp.setStatus(HttpServletResponse.SC_CREATED); //201 created
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

            if (res){
                resp.setStatus(HttpServletResponse.SC_CREATED); //201 Created
                PrintWriter out = resp.getWriter();
                out.print(true); // Отправляем id
                out.flush();
            } else
                resp.sendError(HttpServletResponse.SC_CONFLICT, "error email");

        } else if ("/api/login/password/reset".equals(req.getServletPath())) {

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
            LoginResetDto loginResetDto = objectMapper.readValue(json, LoginResetDto.class);

            try {
                DtoValidator.validate(loginResetDto);

                LoginResponseDto res = loginService.resetPassword(loginMapper.loginResetDtoToUser(loginResetDto));

                if (res.id() != -1L){
                    resp.setStatus(HttpServletResponse.SC_CREATED); //201 Created
                    PrintWriter out = resp.getWriter();

                    String jsonResponse = objectMapper.writeValueAsString(res);

                    out.print(jsonResponse); // Отправляем id
                    out.flush();
                } else
                    resp.sendError(HttpServletResponse.SC_CONFLICT, res.message());

            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            }
        }else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Path not found");
        }
    }
}
