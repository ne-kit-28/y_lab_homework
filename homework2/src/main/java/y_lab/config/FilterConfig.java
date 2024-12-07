package y_lab.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import y_lab.controller.filter.JwtFilterRole;
import y_lab.controller.filter.JwtFilterUserId;

/**
 * Конфиг регистрирующий фильтры по url
 * <p>
 * apiFilter сверяет идентификатор пользователя
 * с запрашиваемыми данными
 * <p>
 * adminFilter проверяет роль пользователя
 */

@Configuration
public class FilterConfig {

    /**
     * Регистрация apiFilter
     * @return registrationBean
     */
    @Bean
    public FilterRegistrationBean<JwtFilterUserId> apiFilter() {
        FilterRegistrationBean<JwtFilterUserId> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtFilterUserId());
        registrationBean.addUrlPatterns("/habit/*", "/user/*", "/progress/*");
        return registrationBean;
    }

    /**
     * Регистрация adminFilter
     * @return registrationBean
     */
    @Bean
    public FilterRegistrationBean<JwtFilterRole> adminFilter() {
        FilterRegistrationBean<JwtFilterRole> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtFilterRole());
        registrationBean.addUrlPatterns("/admin/*");
        return registrationBean;
    }
}
