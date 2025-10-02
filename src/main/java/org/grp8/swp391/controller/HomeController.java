package org.grp8.swp391.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "Trang public <a href='/oauth2/authorization/google'>Login với Google</a>";
    }

    @GetMapping("/home")
    public String home(OAuth2AuthenticationToken authentication) {
        String name = authentication.getPrincipal().getAttribute("name");
        String email = authentication.getPrincipal().getAttribute("email");
        return "Xin chào " + name + " (" + email + ")";
    }
}
