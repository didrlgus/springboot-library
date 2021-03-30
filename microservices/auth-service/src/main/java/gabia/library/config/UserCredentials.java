package gabia.library.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCredentials {
    private Long id;
    private String identifier;
    private String password;
    private String authority;
}
