package com.raf.nwpispit.domain.dto.user;

import com.raf.nwpispit.domain.entities.user.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

    private RoleType role;

}
