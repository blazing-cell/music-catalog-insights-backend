package org.example.musiccataloginsights.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /*
         * CORS preflight requests are OPTIONS requests.
         *
         * They do not contain a JWT token.
         * Therefore, we must allow them to continue immediately.
         */
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String authHeader =
                request.getHeader("Authorization");

        System.out.println(
                "JWT Filter - Request: "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI()
        );

        System.out.println(
                "JWT Filter - Authorization Header: "
                        + (authHeader != null)
        );

        /*
         * If there is no Authorization header,
         * continue the request normally.
         *
         * Public endpoints such as /users/login
         * do not have a JWT yet.
         */
        if (
                authHeader == null
                        || !authHeader.startsWith("Bearer ")
        ) {

            System.out.println(
                    "JWT Filter - No Bearer token found"
            );

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        /*
         * Extract JWT token.
         *
         * "Bearer abc123"
         * becomes
         * "abc123"
         */
        String token =
                authHeader.substring(7);

        try {

            /*
             * Extract email/username from JWT.
             */
            String email =
                    jwtService.extractUsername(token);

            System.out.println(
                    "JWT Filter - Extracted email: "
                            + email
            );

            /*
             * Authenticate only if:
             *
             * 1. Email was successfully extracted.
             * 2. No authentication already exists.
             */
            if (
                    email != null
                            && SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null
            ) {

                /*
                 * Load user from database.
                 */
                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(email);

                System.out.println(
                        "JWT Filter - User found: "
                                + userDetails.getUsername()
                );

                /*
                 * Validate JWT against the user.
                 */
                boolean valid =
                        jwtService.isTokenValid(
                                token,
                                userDetails
                        );

                System.out.println(
                        "JWT Filter - Token valid: "
                                + valid
                );

                if (valid) {

                    /*
                     * Create authenticated user.
                     */
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    /*
                     * Attach request details.
                     */
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    /*
                     * Store authentication in Spring Security context.
                     */
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication
                            );

                    System.out.println(
                            "JWT Filter - Authentication successful"
                    );

                } else {

                    System.out.println(
                            "JWT Filter - Token is invalid"
                    );
                }
            }

        } catch (Exception e) {

            /*
             * If JWT processing fails,
             * clear the security context.
             */
            System.out.println(
                    "JWT Filter - Authentication failed: "
                            + e.getMessage()
            );

            SecurityContextHolder.clearContext();
        }

        /*
         * Continue to the next filter/controller.
         */
        filterChain.doFilter(
                request,
                response
        );
    }


}
