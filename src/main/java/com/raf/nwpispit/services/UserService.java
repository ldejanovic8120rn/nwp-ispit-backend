package com.raf.nwpispit.services;

import com.raf.nwpispit.domain.dto.user.UserCreateDto;
import com.raf.nwpispit.domain.dto.user.UserDto;
import com.raf.nwpispit.domain.dto.user.UserUpdateDto;
import com.raf.nwpispit.domain.entities.user.Role;
import com.raf.nwpispit.domain.entities.user.RoleType;
import com.raf.nwpispit.domain.entities.user.User;
import com.raf.nwpispit.domain.exceptions.NotFoundException;
import com.raf.nwpispit.domain.mapper.UserMapper;
import com.raf.nwpispit.repository.RoleRepository;
import com.raf.nwpispit.repository.UserRepository;
import com.raf.nwpispit.utils.PermissionUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto createUser(UserCreateDto userCreateDto) {
        PermissionUtils.checkRole(RoleType.CAN_CREATE);

        List<Role> roles = roleRepository.findAllByRoleIn(userCreateDto.getUserRoles().stream().map(RoleType::valueOf).collect(Collectors.toList()));
        User user = UserMapper.INSTANCE.userCreateDtoToUser(userCreateDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles);

        return UserMapper.INSTANCE.userToUserDto(userRepository.save(user));
    }

    public List<UserDto> findAllUsers() {
        PermissionUtils.checkRole(RoleType.CAN_READ);

        return userRepository.findAll().stream().map(UserMapper.INSTANCE::userToUserDto).collect(Collectors.toList());
    }

    public UserDto findUserById(Long id) {
        PermissionUtils.checkRole(RoleType.CAN_READ);

        Optional<User> user = userRepository.findById(id);
        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundException("invalid user id"));
    }

    public UserDto findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundException("invalid email"));
    }

    public UserDto updateUserById(Long id, UserUpdateDto userUpdateDto) {
        PermissionUtils.checkRole(RoleType.CAN_UPDATE);

        List<Role> roles = roleRepository.findAllByRoleIn(userUpdateDto.getUserRoles().stream().map(RoleType::valueOf).collect(Collectors.toList()));
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("invalid user id"));

        user = UserMapper.INSTANCE.updateUser(user, userUpdateDto);
        user.setRoles(roles);

        return UserMapper.INSTANCE.userToUserDto(userRepository.save(user));
    }

    public void deleteUserById(Long id) {
        PermissionUtils.checkRole(RoleType.CAN_DELETE);

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("invalid user id"));
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> myUser = userRepository.findByEmail(email);
        if(!myUser.isPresent())
            throw new UsernameNotFoundException("User name " + email + " not found");

        List<RoleType> roles = myUser.get().getRoles().stream().map(Role::getRole).collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(myUser.get().getEmail(), myUser.get().getPassword(), roles);
    }
}
