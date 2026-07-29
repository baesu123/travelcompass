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

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberMapper.findByUsername(username);
        if (member == null) {
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다: " + username);
        }
        return new MemberDetails(member);
    }

    @Transactional
    public void signup(MemberJoinRequest request) {
        if (memberMapper.countByUsername(request.getUsername()) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }

        Member member = Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(request.getNickname())
                .build();

        memberMapper.insert(member);
    }

}
