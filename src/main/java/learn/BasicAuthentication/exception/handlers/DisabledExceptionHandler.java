package learn.BasicAuthentication.exception.handlers;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learn.BasicAuthentication.model.Account;
import learn.BasicAuthentication.service.AccountService;
import learn.BasicAuthentication.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DisabledExceptionHandler implements AuthExceptionHandler {

    private final AccountService accountService;
    private final EmailService emailService;
    private final RedirectStrategy redirectStrategy;

    @Override
    public Class<? extends AuthenticationException> getExceptionType() {
        return DisabledException.class;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException{

        String username = request.getParameter("accountEmail");
        Account account = (Account) accountService.loadUserByUsername(username);
        try {
            emailService.sendValidationMail(account);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
        FlashMap outputFlashMap = new FlashMap();
        outputFlashMap.put("errorMessage", "Your account is disabled, Please enable it");
        new SessionFlashMapManager().saveOutputFlashMap(outputFlashMap, request, response);
        redirectStrategy.sendRedirect(request,response,"/verify-token");
    }
}
