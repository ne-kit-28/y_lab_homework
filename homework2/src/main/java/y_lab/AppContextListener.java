package y_lab;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import y_lab.service.StartApplicationFactory;
import y_lab.service.serviceImpl.*;

@WebListener
public class AppContextListener implements ServletContextListener {

    StartApplicationFactory factory;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {

            factory = new StartApplicationFactory();

            factory.executeMigration();

            UserServiceImpl userService = factory.createUserService();
            HabitServiceImpl habitService = factory.createHabitService();
            ProgressServiceImpl progressService = factory.createProgressService();
            LoginServiceImpl loginService = factory.createLoginService();

            ExecutorServiceImpl executorService = factory.createExecutorService();
            executorService.startScheduler();

            //заполнение servletContext для использования service в сервлетах
            sce.getServletContext().setAttribute("habitService", habitService);
            sce.getServletContext().setAttribute("userService", userService);
            sce.getServletContext().setAttribute("progressService", progressService);
            sce.getServletContext().setAttribute("loginService", loginService);

        } catch (Exception e) {
            System.out.println("Exception in factory");
            throw new RuntimeException("Ошибка инициализации сервиса", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            factory.close();
        } catch (Exception e) {
            System.out.println("factory wasn't init"); //TODO replace it on logging
        }
    }
}
