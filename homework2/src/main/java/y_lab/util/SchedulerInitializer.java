package y_lab.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import y_lab.service.ExecutorService;
import y_lab.service.serviceImplNotAudit.ExecutorServiceImpl;

/**
 * Класс {@code SchedulerInitializer} инициализирует планировщик задач при событии
 * обновления контекста приложения. Он реализует интерфейс {@link ApplicationListener}
 * и слушает события {@link ContextRefreshedEvent}.
 */
@Component
public class SchedulerInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final ExecutorService executorService;

    /**
     * Конструктор класса {@code SchedulerInitializer}.
     *
     * @param executorService экземпляр {@link ExecutorServiceImpl}, используемый для запуска планировщика.
     */
    @Autowired
    public SchedulerInitializer(ExecutorServiceImpl executorService) {
        this.executorService = executorService;
    }

    /**
     * Вызывается при возникновении события {@link ContextRefreshedEvent}.
     * Запускает планировщик задач через {@link ExecutorService}.
     *
     * @param event событие обновления контекста приложения.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        executorService.startScheduler();
    }
}
