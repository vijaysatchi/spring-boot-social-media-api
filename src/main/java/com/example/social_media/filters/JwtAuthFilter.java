package com.example.social_media.filters;

import com.example.social_media.config.JwtConfig;
import com.example.social_media.services.CustomUserDetailsService;
import com.example.social_media.services.Jwt;
import com.example.social_media.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtConfig jwtConfig;

    private String getCookieValue(Cookie[] cookies, String name) {
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String accessToken = getCookieValue(cookies, "accessToken");
        Jwt jwt = null;
        if(accessToken == null){
            String refreshToken = getCookieValue(cookies, "refreshToken");
            if(refreshToken != null) {
                accessToken = jwtService.refresh(refreshToken);
                response.addHeader(
                        "Set-Cookie",
                        "accessToken=" + accessToken +
                                "; HttpOnly; Secure; Path=/; Max-Age=" + jwtConfig.getAccessTokenExpiration() +
                                "; SameSite=Strict"
                );
                jwt = jwtService.parseToken(accessToken);
            }
        } else jwt = jwtService.parseToken(accessToken);

        if (jwt == null || jwt.isExpired()) {
            filterChain.doFilter(request, response);
            return;
        }

        var user = userDetailsService.getCustomUserDetails(jwt.getUserId(), jwt.getClaim("email"));
        var authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
