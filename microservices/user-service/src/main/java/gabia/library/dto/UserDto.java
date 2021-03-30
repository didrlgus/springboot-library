package gabia.library.dto;

import com.fasterxml.jackson.annotation.JsonView;
import gabia.library.config.UserJsonView;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    @JsonView(UserJsonView.Add.class)
    private Long id;

    @JsonView(UserJsonView.Add.class)
    private String authority;

    @JsonView(UserJsonView.Add.class)
    private String identifier;

    private String password;

    @Column(name = "user_name")
    @JsonView(UserJsonView.Default.class)
    private String userName;

    @JsonView(UserJsonView.Modify.class)
    private String email;

    @JsonView(UserJsonView.Default.class)
    private String phone;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}