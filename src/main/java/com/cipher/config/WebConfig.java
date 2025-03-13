package com.cipher.config;


import com.cipher.auth.AesPasswordEncoder;
import com.cipher.auth.JWTRequestFilter;
import com.cipher.auth.UserPasswordCheckFilter;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.tensorflow.Graph;
import org.tensorflow.Session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@EnableAsync
@Configuration
public class WebConfig {

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JWTRequestFilter jwtRequestFilter;

    @Autowired
    private UserPasswordCheckFilter userPasswordCheckFilter;

    @Autowired
    private AesPasswordEncoder aesPasswordEncoder;

    public static String[] excludeUrls = new String[]{"/auth/login", "/auth/faceid","/auth/faceid2"};


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(aesPasswordEncoder);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
       return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOrigin("*");
        configuration.addExposedHeader("Content-Disposition");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Cast to prevent IDE red-line
        httpSecurity.addFilterBefore((Filter) jwtRequestFilter, (Class<? extends Filter>) UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore((Filter) userPasswordCheckFilter, (Class<? extends Filter>) UsernamePasswordAuthenticationFilter.class);

        httpSecurity.authorizeRequests()
                .requestMatchers(excludeUrls)
                .permitAll()
                .anyRequest().authenticated();

        return httpSecurity.build();
    }

    private static final String MODEL_PATH = "/Users/johnny/IdeaProjects/Cipherium/src/main/resources/20180408-102900.pb";

    @Bean
    public Graph graph() throws IOException {
        Graph graph = new Graph();
        byte[] graphDef = Files.readAllBytes(Paths.get(MODEL_PATH));
        graph.importGraphDef(graphDef);
        return graph;
    }

    @Bean
    public Session session(Graph graph) {
        return new Session(graph);
    }


}