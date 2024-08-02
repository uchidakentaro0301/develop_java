package jp.co.sss.java_ec_program.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jp.co.sss.java_ec_program.filter.LoginCheckFilter;

@Configuration
public class AppConfig {

    @Autowired
    private LoginCheckFilter loginCheckFilter;

    @Bean
    public FilterRegistrationBean<LoginCheckFilter> loggingFilter() {
        FilterRegistrationBean<LoginCheckFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(loginCheckFilter);
        registrationBean.addUrlPatterns("/*"); // すべてのURLにフィルターを適用

        return registrationBean;
    }
}