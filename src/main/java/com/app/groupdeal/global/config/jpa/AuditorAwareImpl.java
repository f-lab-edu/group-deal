package com.app.groupdeal.global.config.jpa;

import com.app.groupdeal.global.session.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @NonNull
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return Optional.of("SYSTEM");
            }

            HttpServletRequest request = attributes.getRequest();
            HttpSession session = request.getSession(false);

            if (session == null) {
                return Optional.of("SYSTEM");
            }

            SessionUser sessionUser = SessionUser.fromSession(session);

            if (sessionUser == null) {
                return Optional.of("SYSTEM");
            }

            return Optional.of(String.valueOf(sessionUser.getUserId()));

        } catch (Exception e) {
            return Optional.of("SYSTEM");
        }
    }

}
