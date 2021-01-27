package com.study.account;

import com.study.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public Account processNewAccount(SignUpForm signUpForm) {
        //회원생성
        Account newAccount = saveNewAccount(signUpForm);
        //토큰 생성 
        newAccount.generateEmailCheckToken();
        //이메일 전송
        sendSignUpConfirmEmail(newAccount);

        return newAccount;
    }


    private Account saveNewAccount(@ModelAttribute @Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디 회원가입 인증");
        mailMessage.setText("/check-email-token?token="+newAccount.getEmailCheckToken()+ "&email="+newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }


    public void login(Account account) {
        UsernamePasswordAuthenticationToken token  = new UsernamePasswordAuthenticationToken(
                account.getNickname(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
/*
        UsernamePasswordAuthenticationToken token  = new UsernamePasswordAuthenticationToken(
               username,password);

        Authentication authen = authenticationManager.authenticate(token);*/

        SecurityContext context =  SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }
}
