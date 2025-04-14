package learn.BasicAuthentication.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import learn.BasicAuthentication.dto.AdminRegistrationRequest;
import learn.BasicAuthentication.dto.AuthenticationRequest;
import learn.BasicAuthentication.dto.UserRegistrationRequest;
import learn.BasicAuthentication.dto.VerificationToken;
import learn.BasicAuthentication.exception.custom.AdminDoesNotExistException;
import learn.BasicAuthentication.exception.custom.EmailAlreadyExistException;
import learn.BasicAuthentication.exception.custom.InvalidTokenException;
import learn.BasicAuthentication.exception.custom.TokenExpiredException;
import learn.BasicAuthentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RedirectStrategy redirectStrategy;

    @GetMapping()
    public String homeView(Authentication authentication){
        if(authentication!=null){
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_USER");
            if ("ROLE_ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            } else if ("ROLE_USER".equals(role)) {
                return "redirect:/user/dashboard";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("login")
    public String showLoginPage(
            Model model
    ){
        if(!model.containsAttribute("authenticationRequest")){
            model.addAttribute("authenticationRequest", new AuthenticationRequest());
        }
        return "login";
    }

    @GetMapping("register")
    public String showRegistrationPage(
            Model model
    ){
        if(!model.containsAttribute("adminRegistrationRequest")){
            model.addAttribute("adminRegistrationRequest", new AdminRegistrationRequest());
        }
        if(!model.containsAttribute("userRegistrationRequest")){
            model.addAttribute("userRegistrationRequest", new UserRegistrationRequest());
        }
        return "registration";
    }

    @PostMapping("register-admin")
    public String registerAdmin(
        @ModelAttribute("adminRegistrationRequest") @Valid AdminRegistrationRequest registrationRequest,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    )throws EmailAlreadyExistException, MessagingException {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.adminRegistrationRequest", bindingResult);
            redirectAttributes.addFlashAttribute("adminRegistrationRequest", registrationRequest);
            return "redirect:/register";
        }
        authenticationService.registerAdmin(registrationRequest);
        redirectAttributes.addFlashAttribute("accountName", registrationRequest.getAccountName());
        return "redirect:/verify-token";
    }

    @PostMapping("register-user")
    public String registerUser(
            @ModelAttribute("userRegistrationRequest") @Valid UserRegistrationRequest registrationRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) throws EmailAlreadyExistException, MessagingException, AdminDoesNotExistException {
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegistrationRequest", bindingResult);
            redirectAttributes.addFlashAttribute("adminRegistrationRequest", registrationRequest);
            return "redirect:/register";
        }
        authenticationService.registerUser(registrationRequest);
        redirectAttributes.addFlashAttribute("accountName", registrationRequest.getAccountName());
        return "redirect:/verify-token";
    }

    @GetMapping("verify-token")
    public String showTokenVerificationPage(
        Model model
    ){
        if(!model.containsAttribute("verificationToken")){
            model.addAttribute("verificationToken", new VerificationToken());
        }
        return "token-verification";
    }

    @PostMapping("verify-token")
    public String processToken(
            @ModelAttribute("verificationToken") VerificationToken token,
            @ModelAttribute("accountName") String accountName,
            RedirectAttributes redirectAttributes,
            Model model
    ) throws MessagingException, InvalidTokenException, TokenExpiredException {
        authenticationService.activateAccount(token);
        redirectAttributes.addFlashAttribute("accountName",accountName);
        return "registration-success";
    }
}
