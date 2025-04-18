package learn.BasicAuthentication.controller;


import learn.BasicAuthentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class RestAuthenticationController {

    private final AuthenticationService authenticationService;


}
