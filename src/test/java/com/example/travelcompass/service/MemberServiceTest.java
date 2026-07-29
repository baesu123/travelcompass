package com.example.travelcompass.service;

import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.common.exception.ErrorCode;
import com.example.travelcompass.dto.request.MemberJoinRequest;
import com.example.travelcompass.entity.Member;
import com.example.travelcompass.mapper.MemberMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberServiceTest {

    private final MemberMapper memberMapper = mock(MemberMapper.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ChecklistService checklistService = mock(ChecklistService.class);
    private final MemberService memberService = new MemberService(memberMapper, passwordEncoder, checklistService);

    @Test
    void 회원가입에_성공하면_기본_체크리스트가_생성된다() {
        when(memberMapper.countByUsername("traveler")).thenReturn(0);
        doAnswer((InvocationOnMock invocation) -> {
            Member member = invocation.getArgument(0);
            member.setId(1L);
            return null;
        }).when(memberMapper).insert(any(Member.class));

        MemberJoinRequest request = new MemberJoinRequest();
        request.setUsername("traveler");
        request.setPassword("pass1234");
        request.setNickname("여행자");

        memberService.signup(request);

        ArgumentCaptor<Long> memberIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(checklistService, times(1)).createDefaults(memberIdCaptor.capture());
        assertThat(memberIdCaptor.getValue()).isEqualTo(1L);
    }

    @Test
    void 이미_존재하는_아이디면_예외가_발생하고_체크리스트를_생성하지_않는다() {
        when(memberMapper.countByUsername("traveler")).thenReturn(1);

        MemberJoinRequest request = new MemberJoinRequest();
        request.setUsername("traveler");
        request.setPassword("pass1234");
        request.setNickname("여행자");

        assertThatThrownBy(() -> memberService.signup(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATE_USERNAME);

        verify(checklistService, times(0)).createDefaults(any());
    }

}
