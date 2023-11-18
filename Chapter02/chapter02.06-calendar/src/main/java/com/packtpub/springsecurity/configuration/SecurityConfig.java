package com.packtpub.springsecurity.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * Spring Security Config Class
 */
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

	/**
	 * User details service in memory user details manager.
	 *
	 * @return the in memory user details manager
	 */
	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder()
				.username("user")
				.password("user")
				.roles("USER")
				.build();

		UserDetails admin = User.withDefaultPasswordEncoder()
				.username("admin")
				.password("admin")
				.roles("USER", "ADMIN")
				.build();

		UserDetails user1 = User.withDefaultPasswordEncoder()
				.username("user1@example.com")
				.password("user1")
				.roles("USER")
				.build();

		UserDetails admin1 = User.withDefaultPasswordEncoder()
				.username("admin1@example.com")
				.password("admin1")
				.roles("USER", "ADMIN")
				.build();

		return new InMemoryUserDetailsManager(user, admin, user1, admin1);
	}


	/**
	 * Filter chain security filter chain.
	 *
	 * @param http the http
	 * @return the security filter chain
	 * @throws Exception the exception
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests( authz -> authz
						.requestMatchers(antMatcher("/resources/**")).permitAll()
						.requestMatchers(antMatcher("/webjars/**")).permitAll()
						.requestMatchers(antMatcher("/")).permitAll()
						.requestMatchers(antMatcher("/login/*")).permitAll()
						.requestMatchers(antMatcher("/logout/*")).permitAll()
						.requestMatchers(antMatcher("/errors/**")).permitAll()
						.requestMatchers(antMatcher("/admin/*")).hasRole("ADMIN")
						.requestMatchers(antMatcher("/events/")).hasRole("ADMIN")
						.requestMatchers(antMatcher("/**")).hasRole("USER")
				)
				.exceptionHandling(exceptions -> exceptions
						.accessDeniedPage("/errors/403"))
				.formLogin(form -> form
						.loginPage("/login/form")
						.loginProcessingUrl("/login")
						.failureUrl("/login/form?error")
						.usernameParameter("username")
						.passwordParameter("password")
						.defaultSuccessUrl("/default", true)
						.permitAll()
				).logout(form -> form
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login/form?logout")
						.permitAll()
				)

				.csrf(AbstractHttpConfigurer::disable);
		// For H2 Console
		http.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));

		return http.build();
	}

} 
