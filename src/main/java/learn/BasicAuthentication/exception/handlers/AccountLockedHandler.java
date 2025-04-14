package learn.BasicAuthentication.exception.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AccountLockedHandler implements AuthExceptionHandler {

    private final RedirectStrategy redirectStrategy;

    @Override
    public Class<? extends AuthenticationException> getExceptionType() {
        return LockedException.class;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        FlashMap outputFlashMap = new FlashMap();
        outputFlashMap.put("errorMessage", "Your account is locked. Please contact support");
        new SessionFlashMapManager().saveOutputFlashMap(outputFlashMap, request, response);
        redirectStrategy.sendRedirect(request,response,"/login");
    }
}
