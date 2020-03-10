package com.fydp.backend.service;

import com.fydp.backend.model.GoogleOAuth2UserInfo;
import com.fydp.backend.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOidcUserService.class);

    @Autowired
    private UserServiceImpl userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("Loading user and save");
        OidcUser oidcUser = super.loadUser(userRequest);
        var attributes = oidcUser.getAttributes();

        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo();
        userInfo.setId((String) attributes.get("sub"));
        userInfo.setName((String) attributes.get("name"));
        userInfo.setEmail((String) attributes.get("email"));
        saveUser(userInfo);

        return oidcUser;
    }

    private void saveUser(GoogleOAuth2UserInfo userInfo) {
        Optional<User> optionalUser = userService.findById(userInfo.getId());
        if (!optionalUser.isPresent()) {
            User user = new User();
            user.setId(userInfo.getId());
            user.setName(userInfo.getName());
            user.setEmail(userInfo.getEmail());
            userService.saveUser(user);
        }
    }
}
