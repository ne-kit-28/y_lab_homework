package y_lab.audit_logging_spring_boot_starter.util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * Обработчик окружения для загрузки настроек из YAML-файла при инициализации приложения.
 * <p>
 * Класс реализует {@link EnvironmentPostProcessor} и загружает настройки из файла
 * <code>application.yaml</code>, добавляя их в окружение Spring. Используется
 * {@link YamlPropertySourceLoader} для обработки YAML-файлов.</p>
 */
public class EnvPostProcessor implements EnvironmentPostProcessor {
    private final YamlPropertySourceLoader propertySourceLoader;

    /**
     * Конструктор для инициализации загрузчика свойств YAML.
     */
    public EnvPostProcessor() {
        propertySourceLoader = new YamlPropertySourceLoader(); // Yaml..Loader зачитает для нас конфигурацию из default.yaml
    }

    /**
     * Задает настройки окружения, загружая свойства из файла <code>application.yaml</code>.
     *
     * @param environment текущие настройки окружения Spring
     * @param application экземпляр Spring приложения
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        var resource = new ClassPathResource("application.yaml");
        PropertySource<?> propertySource = null;
        try {
            //просим Yaml зачитать настройки из файла
            propertySource = propertySourceLoader.load("log-package-starter", resource).get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        environment.getPropertySources().addLast(propertySource);
    }
}