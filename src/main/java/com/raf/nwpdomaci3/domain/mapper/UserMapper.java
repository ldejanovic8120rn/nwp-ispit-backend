package com.raf.nwpdomaci3.domain.mapper;

import com.raf.nwpdomaci3.domain.dto.user.UserCreateDto;
import com.raf.nwpdomaci3.domain.dto.user.UserDto;
import com.raf.nwpdomaci3.domain.dto.user.UserUpdateDto;
import com.raf.nwpdomaci3.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target="userRoles", source="user", qualifiedByName = "getUserRoles")
    UserDto userToUserDto(User user);

    User userCreateDtoToUser(UserCreateDto userCreateDto);

    User updateUser(@MappingTarget User user, UserUpdateDto userUpdateDto);

    @Named("getUserRoles")
    default List<String> getUserRoles(User user) {
        return user.getRoles().stream().map(role -> role.getRole().name()).collect(Collectors.toList());
    }

}
