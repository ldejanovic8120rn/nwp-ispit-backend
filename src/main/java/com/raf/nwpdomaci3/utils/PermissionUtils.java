package com.raf.nwpdomaci3.utils;

import com.raf.nwpdomaci3.domain.entities.user.RoleType;
import com.raf.nwpdomaci3.domain.exceptions.PermissionException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class PermissionUtils {

    public static final String permissionMessage = "Don't have permission";

    private static boolean hasPermission(RoleType role) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return authorities.contains(role);
    }

    public static void checkRole(RoleType role) {
        if(!hasPermission(role))
            throw new PermissionException(permissionMessage);
    }

}
