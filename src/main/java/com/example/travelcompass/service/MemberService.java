package com.example.travelcompass.service;

import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.common.exception.ErrorCode;
import com.example.travelcompass.config.MemberDetails;
import com.example.travelcompass.dto.request.MemberJoinRequest;
import com.example.travelcompass.entity.Member;
import com.example.travelcompass.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 인증(로그인)과 회원가입을 담당하는 서비스.
 * Spring Security의 UserDetailsService를 구현해서, 로그인 시 스프링 시큐리티가
 * 이 클래스를 통해 사용자 정보를 조회하고 비밀번호를 검증하도록 연결한다.
 */
@Service
@RequiredArgsConstructor // final 필드들(memberMapper, passwordEncoder, checklistService)을 생성자 주입받는다.
public class MemberService implements UserDetailsService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder; // 비밀번호를 평문으로 저장하지 않기 위한 단방향 암호화 도구
    private final ChecklistService checklistService; // 회원가입 완료 후 기본 체크리스트를 만들어주기 위해 협력

    /**
     * Spring Security가 로그인 처리 과정에서 아이디로 사용자 정보를 조회할 때 호출하는 메서드.
     * @param username 로그인 폼에 입력된 아이디
     * @return 시큐리티가 인증에 사용할 UserDetails(사용자명, 비밀번호, 권한 등을 감싼 객체)
     * @throws UsernameNotFoundException 아이디에 해당하는 회원이 없을 때 발생 (시큐리티가 로그인 실패로 처리)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberMapper.findByUsername(username);
        if (member == null) {
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다: " + username);
        }
        // Member 엔티티를 스프링 시큐리티가 이해하는 UserDetails 규격으로 감싸서 반환한다.
        return new MemberDetails(member);
    }

    /**
     * 신규 회원가입을 처리한다. 아이디 중복 검사, 비밀번호 암호화, 회원 저장,
     * 그리고 신규 회원을 위한 기본 체크리스트 생성까지 하나의 트랜잭션으로 묶어서 처리한다.
     * @param request 아이디/비밀번호/닉네임이 담긴 회원가입 요청 DTO
     */
    @Transactional // 회원 insert와 체크리스트 생성 중 하나라도 실패하면 전체를 롤백하기 위해 트랜잭션으로 묶음
    public void signup(MemberJoinRequest request) {
        if (memberMapper.countByUsername(request.getUsername()) > 0) {
            // 이미 존재하는 아이디면 회원가입을 진행하지 않고 즉시 비즈니스 예외를 던진다.
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }

        Member member = Member.builder()
                .username(request.getUsername())
                // 비밀번호는 절대 평문으로 저장하지 않고, PasswordEncoder로 단방향 암호화(해시)해서 저장한다.
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        // MyBatis Mapper를 통해 DB에 INSERT. insert 이후 MyBatis 설정에 따라 member.getId()에
        // 자동 생성된 PK가 채워진다(useGeneratedKeys 등).
        memberMapper.insert(member);
        // 회원가입 직후 여행 준비물 체크리스트 기본 항목(여권, 환전 등)을 자동으로 만들어준다.
        checklistService.createDefaults(member.getId());
    }

}
