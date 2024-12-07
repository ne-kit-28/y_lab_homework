package y_lab.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

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
}
