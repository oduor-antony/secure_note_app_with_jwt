package com.jwt.secure_notes_app.security;

import com.jwt.secure_notes_app.services.JwtService;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header=request.getHeader(AUTH_HEADER);

        if (header==null||! header.startsWith(BEARER_PREFIX)){
            filterChain.doFilter(request,response);
            return;
        }
        String token=header.substring(BEARER_PREFIX.length()).trim();

        try {
            String userName=jwtService.extractUsername(token);
            if (SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails user=userDetailService.loadUserByUsername(userName);
                if(jwtService.isTokenValid(token,user)){
                    UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch(Exception e){
            log.warn("JWT failed: {}", e.toString());
            SecurityContextHolder.clearContext();
    }
        filterChain.doFilter(request, response);

    }
}