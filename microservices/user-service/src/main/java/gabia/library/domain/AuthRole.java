package gabia.library.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String authority;
}