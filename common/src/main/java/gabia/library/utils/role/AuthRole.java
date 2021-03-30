package gabia.library.utils.role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthRole {

    USER("USER"),
    ADMIN("ADMIN");

    private final String authority;
}
