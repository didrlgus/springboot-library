package gabia.library.service;

import gabia.library.domain.AuthRole;
import gabia.library.domain.User;
import gabia.library.domain.UserRepository;
import gabia.library.dto.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;

    public UserDto addUser(UserDto userDto) {

        boolean isExistUser = userRepository.existsByIdentifier(userDto.getIdentifier());

        if (isExistUser) {
            throw new RuntimeException("현재 사용중인 아이디 입니다.");
        }

        return getAddUserResultMap(saveAndGetNewUser(userDto));
    }

    // 회원 가입
    private User saveAndGetNewUser(UserDto userDto) {

        return userRepository.save(User.builder()
                .authority(AuthRole.USER.getAuthority())
                .identifier(userDto.getIdentifier())
                .password(bCryptPasswordEncoder.encode(userDto.getPassword()))
                .userName(userDto.getUserName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .build());
    }

    // 회원 가입 User 정보
    private UserDto getAddUserResultMap(User newUser) {
        return modelMapper.map(newUser, UserDto.class);
    }

    // 회원 전체 조회
    public List<UserDto> findAll(){
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    // 회원 상세 조회
    public UserDto findUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));

        return modelMapper.map(user, UserDto.class);
    }

    // 회원 수정
    @Transactional
    public UserDto updateUser(UserDto userDto){
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));

        user.updateUser(userDto);

        return modelMapper.map(user, UserDto.class);
    }

    //회원 삭제
    public UserDto removeUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));

        userRepository.deleteById(id);

        return modelMapper.map(user, UserDto.class);
    }

    public UserDto Login(String identifier){
        User authuser = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));

        return modelMapper.map(authuser, UserDto.class);
    }
}
