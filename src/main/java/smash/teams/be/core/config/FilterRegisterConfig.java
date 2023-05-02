package smash.teams.be.core.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smash.teams.be.core.filter.TempFilter;

@Configuration
public class FilterRegisterConfig {
    @Bean
    public FilterRegistrationBean<?> filter1() {
        FilterRegistrationBean<TempFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TempFilter()); // 서블릿 필터 객체 담기
        registration.addUrlPatterns("/*");
        registration.setOrder(1); // 순서
        return registration;
    }
}
