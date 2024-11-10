package y_lab;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import y_lab.config.AopConfig;
import y_lab.config.SwaggerConfig;
import y_lab.config.WebConfig;
import y_lab.controller.filter.JwtFilterRole;
import y_lab.controller.filter.JwtFilterUserId;

public class MainWebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(WebConfig.class, AopConfig.class, SwaggerConfig.class);
        container.addListener(new ContextLoaderListener(context));


        AnnotationConfigWebApplicationContext apiContext = new AnnotationConfigWebApplicationContext();
        apiContext.setParent(context);
        apiContext.scan("y_lab.controller.api");
        ServletRegistration.Dynamic apiDispatcher = container.addServlet("apiDispatcher", new DispatcherServlet(apiContext));
        apiDispatcher.setLoadOnStartup(1);
        apiDispatcher.addMapping("/api/*");

        FilterRegistration.Dynamic apiFilter = container.addFilter("apiFilter", new JwtFilterUserId());
        apiFilter.addMappingForUrlPatterns(null, false, "/api/habit/*", "/api/user/*", "/api/progress/*");


        AnnotationConfigWebApplicationContext adminContext = new AnnotationConfigWebApplicationContext();
        adminContext.setParent(context);
        apiContext.scan("y_lab.controller");
        ServletRegistration.Dynamic adminDispatcher = container.addServlet("adminDispatcher", new DispatcherServlet(adminContext));
        adminDispatcher.setLoadOnStartup(2);
        adminDispatcher.addMapping("/api/admin/*");

        FilterRegistration.Dynamic adminFilter = container.addFilter("adminFilter", new JwtFilterRole());
        adminFilter.addMappingForUrlPatterns(null, false, "/api/admin/*");
    }
}
