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

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupForm", new MemberJoinRequest());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("signupForm") MemberJoinRequest request,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            return "member/signup";
        }

        try {
            memberService.signup(request);
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/signup";
        }

        return "redirect:/login?signup";
    }

}
