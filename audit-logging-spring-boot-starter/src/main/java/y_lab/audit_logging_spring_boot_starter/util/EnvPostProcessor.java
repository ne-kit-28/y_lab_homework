package y_lab.audit_logging_spring_boot_starter.util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class EnvPostProcessor implements EnvironmentPostProcessor {
    private final YamlPropertySourceLoader propertySourceLoader;

    public EnvPostProcessor() {
        propertySourceLoader = new YamlPropertySourceLoader(); // Yaml..Loader зачитает для нас конфигурацию из default.yaml
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        var resource = new ClassPathResource("application.yaml"); // определяем application.yml как локальный ресурс
        PropertySource<?> propertySource = null;
        try {
            // и просим Yaml...Loader зачитать настройки из файла
            propertySource = propertySourceLoader.load("log-package-starter", resource).get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // прочитанные настройки проставляются в настройки окружения Spring'а
        environment.getPropertySources().addLast(propertySource);
    }
}