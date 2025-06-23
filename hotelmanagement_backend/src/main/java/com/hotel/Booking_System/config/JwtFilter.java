package com.hotel.Booking_System.config;

import com.hotel.Booking_System.service.userService.ManagerUserDetailsService;
import com.hotel.Booking_System.service.userService.GuestUserDetailsService;
import com.hotel.Booking_System.service.userService.AdminUserDetailsService;
import com.hotel.Booking_System.service.userService.JWTServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTServiceImpl jwtService;

    @Autowired
    private  ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);
        username = jwtService.extractUserName(token);

        List<String> roles = jwtService.extractRoles(token);
        if (roles == null || roles.isEmpty()) {
            logger.error("Roles not found in JWT token!");
        }

        //setting up roles

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;
            if (roles != null) {
                if (roles.contains("ROLE_GUEST")) {
                    try {
                        userDetails = context.getBean(GuestUserDetailsService.class).loadUserByUsername(username);
                    } catch (Exception e) {

                    }
                } else if (roles.contains("ROLE_MANAGER")) {
                    try {
                        userDetails = context.getBean(ManagerUserDetailsService.class).loadUserByUsername(username);
                    } catch (Exception e) {
                    }
                }else if (roles.contains("ROLE_ADMIN")) {
                    try {
                        userDetails = context.getBean(AdminUserDetailsService.class).loadUserByUsername(username);
                    } catch (Exception e) {
                    }
                }
                if (userDetails != null && jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}

