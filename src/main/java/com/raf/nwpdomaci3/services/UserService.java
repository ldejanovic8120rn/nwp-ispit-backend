package com.raf.nwpdomaci3.services;

import com.raf.nwpdomaci3.domain.dto.CreateUserDto;
import com.raf.nwpdomaci3.domain.dto.UserDto;
import com.raf.nwpdomaci3.domain.entities.User;
import com.raf.nwpdomaci3.domain.mapper.UserMapper;
import com.raf.nwpdomaci3.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto createUser(CreateUserDto createUserDto) {
        User user = UserMapper.INSTANCE.userDtoToUser(createUserDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return UserMapper.INSTANCE.userToUserDto(userRepository.save(user));
    }

    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(UserMapper.INSTANCE::userToUserDto).collect(Collectors.toList());
    }

    public UserDto findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserMapper.INSTANCE::userToUserDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid user id"));
    }

    public UserDto findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(UserMapper.INSTANCE::userToUserDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid email"));
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> myUser = userRepository.findByEmail(username);
        if(!myUser.isPresent())
            throw new UsernameNotFoundException("User name "+username+" not found");

        return new org.springframework.security.core.userdetails.User(myUser.get().getEmail(), myUser.get().getPassword(), myUser.get().getRoles());
    }
}
