package com.raf.nwpdomaci3.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    private String firstName;
    private String lastName;
    private String email;
    private List<RoleDto> roles;

}
