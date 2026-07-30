package com.example.travelcompass.config;

import com.example.travelcompass.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 로그인한 회원 정보를 감싸는 UserDetails 구현체.
 * 컨트롤러에서 @AuthenticationPrincipal MemberDetails 로 주입받아
 * memberId를 꺼내 쓴다. (본인 데이터만 조회/수정하기 위한 기준)
 */
public class MemberDetails implements UserDetails {

    // 실제 회원 데이터(Entity)를 그대로 감싸서 보관 (위임 대상)
    private final Member member;

    /**
     * 감쌀 Member 엔티티를 받아 MemberDetails를 생성한다.
     * 초보자 팁: 로그인 시 Spring Security가 UserDetailsService를 통해 이 객체를 만들고,
     * 인증에 성공하면 이 객체가 세션의 Authentication 안에 principal로 저장되어
     * 이후 요청마다 @AuthenticationPrincipal로 꺼내 쓸 수 있게 된다.
     *
     * @param member 로그인 대상 회원 엔티티
     */
    public MemberDetails(Member member) {
        this.member = member;
    }

    /**
     * 감싸고 있는 회원의 PK(고유 번호)를 꺼내온다.
     * 컨트롤러/서비스에서 "현재 로그인한 회원"을 식별할 때 이 값을 기준으로 본인 데이터만 조회/수정한다.
     *
     * @return 회원 id
     */
    public Long getMemberId() {
        return member.getId();
    }

    /**
     * 감싸고 있는 회원의 닉네임을 꺼내온다. (화면 표시용)
     *
     * @return 회원 닉네임
     */
    public String getNickname() {
        return member.getNickname();
    }

    /**
     * 이 회원이 가진 권한(role) 목록을 반환한다.
     * 이 프로젝트는 별도의 관리자 권한 체계가 없으므로 모든 로그인 회원에게 고정으로 ROLE_USER 하나만 부여한다.
     *
     * @return 항상 ROLE_USER 하나만 담긴 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Spring Security가 로그인 시 폼에 입력된 비밀번호와 비교하기 위해 사용하는 암호화된 비밀번호를 반환한다.
     *
     * @return DB에 저장된 BCrypt 암호화 비밀번호
     */
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /**
     * Spring Security가 로그인 아이디로 취급하는 값을 반환한다.
     *
     * @return 회원의 로그인 아이디(username)
     */
    @Override
    public String getUsername() {
        return member.getUsername();
    }

    /**
     * 계정 만료 여부. 이 프로젝트는 계정 만료 기능을 사용하지 않으므로 항상 true(만료 안 됨)를 반환한다.
     *
     * @return 항상 true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠김 여부. 이 프로젝트는 계정 잠금 기능을 사용하지 않으므로 항상 true(잠기지 않음)를 반환한다.
     *
     * @return 항상 true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명(비밀번호) 만료 여부. 별도 기능이 없으므로 항상 true(만료 안 됨)를 반환한다.
     *
     * @return 항상 true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부. 별도의 계정 비활성화 기능이 없으므로 항상 true(활성 상태)를 반환한다.
     *
     * @return 항상 true
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
