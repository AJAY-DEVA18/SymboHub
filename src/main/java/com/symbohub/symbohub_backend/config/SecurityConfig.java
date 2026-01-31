package com.symbohub.symbohub_backend.config;

import com.symbohub.symbohub_backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http
                // 1. Setup CORS first
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 2. Disable CSRF for Stateless APIs
                .csrf(csrf -> csrf.disable())
                // 3. Set Session to Stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow H2 Console
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()

                        // Explicitly allow OPTIONS (Preflight) requests for all paths
                        .requestMatchers(new AntPathRequestMatcher("/**", "OPTIONS")).permitAll()

                        // Public endpoints (Note: /api prefix is omitted as it's the context-path)
                        .requestMatchers(mvcMatcherBuilder.pattern("/colleges/register")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/colleges/login")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/colleges/approved")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/colleges/{id}")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/departments/login")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/admin/login")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/auth/**")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/files/**")).permitAll()

                        // Brochure Public endpoints (Fixed middle wildcard)
                        .requestMatchers(mvcMatcherBuilder.pattern("/brochures/college/*/public")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/brochures/department/*/public")).permitAll()

                        // Role-based endpoints
                        .requestMatchers(mvcMatcherBuilder.pattern("/colleges/**")).hasAnyRole("COLLEGE", "ADMIN")
                        .requestMatchers(mvcMatcherBuilder.pattern("/departments/**")).hasAnyRole("DEPARTMENT", "COLLEGE", "ADMIN")
                        .requestMatchers(mvcMatcherBuilder.pattern("/brochures/**")).hasAnyRole("DEPARTMENT", "COLLEGE", "ADMIN")
                        .requestMatchers(mvcMatcherBuilder.pattern("/admin/**")).hasRole("ADMIN")

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )
                // Allow frames for H2 console
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // Add JWT filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow your React app
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Standard headers used by Axios and JWT
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}