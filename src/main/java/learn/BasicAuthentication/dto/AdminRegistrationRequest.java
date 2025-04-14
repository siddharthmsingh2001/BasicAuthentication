package learn.BasicAuthentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.io.Serializable;
import learn.BasicAuthentication.model.Role;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdminRegistrationRequest implements Serializable {

    @NotBlank(message = "Account Name mandatory")
    @Size(min = 2, message = "Name should be minimum 1 character")
    private String accountName;

    @NotBlank(message = "Account Email mandatory")
    @Email(message = "Invalid Email")
    private String accountEmail;

    @NotBlank(message = "Password mandatory")
    @Size(min = 1, message = "Password Length should be minimum 1 character")
    private String accountPassword;

    private Role accountRole = Role.ADMIN;
}
