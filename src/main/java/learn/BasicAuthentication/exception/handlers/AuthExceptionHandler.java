package learn.BasicAuthentication.exception.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

public interface AuthExceptionHandler {

    Class<? extends AuthenticationException> getExceptionType();

    void handle(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException;
}
