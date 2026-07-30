package com.example.travelcompass.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 회원(사용자 계정) 정보를 나타내는 엔티티 클래스.
 * DB의 member 테이블 한 행과 매핑되며, 로그인/회원가입 등 인증 관련 기능의 기반 데이터가 된다.
 * 이 클래스 자체는 순수 데이터 객체이며, Spring Security 인증을 위해서는
 * 이 객체를 감싸는 MemberDetails(UserDetails 구현체)가 별도로 사용된다.
 *
 * Lombok 어노테이션 설명(초보자용 팁):
 * - @Getter/@Setter: 필드별 getter/setter 자동 생성.
 * - @NoArgsConstructor/@AllArgsConstructor: 기본 생성자/전체 필드 생성자 자동 생성.
 * - @Builder: Member.builder()...build() 형태로 객체 생성 가능.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    // 회원 고유 번호 (PK)
    private Long id;
    // 로그인에 사용하는 아이디(계정명), 보통 unique 제약이 걸려있다
    private String username;
    // 암호화(해시)되어 저장되는 비밀번호. 평문이 아니라 PasswordEncoder로 인코딩된 값이 저장된다
    private String password;
    // 화면에 노출되는 닉네임(별칭)
    private String nickname;
    // 회원가입(계정 생성) 시각
    private LocalDateTime createdAt;

}
