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
 * The {@code JwtFilterRole} class is a servlet filter that validates the user role
 * in the JWT token for incoming requests to specific API endpoints.
 * This filter ensures that only users with the appropriate role (in this case,
 * the {@code ADMINISTRATOR} role) can access certain resources.
 *
 * <p>This filter is applied to the following URL pattern:</p>
 * <ul>
 *     <li>{@code /api/user/all}</li>
 * </ul>
 *
 * <p>If the JWT token is missing, invalid, or if the user's role does not match the
 * required role, the filter will return an HTTP error response.</p>
 */

public class JwtFilterRole implements Filter {

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

        token = token.substring(7);

        try {
            String roleFromToken = JwtUtil.getRole(token);

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
