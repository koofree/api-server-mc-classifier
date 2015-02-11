package kr.ac.korea.mobide.apiservice.infrastructure.config;

import kr.ac.korea.mobide.apiservice.infrastructure.filter.RequestLoggingFilter;
import kr.ac.korea.mobide.sigma.classifier.sqlite.CentroidClassifierSQLite;
import kr.ac.korea.mobide.sigma.wppr.sqlite.WPPRSQLite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * Created by Koo Lee on 2014-08-29.
 */

@Configuration
public class ServiceConfiguration {

    @Value("${spring.datasource.sigmaBase}")
    private String sigmaBase;

    @Value("${spring.datasource.sigmaSimilarity}")
    private String sigmaSimialrity;

    @Bean
    public FilterRegistrationBean requestLoggingFilterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new RequestLoggingFilter());
        bean.addUrlPatterns("/category");
        return bean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        bean.setFilter(filter);
        return bean;
    }

    @Bean
    public CentroidClassifierSQLite centroidClassifierSQLite() {
        return new CentroidClassifierSQLite(sigmaBase);
    }

    @Bean
    public WPPRSQLite wpprSQLite() {
        return new WPPRSQLite(sigmaSimialrity);
    }
}
