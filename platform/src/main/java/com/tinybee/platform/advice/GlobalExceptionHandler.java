package com.tinybee.platform.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tinybee.platform.enums.ResponseEnum;
import com.tinybee.platform.excepiton.GlobalException;
import com.tinybee.platform.responses.ResponseData;

@ControllerAdvice
public class GlobalExceptionHandler
{
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseData handlerException(Exception e)
	{
		if (e instanceof GlobalException)
		{
			return ResponseData.create(ResponseEnum.WORK_ERROR.getCode(), e.getMessage());
		}
		else
		{
			return ResponseData.create(ResponseEnum.SYSTEM_ERROR.getCode(), e.getMessage());
		}
	}
}
