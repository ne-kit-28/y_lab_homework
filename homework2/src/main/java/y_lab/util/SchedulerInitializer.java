package y_lab.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import y_lab.service.ExecutorService;
import y_lab.service.serviceImplNotAudit.ExecutorServiceImpl;

@Component
public class SchedulerInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final ExecutorService executorService;

    @Autowired
    public SchedulerInitializer(ExecutorServiceImpl executorService) {
        this.executorService = executorService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        executorService.startScheduler();
    }
}
