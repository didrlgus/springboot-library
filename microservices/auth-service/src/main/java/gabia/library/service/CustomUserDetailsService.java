package gabia.library.service;

import gabia.library.config.CustomUserDetails;
import gabia.library.config.UserCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static gabia.library.config.CommonUrlPathPrefix.USER_SERVICE_PREFIX;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final static String GET_AUTH_USER_URL = USER_SERVICE_PREFIX + "/login?identifier=";

    private final RestTemplate restTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ResponseEntity<UserCredentials> entity = getAuthUserEntityByUsername(username);

        UserCredentials userCredentials = entity.getBody();

        if(isNull(userCredentials) || !userCredentials.getIdentifier().equals(username)) {
            throw new UsernameNotFoundException("Username: " + username + " not found");
        }

        return new CustomUserDetails(userCredentials.getId(), userCredentials.getIdentifier(), userCredentials.getPassword(), getAuthorityList(userCredentials));
    }

    public ResponseEntity<UserCredentials> getAuthUserEntityByUsername(String username) {
        return restTemplate.getForEntity(GET_AUTH_USER_URL + username, UserCredentials.class);
    }

    public List<GrantedAuthority> getAuthorityList(UserCredentials authUser) {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authUser.getAuthority());
    }

}
