package com.internalproject.user_service.filter;

import com.internalproject.user_service.service.CustomUserDetailsService;
import com.internalproject.user_service.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip JWT check for public endpoints
        return path.startsWith("/api/auth/") || path.equals("/api/health");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1️⃣ Get Authorization header
        final String authHeader = request.getHeader("Authorization");

        // If header missing or not Bearer → continue filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token - Spring Security will reject if endpoint is protected
            filterChain.doFilter(request, response);
            return;
        }

        // 2️⃣ Extract token
        String token = authHeader.substring(7);

        // 3️⃣ Extract userId from token
        String userId;
        try {
            userId = jwtService.validateAndExtractUserId(token);
        } catch (Exception e) {
            logger.warn("JWT validation failed: {}", e.getMessage());
            // Send 401 immediately instead of passing to the chain unauthenticated
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        // 4️⃣ If user not already authenticated
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5️⃣ Load user from DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            // 6️⃣ Create Authentication object
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 7️⃣ Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 8️⃣ Continue filter chain
        filterChain.doFilter(request, response);
    }
}
