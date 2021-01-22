package com.study.account;

import com.study.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    public void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up")).andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("회원가입 처리 , 입력값 오류")
    @Test
    public void signUpSubmitWithError() throws Exception {

        mockMvc.perform(post("/sign-up")
        .param("nickname","sun")
        .param("email","email..")
        .param("password","12341234")
        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("회원가입 처리,메일전송 확인")
    @Test
    public void signUpSubmit() throws Exception {

        mockMvc.perform(post("/sign-up")
                .param("nickname","sun")
                .param("email","test@test.com")
                .param("password","12341234")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        Account account =  accountRepository.findByEmail("test@test.com");

        assertNotNull(account);
        assertNotNull(account.getEmailCheckToken());
        assertNotEquals(account.getEmail(),"12341234"); //패스워드 인코딩 확인
        assertTrue(accountRepository.existsByEmail("test@test.com"));
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}