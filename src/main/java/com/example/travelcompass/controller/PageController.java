package com.example.travelcompass.controller;

import com.example.travelcompass.config.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PageController {

    @GetMapping("/")
    public String index(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        model.addAttribute("nickname", nicknameOf(memberDetails));
        return "index";
    }

    @GetMapping("/map")
    public String map() {
        return "redirect:/";
    }

    @GetMapping("/country/{countryCode}")
    public String countryDetail(@AuthenticationPrincipal MemberDetails memberDetails,
                                 @PathVariable String countryCode,
                                 Model model) {
        model.addAttribute("nickname", nicknameOf(memberDetails));
        model.addAttribute("countryCode", countryCode.toUpperCase());
        return "country/detail";
    }

    @GetMapping("/favorites")
    public String favorites(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        model.addAttribute("nickname", nicknameOf(memberDetails));
        return "favorite/list";
    }

    @GetMapping("/checklist")
    public String checklist(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        model.addAttribute("nickname", nicknameOf(memberDetails));
        return "checklist/list";
    }

    @GetMapping("/reviews")
    public String reviews(@AuthenticationPrincipal MemberDetails memberDetails,
                           @RequestParam(required = false) String countryCode,
                           Model model) {
        model.addAttribute("nickname", nicknameOf(memberDetails));
        model.addAttribute("countryCode", countryCode);
        return "review/list";
    }

    @GetMapping("/budget")
    public String budget(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        model.addAttribute("nickname", nicknameOf(memberDetails));
        return "budget/calculator";
    }

    @GetMapping("/reviews/{reviewId}")
    public String reviewDetail(@AuthenticationPrincipal MemberDetails memberDetails,
                                @PathVariable Long reviewId,
                                Model model) {
        model.addAttribute("nickname", nicknameOf(memberDetails));
        model.addAttribute("reviewId", reviewId);
        return "review/detail";
    }

    private String nicknameOf(MemberDetails memberDetails) {
        return memberDetails != null ? memberDetails.getNickname() : null;
    }

}
