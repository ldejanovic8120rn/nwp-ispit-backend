package com.raf.nwpdomaci3.domain.dto.user;

import com.raf.nwpdomaci3.domain.entities.user.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

    private RoleType role;

}
