package gabia.library.controller;

import com.fasterxml.jackson.annotation.JsonView;
import gabia.library.config.UserJsonView;
import gabia.library.dto.UserDto;
import gabia.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    @JsonView(UserJsonView.Add.class)
    public ResponseEntity<UserDto> addUser(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(userService.addUser(userDto));
    }

    @GetMapping("/users")
    @JsonView(UserJsonView.Add.class)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/users/{id}")
    @JsonView(UserJsonView.Add.class)
    public ResponseEntity<UserDto> findUser(@PathVariable("id") Long id){
        return ResponseEntity.ok(userService.findUser(id));
    }

    @PutMapping("/users/{id}")
    @JsonView(UserJsonView.Add.class)
    public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserDto userDto){
        return ResponseEntity.ok(userService.updateUser(userDto));
    }

    @DeleteMapping("/users/{id}")
    @JsonView(UserJsonView.Modify.class)
    public ResponseEntity<UserDto> removeUser(@PathVariable("id") Long id){
        return ResponseEntity.ok(userService.removeUser(id));
    }

    @GetMapping("/login")
    public ResponseEntity<UserDto> getUser(@RequestParam("identifier") String identifier) {
        return ResponseEntity.ok(userService.Login(identifier));
    }
}
