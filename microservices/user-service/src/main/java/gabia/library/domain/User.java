package gabia.library.domain;

import gabia.library.config.BaseTimeEntity;
import gabia.library.dto.UserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 15, nullable = false)
    private String authority;

    @Column(length = 25, nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_name", length = 15, nullable = false)
    private String userName;

    @Column(nullable = false)
    private String email;

    @Column(length = 15)
    private String phone;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted;

    @Builder
    public User(String authority, String identifier, String password, String userName, String email, String phone) {
        this.authority = authority;
        this.identifier = identifier;
        this.password = password;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
    }

    public void updateUser(UserDto userDto){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.password = bCryptPasswordEncoder.encode(userDto.getPassword());
        this.userName = userDto.getUserName();
        this.email = userDto.getEmail();
        this.phone = userDto.getPhone();
    }
}
