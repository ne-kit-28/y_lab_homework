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

/**
 * The {@code JwtFilterUserId} class is a servlet filter that checks for a valid JWT token
 * in incoming requests for specific API endpoints. It ensures that the user ID in the request
 * matches the user ID embedded in the token or that the user has administrator privileges.
 *
 * <p>This filter is applied to the following URL patterns:</p>
 * <ul>
 *     <li>{@code /api/user}</li>
 *     <li>{@code /api/habit/*}</li>
 *     <li>{@code /api/progress/*}</li>
 * </ul>
 *
 * <p>On detecting an invalid token or a mismatch between the user ID in the token and the request,
 * the filter will return an HTTP error response.</p>
 */

@WebFilter(urlPatterns = {
        "/api/user"
        , "/api/habit/*"
        , "/api/progress/*"
})
public class JwtFilterUserId implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String token = req.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization token is missing or invalid");
            return;
        }

        token = token.substring(7); // Убираем Bearer

        try {
            String roleFromToken = JwtUtil.getRole(token);
            String roleParam = Role.ADMINISTRATOR.getValue();

            Long userIdFromToken = JwtUtil.getUserId(token);
            String userIdParam = req.getParameter("userId");

            if (userIdParam != null && Long.parseLong(userIdParam) != userIdFromToken && !roleParam.equals(roleFromToken)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "User ID mismatch");
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
