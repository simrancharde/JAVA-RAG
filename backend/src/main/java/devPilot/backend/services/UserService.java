package devPilot.backend.services;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import devPilot.backend.entity.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import devPilot.backend.repository.UserRepository;
import devPilot.backend.utils.TextEncoder;
import devPilot.backend.exception.NotFounfException;

@Service
@RequiredArgsConstructor
public class UserService{
    public final UserRepository userRepository;
    public final TextEncoder textEncoder;
    @Transactional(readOnly = true)
    public User requireBdyId(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFounfException("User not found: " + id));
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