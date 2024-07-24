package ecommerce.accountmanagement;

import ecommerce.accountmanagement.config.RequestLoggingFilterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AccountManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountManagementApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<RequestLoggingFilterConfig> loggingFilter() {
		final FilterRegistrationBean<RequestLoggingFilterConfig> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestLoggingFilterConfig());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}
}
