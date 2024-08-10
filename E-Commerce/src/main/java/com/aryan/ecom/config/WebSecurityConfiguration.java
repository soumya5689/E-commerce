package com.aryan.ecom.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.aryan.ecom.filters.JwtRequestFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfiguration {

	private final JwtRequestFilter jwtRequestFilter;

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}

	@SuppressWarnings("deprecation")
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
		log.info("Configuring security filter chain");

		http.csrf(csrf -> {
			log.info("Disabling CSRF protection");
			csrf.disable();
		});

		http.authorizeRequests(auth -> {
			log.info("Configuring authorization rules");
			auth.requestMatchers(
							mvc.pattern("/authenticate"),
							mvc.pattern("/sign-up"),
							mvc.pattern("/order/**"),
							mvc.pattern("/v3/api-docs"),
							mvc.pattern("/swagger-resources/**"),
							mvc.pattern("/swagger-ui/**"),
							mvc.pattern("/webjars/**")
					).permitAll()
					.requestMatchers(mvc.pattern(".api/**")).authenticated();
		});

		http.sessionManagement(sessionManagement -> {
			sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		});

		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
