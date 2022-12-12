package com.raf.nwpdomaci3.domain.mapper;

import com.raf.nwpdomaci3.domain.dto.user.UserCreateDto;
import com.raf.nwpdomaci3.domain.dto.user.UserDto;
import com.raf.nwpdomaci3.domain.dto.user.UserUpdateDto;
import com.raf.nwpdomaci3.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);


    UserDto userToUserDto(User user);

    User userCreateDtoToUser(UserCreateDto userCreateDto);

    User updateUser(@MappingTarget User user, UserUpdateDto userUpdateDto);

}
