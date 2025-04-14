package learn.BasicAuthentication.service;

import jakarta.mail.MessagingException;
import learn.BasicAuthentication.dto.*;
import learn.BasicAuthentication.exception.custom.AdminDoesNotExistException;
import learn.BasicAuthentication.exception.custom.EmailAlreadyExistException;
import learn.BasicAuthentication.exception.custom.InvalidTokenException;
import learn.BasicAuthentication.exception.custom.TokenExpiredException;
import learn.BasicAuthentication.model.Account;
import learn.BasicAuthentication.model.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final TokenService tokenService;

    public void registerAdmin(AdminRegistrationRequest request) throws MessagingException, EmailAlreadyExistException {
        var account = Account.builder()
                .accountName(request.getAccountName())
                .accountEmail(accountService.checkDuplicateEmail(request.getAccountEmail()))
                .accountAdmin(null)
                .accountPassword(passwordEncoder.encode(request.getAccountPassword()))
                .accountRole(request.getAccountRole())
                .accountLocked(false)
                .accountEnabled(false)
                .build();
        accountService.save(account);
        emailService.sendValidationMail(account);
    }

    public void registerUser(UserRegistrationRequest request) throws MessagingException, EmailAlreadyExistException, AdminDoesNotExistException {
        var account = Account.builder()
                .accountName(request.getAccountName())
                .accountEmail(accountService.checkDuplicateEmail(request.getAccountEmail()))
                .accountAdmin(accountService.checkAccountAdmin(request.getAdminEmail()))
                .accountPassword(passwordEncoder.encode(request.getAccountPassword()))
                .accountRole(request.getAccountRole())
                .accountLocked(false)
                .accountEnabled(false)
                .build();
        accountService.save(account);
        emailService.sendValidationMail(account);
    }

    public void activateAccount(VerificationToken token) throws MessagingException, InvalidTokenException, TokenExpiredException {
        Token accountToken = tokenService.findByToken(token.getToken()).orElseThrow(()->new InvalidTokenException("Entered toke is invalid"));
        if(LocalDateTime.now().isAfter(accountToken.getTokenExpiresAt())){
            emailService.sendValidationMail(accountToken.getTokenAccount());
            throw new TokenExpiredException("Activation Token has expired. A new Token has been resent");
        }
        var account = accountService.findById(accountToken.getTokenAccount().getAccountId()).orElseThrow(()->new RuntimeException("User not found"));
        account.setAccountEnabled(true);
        accountService.save(account);
        accountToken.setTokenValidatedAt(LocalDateTime.now());
        tokenService.save(accountToken);
    }

}
