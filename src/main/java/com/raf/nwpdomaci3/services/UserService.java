package com.raf.nwpdomaci3.services;

import com.raf.nwpdomaci3.domain.dto.user.UserCreateDto;
import com.raf.nwpdomaci3.domain.dto.user.UserDto;
import com.raf.nwpdomaci3.domain.dto.user.UserUpdateDto;
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

    public UserDto createUser(UserCreateDto userCreateDto) {
        User user = UserMapper.INSTANCE.userCreateDtoToUser(userCreateDto);
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

    public UserDto updateUserById(Long id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid id"));

        user = UserMapper.INSTANCE.updateUser(user, userUpdateDto);
        return UserMapper.INSTANCE.userToUserDto(userRepository.save(user));
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid id"));

        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> myUser = userRepository.findByEmail(email);
        if(!myUser.isPresent())
            throw new UsernameNotFoundException("User name "+email+" not found");

        return new org.springframework.security.core.userdetails.User(myUser.get().getEmail(), myUser.get().getPassword(), myUser.get().getRoles());
    }
}
