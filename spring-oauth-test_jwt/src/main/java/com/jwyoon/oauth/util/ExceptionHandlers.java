package com.jwyoon.oauth.util;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jwyoon.oauth.error.PasswordNotMatchException;

@RestControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler(PasswordNotMatchException.class)
	public String passwordWrongException(PasswordNotMatchException ex) {
		return "{code:500,msg:��й�ȣ �Է��� ���� �ʽ��ϴ�. }";
	}
}
