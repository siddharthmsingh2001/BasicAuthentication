package learn.BasicAuthentication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserDashboardController {

    @GetMapping("/dashboard")
    public String userDashboard(){
        return"user-dashboard";
    }

}
