package y_lab.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Конфиг настраивающий дополнительный админский Диспатчер сервлет,
 * (помимо дефолтного поставляемого Spring boot)
 * для удобного использования фильтра, который разрешает доступ к информации
 * на основе роли пользователя
 */

@Configuration
public class WebConfig{

    @Bean
    public ServletRegistrationBean<DispatcherServlet> adminDispatcher() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.scan("y_lab.controller");
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ServletRegistrationBean<DispatcherServlet> registrationBean =
                new ServletRegistrationBean<>(dispatcherServlet, "/admin/*");
        registrationBean.setName("adminDispatcher");
        registrationBean.setLoadOnStartup(2);
        return registrationBean;
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // Allow all origins for development
        config.addAllowedOriginPattern("*");
        // Expose the Authorization header so the client can read it (for token exchange via headers)
        config.addExposedHeader("Authorization");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
