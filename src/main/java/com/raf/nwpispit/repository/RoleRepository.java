package com.raf.nwpispit.repository;

import com.raf.nwpispit.domain.entities.user.Role;
import com.raf.nwpispit.domain.entities.user.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByRoleIn(List<RoleType> roles);

}
