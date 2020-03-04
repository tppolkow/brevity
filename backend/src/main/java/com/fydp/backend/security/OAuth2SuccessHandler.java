package com.fydp.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    @Value(value = "${brevity.frontend.url}")
    private String targetUrl;

    @Autowired
    private TokenUtil tokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("Authenticate successfully");

        String url = buildUrl(authentication);
        getRedirectStrategy().sendRedirect(request, response, url);
    }

    private String buildUrl(Authentication authentication) {
        logger.info("Building URL");
        String token = tokenUtil.createToken(authentication);
        return UriComponentsBuilder.fromUriString(targetUrl + "/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();
    }
}
