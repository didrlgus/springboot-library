package gabia.library.service;

import com.fasterxml.jackson.annotation.JsonView;
import gabia.library.config.UserJsonView;
import gabia.library.controller.UserController;
import gabia.library.domain.User;
import gabia.library.domain.UserRepository;
import gabia.library.dto.UserDto;
import gabia.library.exception.EntityNotFoundException;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static gabia.library.exception.message.CommonExceptionMessage.ENTITY_NOT_FOUND;

@ActiveProfiles({ "test", "dev" })
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserController userController;

    @Autowired
    ModelMapper modelMapper;

    @AfterEach
    public void after() {
        userRepository.deleteAll();
    }

    @Test
    public void 회원가입() throws Exception{
        // given
        User u1 = User.builder()
                .authority("ROLE_USER")
                .identifier("TestTest")
                .password("gabia")
                .userName("Matt")
                .email("aaa@naver.com")
                .phone("010-1111-1111")
                .build();

        //when
        User user = userRepository.save(u1);

        //then
        ModelMapper mo = new ModelMapper();
        Assertions.assertEquals(userService.findUser(user.getId()).getUserName(), mo.map(u1, UserDto.class).getUserName(), "fail" );
    }

    @Test
    @JsonView(UserJsonView.Default.class)
    public void 전체조회() throws Exception{
        //given
        User u1 = User.builder()
                .authority("ROLE_USER")
                .identifier("gabia")
                .password("gabia")
                .userName("Matt")
                .email("aaa@naver.com")
                .phone("010-1111-1111")
                .build();

        User u2 = User.builder()
                .authority("ROLE_USER")
                .identifier("gabia2")
                .password("gabia2")
                .userName("Matt2")
                .email("bbb@naver.com")
                .phone("010-2222-2222")
                .build();
        //when

        userRepository.save(u1);
        userRepository.save(u2);

        //then
        Optional<User> r1 = userRepository.findById(1L);
        Optional<User> r2 = userRepository.findById(2L);

        Assertions.assertEquals(u1.getUserName(), r1.get().getUserName(), "fail");
        Assertions.assertEquals(u2.getUserName(), r2.get().getUserName(), "fail");
    }

    @Test
    @JsonView(UserJsonView.Modify.class)
    public void 회원수정() throws Exception{
        // given
        User u1 = User.builder()
                .authority("ROLE_USER")
                .identifier("gabia")
                .password("gabia")
                .userName("Matt")
                .email("aaa@naver.com")
                .phone("010-1111-1111")
                .build();

        User u2 = User.builder()
                .authority("ROLE_USER")
                .identifier("gabia2")
                .password("gabia2")
                .userName("Matt2")
                .email("bbb@naver.com")
                .phone("010-2222-2222")
                .build();


        //when
        u1 = userRepository.save(u1);
        UserDto userDto = modelMapper.map(u2, UserDto.class);
        u1.updateUser(userDto);

        //then
        Assertions.assertEquals(u1.getUserName(), u2.getUserName(),"fail");
        Assertions.assertEquals(u1.getEmail(), u2.getEmail(),"fail");
        Assertions.assertEquals(u1.getPhone(), u2.getPhone(),"fail");
    }

    @Test
    @JsonView(UserJsonView.Modify.class)
    public void 회원삭제() throws Exception{
        //given
        User u1 = User.builder()
                .authority("ROLE_USER")
                .identifier("gabia")
                .password("gabia")
                .userName("Matt")
                .email("aaa@naver.com")
                .phone("010-1111-1111")
                .build();

        userRepository.save(u1);
        User user = userRepository.findByIdentifier(u1.getIdentifier()).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        //when
        Long result = userRepository.count();

        userService.removeUser(user.getId());

        Assertions.assertEquals(result - 1, userRepository.count(), "false");
    }
}
