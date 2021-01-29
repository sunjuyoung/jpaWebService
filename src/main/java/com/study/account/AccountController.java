package com.study.account;

import com.study.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dom4j.rule.Mode;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;


   @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm",new SignUpForm());
        //model.addAttribute(new SignUpForm()); 이름이 같으면 생략 가능
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors){
        if(errors.hasErrors()){
            return "account/sign-up";
        }


        //signUpFormValidator.validate(signUpForm,errors);
        Account account= accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token,String email,Model model){
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        if(account == null){
            model.addAttribute("error","wrong email");
            return view;
        }
        if(!account.getEmailCheckToken().equals(token)){
            model.addAttribute("error","wrong token");
            return view;
        }

        account.completeSignUp();
        accountService.login(account);
        model.addAttribute("numberOfUser",accountRepository.count());
        model.addAttribute("nickname",account.getNickname());
        return view;

    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account, Model model){
       model.addAttribute(account);
        return "account/check-email";
    }

    /**
     * 인증메일 재전송
     * @param account
     * @param model
     * @return
     */
    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account,Model model){

       if(!account.canSendConfirmEmail()){

           model.addAttribute("error","인증메일은 1시간에 한번 전송할 수 있습니다.");
           model.addAttribute(account);
           return "account/check-email";
       }

        accountService.sendSignUpConfirmEmail(account);
       return "redirect:/";

    }




}
