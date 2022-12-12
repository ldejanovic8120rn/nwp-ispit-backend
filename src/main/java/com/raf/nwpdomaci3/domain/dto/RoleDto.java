package com.raf.nwpdomaci3.domain.dto;

import com.raf.nwpdomaci3.domain.entities.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

    private RoleType role;

//    public RoleType getRole() {
//        return role;
//    }
//
//    public void setRole(RoleType role) {
//        this.role = role;
//    }
}
