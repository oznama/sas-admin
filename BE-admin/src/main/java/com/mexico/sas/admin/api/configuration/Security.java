package com.mexico.sas.admin.api.configuration;

import com.mexico.sas.admin.api.security.AuthorizationFilter;
import com.mexico.sas.admin.api.security.CustomAuthEntryPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
public class Security extends WebSecurityConfigurerAdapter {

  @Value("#{'${api.security.pathAllowed}'.split(',')}")
  private String[] pathsAllowed;

  @Autowired
  private AuthorizationFilter authorizationFilter;

  @Autowired
  private CustomAuthEntryPoint customAuthEntryPoint;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    log.debug("Configuring security ...");
    http.cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(customAuthEntryPoint)
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().headers().frameOptions().disable()
            .and().authorizeRequests()
            .antMatchers(pathsAllowed).permitAll()
            .anyRequest().authenticated()
            .and().addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    log.debug("Cors configuration source ...");
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("*"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setExposedHeaders(Arrays.asList("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
