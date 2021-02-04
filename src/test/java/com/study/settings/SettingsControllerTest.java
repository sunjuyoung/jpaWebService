package com.study.settings;

import com.study.WithAccount;
import com.study.account.AccountRepository;
import com.study.account.AccountService;
import com.study.account.SignUpForm;
import com.study.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;


/*    @BeforeEach
    void beforeEach(){
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("syseoz");
        signUpForm.setEmail("syseoz@naver.com");
        signUpForm.setPassword("12341234");
        accountService.processNewAccount(signUpForm);

    }*/

    @AfterEach
    void after(){
        accountRepository.deleteAll();
    }

    //@WithUserDetails(value = "syseoz",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @WithAccount("syseoz")
    @DisplayName("프로필 수정")
    @Test
    public void updateProfile()throws Exception{
        String bio = "소개 테스트";
        mockMvc.perform(post("/settings/profile")
                .param("bio",bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("syseoz");
        assertEquals(bio,account.getBio());

    }

}