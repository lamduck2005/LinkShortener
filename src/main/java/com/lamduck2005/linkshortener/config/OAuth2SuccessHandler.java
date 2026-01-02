package com.lamduck2005.linkshortener.config;

import com.lamduck2005.linkshortener.dto.response.JwtResponse;
import com.lamduck2005.linkshortener.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        JwtResponse jwtResponse = authService.handleOAuth2Login(email, oauth2User);

        String token = URLEncoder.encode(jwtResponse.token(), StandardCharsets.UTF_8);
        String userInfoJson = buildUserInfoJson(jwtResponse.userInfo());

        String redirectUrl = String.format("%s/oauth2/callback?token=%s&userInfo=%s",
                frontendUrl, token, URLEncoder.encode(userInfoJson, StandardCharsets.UTF_8));

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String buildUserInfoJson(JwtResponse.UserInfo userInfo) {
        StringBuilder rolesJson = new StringBuilder("[");
        List<String> roles = userInfo.roles();
        for (int i = 0; i < roles.size(); i++) {
            if (i > 0) rolesJson.append(",");
            rolesJson.append("\"").append(roles.get(i)).append("\"");
        }
        rolesJson.append("]");

        return String.format(
                "{\"id\":%d,\"email\":\"%s\",\"username\":\"%s\",\"roles\":%s}",
                userInfo.id(),
                userInfo.email(),
                userInfo.username(),
                rolesJson.toString()
        );
    }
}

