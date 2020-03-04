package com.fydp.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    @Value(value = "${brevity.frontend.url}")
    private String targetUrl;

    @Value(value = "${jwt.secret}")
    private String secret;

    @Value(value = "${jwt.expireTime}")
    private long expireTime;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("Authenticate successfully");

        String url = buildUrl(authentication);
        getRedirectStrategy().sendRedirect(request, response, url);
    }

    private String createToken(Authentication authentication) {
        logger.info("Creating token");
        var user = (DefaultOidcUser) authentication.getPrincipal();
        var attributes = user.getAttributes();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setSubject((String)attributes.get("sub"))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private String buildUrl(Authentication authentication) {
        logger.info("Building URL");
        String token = createToken(authentication);
        return UriComponentsBuilder.fromUriString(targetUrl + "/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();
    }
}
