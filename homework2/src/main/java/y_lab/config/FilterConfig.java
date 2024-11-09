package y_lab.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import y_lab.controller.filter.JwtFilterRole;
import y_lab.controller.filter.JwtFilterUserId;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<JwtFilterUserId> apiFilter() {
        FilterRegistrationBean<JwtFilterUserId> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtFilterUserId());
        registrationBean.addUrlPatterns("/habit/*", "/user/*", "/progress/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<JwtFilterRole> adminFilter() {
        FilterRegistrationBean<JwtFilterRole> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtFilterRole());
        registrationBean.addUrlPatterns("/admin/*");
        return registrationBean;
    }
}
