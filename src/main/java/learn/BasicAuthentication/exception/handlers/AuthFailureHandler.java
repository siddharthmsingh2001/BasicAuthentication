package learn.BasicAuthentication.exception.handlers;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthFailureHandler implements AuthenticationFailureHandler {

    private final List<AuthExceptionHandler> exceptionHandlers;
    private final UnknownAuthExceptionHandler unknownHandler;

    private final Map<Class<? extends AuthenticationException>, AuthExceptionHandler> handlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (AuthExceptionHandler handler : exceptionHandlers) {
            handlerMap.put(handler.getExceptionType(), handler);
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        AuthExceptionHandler handler = handlerMap.getOrDefault(exception.getClass(), unknownHandler);
        handler.handle(request, response, exception);
    }
}
