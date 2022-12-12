package com.raf.nwpdomaci3.domain.entities;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority {
    CAN_CREATE("CAN_CREATE"),
    CAN_READ("CAN_READ"),
    CAN_UPDATE("CAN_UPDATE"),
    CAN_DELETE("CAN_DELETE");


    private final String role;

    RoleType(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }

}
