package learn.BasicAuthentication.exception;

import jakarta.mail.MessagingException;
import learn.BasicAuthentication.exception.custom.AdminDoesNotExistException;
import learn.BasicAuthentication.exception.custom.EmailAlreadyExistException;
import learn.BasicAuthentication.exception.custom.InvalidTokenException;
import learn.BasicAuthentication.exception.custom.TokenExpiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class MVCGlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistException.class)
    public String handleDuplicateEmail(
            EmailAlreadyExistException cause,
            RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage",cause.getMessage());
        return "redirect:/login";
    }

    @ExceptionHandler(AdminDoesNotExistException.class)
    public String handleAdminNotExist(
            AdminDoesNotExistException cause,
            RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage", cause.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(MessagingException.class)
    public String handleMessaging(
            MessagingException cause,
            RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage",cause.getMessage());
        return "redirect:/login";
    }

    @ExceptionHandler(InvalidTokenException.class)
    public String handleInvalidToken(
            InvalidTokenException cause,
            RedirectAttributes redirectAttributes
    ){
        redirectAttributes.addFlashAttribute("errorMessage", cause.getMessage());
        return "redirect:/verify-token";
    }

    @ExceptionHandler(TokenExpiredException.class)
    public String handleTokenExpired(
            TokenExpiredException cause,
            RedirectAttributes redirectAttributes
    ){
        redirectAttributes.addFlashAttribute("errorMessage", cause.getMessage());
        return "redirect:/verify-token";
    }
}
