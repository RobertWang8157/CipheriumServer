package com.cipher.auth;


import com.cipher.entity.User;
import com.cipher.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTProvider {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_START_WITH = "Bearer ";
    private static final long JWT_VALIDITY_IN_MILLISECONDS = 8 * 60 * 60 * 1000;
    private static final String JWT_AUTHORITY_NAME = "aut";

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private UserService userService;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        //claims.put(JWT_AUTHORITY_NAME, userDetails.getAuthorities().toArray(new GrantedAuthority[0])[0].getAuthority());

        Key secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_VALIDITY_IN_MILLISECONDS))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String userName = getSubject(token);
        User user = userService.findByUserName(userName);

        return StringUtils.equalsIgnoreCase(getSubject(token), userDetails.getUsername())
                && !isTokenExpired(token)
                && user != null;
    }



    public String getSubject(String token) {
        return getAllClaims(token).getSubject();
    }

    private Date getExpirationDate(String token) {
        return getAllClaims(token).getExpiration();
    }

    private Date getIssuedAt(String token) {
        return getAllClaims(token).getIssuedAt();
    }

    public String getAuthority(String token) {
        return (String) getAllClaims(token).get(JWT_AUTHORITY_NAME);
    }

    private Claims getAllClaims(String token) {
        Key secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
        return jwtParser.parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    public String resolveToken(HttpServletRequest request) {
        String authToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(authToken) && authToken.startsWith(TOKEN_START_WITH)) {
            return authToken.substring(7);
        }

        return null;
    }
}
