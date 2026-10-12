package com.kusitms.website.global.config;

import com.kusitms.website.global.auth.AccessDeniedHandlerImpl;
import com.kusitms.website.global.auth.RestAuthenticationEntryPoint;
import com.kusitms.website.global.auth.jwt.JwtAuthenticationFilter;
import com.kusitms.website.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    // 허용할 FE origin 목록 (사이트 FE, 어드민 FE). 운영 환경에서는 실제 도메인으로 지정한다.
    @Value("${cors.allowed-origins:*}")
    private List<String> allowedOrigins;

    // 상위 권한이 하위 권한을 포함한다. hasRole('MANAGER')는 OWNER도 통과한다.
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_OWNER > ROLE_MANAGER\nROLE_MANAGER > ROLE_MEMBER");
        return roleHierarchy;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS 허용 적용
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT","PATCH","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 어드민: 권한 조정은 OWNER, 나머지 어드민 기능은 MANAGER 이상
                .antMatchers("/admin/owner/**").hasRole("OWNER")
                .antMatchers("/admin/**").hasRole("MANAGER")
                // 로그인한 학회원만 사용하는 API
                .antMatchers("/api/mypage/**", "/api/chat/**", "/api/auth/logout").authenticated()
                .antMatchers("/api/**",
                        "/ws/**",
                        "/swagger-ui/**",
                        "/api-docs",
                        "/swagger-resources/**",
                        "/api-docs/json/**",
                        "/favicon.ico",
                        "/error").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic().disable()
                .formLogin().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider) ,
                        UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}
