package y_lab.controller.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import y_lab.domain.enums.Role;
import y_lab.util.JwtUtil;

@WebFilter(urlPatterns = {
        "/api/user/all"
})
public class JwtFilterRole implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Получаем токен из заголовка
        String token = req.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization token is missing or invalid");
            return;
        }

        token = token.substring(7); // Убираем "Bearer "
        System.out.println(token); //для отладки

        try {
            String roleFromToken = JwtUtil.getRole(token);
            System.out.println(roleFromToken);

            String roleParam = Role.ADMINISTRATOR.getValue();


            if (!roleParam.equals(roleFromToken)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "User role mismatch");
                return;
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}