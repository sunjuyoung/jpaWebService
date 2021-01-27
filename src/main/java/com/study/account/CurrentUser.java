package com.study.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER) //파라미터에만 사용가능
//현재 애노테이션 참조하고있는 객체가 anonymousUser라는 문자열이면 null
@AuthenticationPrincipal(expression = "#this == 'anonymousUser'? null : account")
public @interface CurrentUser {

}
