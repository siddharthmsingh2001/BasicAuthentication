package learn.BasicAuthentication.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import learn.BasicAuthentication.model.Account;
import learn.BasicAuthentication.model.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Async
public class EmailService {

    private final SecureRandom secureRandom;
    private final TokenService tokenService;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final HashSet<String> generatedTokens;
    @Value("$application.mailing.route.activation-url")
    private String activationURL;


    public void sendValidationMail(Account account) throws MessagingException {
        var token = generateAndSaveToken(account);
        sendMail(
                account.getAccountEmail(),
                account.getAccountName(),
                EmailTemplateName.ACTIVATION_MAIL,
                activationURL,
                token,
                "Verification code for Account Activation"
        );
    }

    private void sendMail(
            String to,
            String accountName,
            EmailTemplateName emailTemplate,
            String confirmationURL,
            String activationCode,
            String subject
    ) throws MessagingException {
        String templateName;
        if (emailTemplate == null) { templateName = "activation-mail";}
        else { templateName = emailTemplate.getFile();}

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        Map<String,Object> properties = new HashMap<>();
        properties.put("accountName",accountName);
        properties.put("confirmationURL",confirmationURL);
        properties.put("activationCode",activationCode);

        Context thymeleafContext =  new Context();
        thymeleafContext.setVariables(properties);

        mimeMessageHelper.setFrom("siddharthmsingh2001@gmail.com");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);

        mimeMessageHelper.setText(templateEngine.process(templateName, thymeleafContext), true);
        mailSender.send(mimeMessage);
    }

    private String generateAndSaveToken(Account account){
        String generatedToken = generateToken(6);
        var token = Token.builder()
                .token(generatedToken)
                .tokenCreatedAt(LocalDateTime.now())
                .tokenExpiresAt(LocalDateTime.now().plusMinutes(10))
                .tokenAccount(account)
                .build();
        tokenService.save(token);
        return generatedToken;
    }

    private String generateToken(int length) {
        String characters = "0123456789";
        StringBuilder tokenBuilder = new StringBuilder();
        String token;
        do{
            tokenBuilder.setLength(0);
            for (int i = 0; i < length; i++) {
                int randomIndex = secureRandom.nextInt(characters.length());
                tokenBuilder.append(characters.charAt(randomIndex));
            }
            token = tokenBuilder.toString();
        } while(!generatedTokens.add(token));
        return token;
    }

    @Getter
    public enum EmailTemplateName{

        ACTIVATION_MAIL("activation-mail");

        private final String file;

        EmailTemplateName(String file) {
            this.file = file;
        }
    }
}
