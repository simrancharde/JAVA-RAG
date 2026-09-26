package devPilot.backend.services;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import devPilot.backend.entity.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import devPilot.backend.repository.UserRepository;
import devPilot.backend.utils.TextEncoder;
import devPilot.backend.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class UserService{
   public User upsertFromGithub(Map<String, Object> attributes, String accessToken, String scopes) {
    Long githubId = toLong(attributes.get("id"));
    String login = (String) attributes.get("login");
    String name = (String) attributes.get("name");
    String email = (String) attributes.get("email");
    String avatarUrl = (String) attributes.get("avatar_url");
String encryptedAccessToken = textEncoder.encrypt(accessToken);
    User user = userRepository.findByGithubId(githubId).orElseGet(() -> {
        User newUser = new User();
        newUser.setGithubId(githubId);
        return newUser;
    });

    user.setLogin(login);
    user.setName(name);
    user.setEmail(email);
    user.setAvatarUrl(avatarUrl);
    user.setAccessToken(encryptedAccessToken);
    user.setScopes(scopes);

    return userRepository.save(user);
   }

    public final UserRepository userRepository;
    public final TextEncoder textEncoder;
    @Transactional(readOnly = true)
    public User requireBdyId(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    public String decryptAccessToken(User user) {
        return textEncoder.decrypt(user.getAccessToken());


    }

    private static Long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());


        
    }}