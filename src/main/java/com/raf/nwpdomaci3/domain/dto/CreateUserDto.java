package com.raf.nwpdomaci3.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<RoleDto> roles;

}
