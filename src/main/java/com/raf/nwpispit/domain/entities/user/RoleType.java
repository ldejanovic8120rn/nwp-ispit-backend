package com.raf.nwpispit.domain.entities.user;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority {
    CAN_CREATE("CAN_CREATE"),
    CAN_READ("CAN_READ"),
    CAN_UPDATE("CAN_UPDATE"),
    CAN_DELETE("CAN_DELETE"),

    CAN_SEARCH_MACHINE("CAN_SEARCH_MACHINE"),
    CAN_START_MACHINE("CAN_START_MACHINE"),
    CAN_STOP_MACHINE("CAN_STOP_MACHINE"),
    CAN_RESTART_MACHINE("CAN_RESTART_MACHINE"),
    CAN_CREATE_MACHINE("CAN_CREATE_MACHINE"),
    CAN_DESTROY_MACHINE("CAN_DESTROY_MACHINE");


    private final String role;

    RoleType(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }

}
