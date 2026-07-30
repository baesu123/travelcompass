package com.example.travelcompass.controller;

import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.dto.request.MemberJoinRequest;
import com.example.travelcompass.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 회원 관련 화면(로그인, 회원가입)을 처리하는 컨트롤러.
 * {@code @Controller}로 선언되어 있어 반환값이 View 이름(템플릿 파일 경로)으로 해석되며,
 * REST API가 아닌 Thymeleaf 기반의 서버사이드 렌더링 화면을 담당한다.
 */
// @Controller : 이 클래스가 MVC 패턴의 컨트롤러임을 스프링에게 알려주는 어노테이션.
// 메서드 반환값(String)은 기본적으로 View(템플릿) 이름으로 처리된다. (@RestController와 달리 JSON을 바로 반환하지 않음)
// @RequiredArgsConstructor : Lombok이 final 필드(memberService)를 매개변수로 받는 생성자를 자동 생성해준다.
// 필드 주입(@Autowired) 대신 생성자 주입을 사용하면 불변성(final)을 보장하고 테스트하기도 쉬워진다.
@Controller
@RequiredArgsConstructor
public class MemberController {

    // 회원가입/로그인 등의 비즈니스 로직을 실제로 처리하는 서비스 계층.
    // 컨트롤러는 요청을 받아 서비스에 위임만 하고, 실제 로직(DB 저장, 검증 등)은 서비스가 담당한다.
    private final MemberService memberService;

    /**
     * 로그인 화면을 보여주는 메서드.
     * GET /login 요청이 들어오면 로그인 폼이 있는 템플릿을 반환한다.
     *
     * @return 렌더링할 뷰 이름 ("member/login" -> templates/member/login.html)
     */
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    /**
     * 회원가입 입력 화면을 보여주는 메서드.
     * 화면에 바인딩할 빈 객체(MemberJoinRequest)를 미리 Model에 담아두어,
     * Thymeleaf 폼에서 th:object로 필드를 매핑할 수 있도록 한다.
     *
     * @param model 뷰(템플릿)에 데이터를 전달하기 위한 스프링 MVC 객체
     * @return 렌더링할 뷰 이름 ("member/signup" -> templates/member/signup.html)
     */
    @GetMapping("/signup")
    public String signupForm(Model model) {
        // 빈 폼 객체를 미리 넣어줘야 th:object="${signupForm}" 같은 Thymeleaf 바인딩이 정상 동작한다.
        model.addAttribute("signupForm", new MemberJoinRequest());
        return "member/signup";
    }

    /**
     * 회원가입 폼 제출을 처리하는 메서드.
     * 입력값 검증(@Valid) 후 오류가 없으면 서비스에 회원가입을 위임하고,
     * 비즈니스 로직상 오류(예: 이메일 중복)가 발생하면 에러 메시지를 화면에 다시 보여준다.
     *
     * @param request       사용자가 입력한 회원가입 폼 데이터를 담은 DTO
     * @param bindingResult @Valid 검증 결과(에러 목록)를 담고 있는 객체
     * @param model         에러 발생 시 화면에 메시지를 전달하기 위한 객체
     * @return 성공 시 로그인 페이지로 리다이렉트, 실패 시 회원가입 폼을 다시 렌더링
     */
    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("signupForm") MemberJoinRequest request,
                          BindingResult bindingResult,
                          Model model) {
        // @Valid : MemberJoinRequest 내부에 선언된 @NotBlank, @Email 등의 검증 어노테이션을 실행시킨다.
        // 검증 실패 결과는 예외로 던져지지 않고 BindingResult에 담기므로, 아래처럼 직접 확인해야 한다.
        if (bindingResult.hasErrors()) {
            // 입력값 자체가 잘못된 경우 (형식 오류 등) 다시 같은 폼 화면을 보여준다.
            return "member/signup";
        }

        try {
            // 실제 회원가입 처리(중복 검사, 비밀번호 암호화, DB 저장 등)는 서비스 계층에 위임한다.
            memberService.signup(request);
        } catch (BusinessException e) {
            // 서비스 계층에서 발생한 비즈니스 규칙 위반(예: 이미 가입된 이메일)을 화면에 메시지로 표시한다.
            model.addAttribute("errorMessage", e.getMessage());
            return "member/signup";
        }

        // 회원가입 성공 시 로그인 페이지로 리다이렉트한다.
        // "redirect:" 접두사를 사용하면 브라우저가 다시 GET /login?signup 요청을 보내게 되어
        // 새로고침 시 폼이 중복 제출되는 것을 방지한다(Post-Redirect-Get 패턴).
        return "redirect:/login?signup";
    }

}
