package com.ingrid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	public static final String[] AUTH_WHITELIST = { "/v3/api-docs/**", "/swagger-ui/**"};

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(AUTH_WHITELIST);
	}

	// @Bean: Expoe o retorno do metodo ao Spring
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// desabilita ataques contra cross-site request forgery (o token por si só já protege)
		return http.csrf().disable()
				// configura a seguranca stateless (não armazena estado a cada requisição)
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();

	}

}
