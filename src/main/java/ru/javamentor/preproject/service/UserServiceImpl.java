package ru.javamentor.preproject.service;

import ru.javamentor.preproject.model.User;
import ru.javamentor.preproject.model.UserDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final String SERVER_URL = "http://localhost:8081/api/admin/";
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    public UserServiceImpl(PasswordEncoder passwordEncoder, RestTemplate restTemplate) {
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<UserDto> getUsers() {
        ResponseEntity<List<UserDto>> response = 
                restTemplate.exchange(SERVER_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDto>>(){});
        return response.getBody();
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> userOptional =
                Optional.ofNullable(restTemplate.getForObject(SERVER_URL +
                        username, User.class));
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("Can`t retrieve UserDetails from server");
        }
        return userOptional.get();
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Optional<UserDto> userDtoOptional =
                Optional.ofNullable(restTemplate.postForObject(SERVER_URL,
                        userDto, UserDto.class));
        if (!userDtoOptional.isPresent()) {
            throw new UsernameNotFoundException("Can`t create User");
        }
        return userDtoOptional.get();
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        if (!userDto.getPassword().isEmpty()) {
            String hashPassword = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(hashPassword);
        }
        ResponseEntity<UserDto> response =
                restTemplate.exchange(SERVER_URL,
                HttpMethod.PUT,
                new HttpEntity<>(userDto),
                UserDto.class);
        Optional<UserDto> userDtoOptional = Optional.ofNullable(response.getBody());
        if (!userDtoOptional.isPresent()) {
            throw new UsernameNotFoundException("Can`t update User");
        }
        return userDtoOptional.get();
    }

    @Override
    public void deleteUser(long id) {
        restTemplate.delete(SERVER_URL + id);
    }
}
