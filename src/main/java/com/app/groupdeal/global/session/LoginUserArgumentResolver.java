package com.app.groupdeal.global.session;

import com.app.groupdeal.global.error.ErrorType;
import com.app.groupdeal.global.error.exception.BusinessException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginUserAnnotation = parameter.hasParameterAnnotation(LoginUser.class);
        boolean isSessionUserType = SessionUser.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginUserAnnotation && isSessionUserType;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return null;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return handleNoSession(parameter);
        }

        SessionUser sessionUser = SessionUser.fromSession(session);
        if (sessionUser == null) {
            return handleNoSession(parameter);
        }

        return sessionUser;
    }

    private Object handleNoSession(MethodParameter parameter) {
        LoginUser annotation = parameter.getParameterAnnotation(LoginUser.class);

        if (annotation != null && annotation.required()) {
            throw new BusinessException(ErrorType.UNAUTHORIZED);
        }

        return null;
    }

}
