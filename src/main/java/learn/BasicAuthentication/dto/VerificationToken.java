package learn.BasicAuthentication.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VerificationToken {
    private String token;
}
