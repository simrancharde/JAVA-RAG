package devPilot.backend.security;

import devPilot.backend.entity.User;
import devPilot.backend.services.UserService;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GithubOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserService userService;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User githubUser = delegate.loadUser(userRequest);
        String accessToken = userRequest.getAccessToken().getTokenValue();
        String scopes = userRequest.getAccessToken().getScopes() == null
                ? "read:user,repo"
                : userRequest.getAccessToken().getScopes().stream().collect(Collectors.joining(","));
        User user = userService.upsertFromGithub(Map.copyOf(githubUser.getAttributes()), accessToken, scopes);
        return new AppUserPrincipal(user, githubUser.getAttributes());
    }
}