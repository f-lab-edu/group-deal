package com.app.groupdeal.global.session;

import com.app.groupdeal.global.constant.SessionConstants;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SessionUser implements Serializable {
    private Long userId;
    private String email;
    private String nickname;

    public static SessionUser from(Long userId, String email, String nickname) {
        return new SessionUser(userId, email, nickname);
    }

    public void saveToSession(HttpSession session) {
        session.setAttribute(SessionConstants.USER, this);
    }

    public static SessionUser fromSession(HttpSession session) {
        Object attribute = session.getAttribute(SessionConstants.USER);
        if (attribute instanceof SessionUser) {
            return (SessionUser) attribute;
        }
        return null;
    }
}
