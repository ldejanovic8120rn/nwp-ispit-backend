package com.raf.nwpdomaci3.domain.mapper;

import com.raf.nwpdomaci3.domain.dto.CreateUserDto;
import com.raf.nwpdomaci3.domain.dto.UserDto;
import com.raf.nwpdomaci3.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userToUserDto(User user);

    User userDtoToUser(CreateUserDto createUserDto);

}
