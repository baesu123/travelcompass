package com.example.travelcompass.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스. 세션 기반(폼 로그인) 인증 방식을 사용하도록 구성한다.
 * 초보자 팁: @EnableWebSecurity를 붙이면 스프링 시큐리티의 웹 보안 기능이 활성화되고,
 * 이 클래스에서 정의한 SecurityFilterChain 빈이 모든 HTTP 요청을 가로채는 필터 체인으로 등록된다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 비밀번호 암호화에 사용할 PasswordEncoder 빈을 등록한다.
     * BCrypt는 단방향 해시 알고리즘으로, 회원가입 시 비밀번호를 암호화해서 저장하고
     * 로그인 시 입력한 비밀번호를 같은 방식으로 해시해 저장된 값과 비교(matches)하는 데 사용된다.
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * HTTP 요청에 대한 인증/인가 규칙과 로그인/로그아웃 방식을 정의하는 SecurityFilterChain을 구성한다.
     * 초보자 팁: SecurityFilterChain은 요청이 컨트롤러에 도달하기 전에 거치는 보안 필터들의 묶음이며,
     * 스프링 시큐리티는 세션에 저장된 인증 정보(Authentication)를 통해 로그인 상태를 유지한다(세션 기반 인증).
     *
     * @param http 보안 설정을 구성하기 위한 HttpSecurity 빌더
     * @return 구성이 끝난 SecurityFilterChain
     * @throws Exception 보안 설정 구성 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 요청 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인/회원가입 페이지와 정적 리소스(css/js/images)는 인증 없이 누구나 접근 가능
                        .requestMatchers("/login", "/signup", "/css/**", "/js/**", "/images/**").permitAll()
                        // 그 외 나머지 모든 요청은 로그인(인증)된 사용자만 접근 가능
                        .anyRequest().authenticated()
                )
                // 폼 로그인(아이디/비밀번호 입력 화면) 방식 설정
                .formLogin(form -> form
                        .loginPage("/login") // 커스텀 로그인 페이지 경로
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 항상 홈으로 이동
                        .failureUrl("/login?error") // 로그인 실패 시 이동할 경로 (에러 메시지 표시용)
                        .permitAll() // 로그인 페이지 자체는 인증 없이 접근 가능해야 함
                )
                // 로그아웃 처리 설정
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃을 트리거하는 요청 경로 (세션 무효화 처리)
                        .logoutSuccessUrl("/login?logout") // 로그아웃 성공 후 이동할 경로
                        .permitAll()
                );

        return http.build();
    }

}
