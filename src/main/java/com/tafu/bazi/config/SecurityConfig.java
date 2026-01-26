package com.tafu.bazi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig
 *
 * <p>描述: Spring Security 安全配置类。
 *
 * <p>包含内容: 1. 过滤器链配置 (FilterChain) 2. 密码编码器配置 (BCrypt) 3. 认证管理器配置
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserDetailsService userDetailsService;

  private final JwtAuthenticationFilter jwtAuthFilter;

  private final RateLimitFilter rateLimitFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        // 认证相关
                        "/auth/login/**",
                        "/auth/register",
                        "/auth/send-code",
                        // 排盘相关 - 允许游客访问
                        "/bazi/calculate",
                        "/bazi/leap-month/**",
                        "/bazi/coordinates",
                        // API 文档
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(
            rateLimitFilter,
            org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
                .class)
        .addFilterBefore(
            jwtAuthFilter,
            org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
                .class);

    return http.build();
  }

  @Bean
  public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
    org.springframework.web.cors.CorsConfiguration configuration =
        new org.springframework.web.cors.CorsConfiguration();
    configuration.setAllowedOriginPatterns(java.util.List.of("*"));
    configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(java.util.List.of("*"));
    configuration.setAllowCredentials(true);
    org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
        new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
