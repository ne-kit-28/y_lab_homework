package y_lab;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import y_lab.config.WebConfig;
import y_lab.controller.filter.JwtFilterRole;
import y_lab.controller.filter.JwtFilterUserId;

public class MainWebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext container) throws ServletException {

        //AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        //context.register(WebConfig.class);
        //container.addListener(new ContextLoaderListener(context));

        AnnotationConfigWebApplicationContext apiContext = new AnnotationConfigWebApplicationContext();
        apiContext.register(WebConfig.class);

        apiContext.scan("y_lab");
        ServletRegistration.Dynamic apiDispatcher = container.addServlet("apiDispatcher", new DispatcherServlet(apiContext));
        apiDispatcher.setLoadOnStartup(1);
        apiDispatcher.addMapping("/");

        container.addListener(new ContextLoaderListener(apiContext));

        FilterRegistration.Dynamic apiFilter = container.addFilter("apiFilter", new JwtFilterUserId());
        apiFilter.addMappingForServletNames(null, false, "/api/user/**", "/api/habit/**", "/api/progress/**");


//        AnnotationConfigWebApplicationContext adminContext = new AnnotationConfigWebApplicationContext();
//        adminContext.register(WebConfig.class);
//        adminContext.scan("y_lab");
//        ServletRegistration.Dynamic adminDispatcher = container.addServlet("adminDispatcher", new DispatcherServlet(adminContext));
//        adminDispatcher.setLoadOnStartup(2);
//        adminDispatcher.addMapping("/admin/**");
//
//        FilterRegistration.Dynamic adminFilter = container.addFilter("adminFilter", new JwtFilterRole());
//        adminFilter.addMappingForServletNames(null, false, "adminDispatcher");
    }
}
