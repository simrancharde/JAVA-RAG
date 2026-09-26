package devPilot.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final GithubOAuth2UserService githubOAuth2UserService;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf().disable()
            .sessionManagement(session->session).sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .authorizeRequests(auth->auth.requestMatchers(
                ...patterns:"api/auth/login-url",
                "/oauth2/**",
                "/login/oauth2/**",
                "/error"
            ))
            .permitAll()
            .requestMatchers(HttpMethod.OPTIONS, ...patterns:"/**").permitAll()
            .requestMatchers(...patterns:"/api/**").authenticated()
            .anyRequest().permitAll()
          .exceptionHandling(ex->ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
          .oauth2Login(oauth->oauth.userInfoEndpoint(userInfo->userInfo.userService(githubOAuth2UserService)));
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
            .logout(logout->logout
                .logoutUrl(logoutUrl:"/api/auth/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpStatus.NO_CONTENT.value() );
                })
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies(...cookiesNamestoCLear:"DEV_PILOT_SESSION");
         
            );
            return http.build();
    }
    @Bean 
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        @Value("${app.frontend-url}") String frontendUrl) {
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler(frontendUrl);
         successHandler.setDefaultTargetUrl(frontendUrl + "/auth/callback");
        return successHandler;
    }

        @Bean
        AuthenticationFailureHandler authenticationFailureHandler() {
             @Value("${app.frontend-url}") String frontendUrl) {
        SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler(frontendUrl);
         failureHandler.setDefaultFailureUrl(frontendUrl + "/login?error=ouath_failure");
        return failureHandler;
    }
}