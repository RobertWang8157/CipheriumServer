package com.cipher.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private JWTUserDetailsService jwtUserDetailsService;

    @Autowired
    private MessageSource messageSource;

    private List<String> excludeUrls = List.of("/auth/login", "/auth/faceid","/auth/faceid2");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            String jwt = jwtProvider.resolveToken(request);

            if (StringUtils.isNotBlank(jwt)) {
                String username = jwtProvider.getSubject(jwt);

                if (StringUtils.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
                    if (jwtProvider.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(token);

                    }else {
                        throw new Exception("JWT invalid.");
                    }
                }
                filterChain.doFilter(request, response);
            } else {
                throw new Exception("JWT invalid.");
            }
        } catch (Exception e){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ObjectMapper objectMapper= new ObjectMapper();
            String jsonString = objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).writeValueAsString(e.getMessage());
            PrintWriter out = response.getWriter();
            out.print(jsonString);
            out.flush();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        if(excludeUrls.contains(path)){
            return true;
        }
        return false;
    }
}