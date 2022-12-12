package com.raf.nwpdomaci3.utils;

import com.raf.nwpdomaci3.domain.entities.RoleType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class PermissionUtils {

    public static final String permissionMessage = "Don't have permission";

    public static boolean hasPermission(RoleType role) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return authorities.contains(role);
    }

}
