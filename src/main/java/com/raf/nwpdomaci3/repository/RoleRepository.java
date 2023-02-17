package com.raf.nwpdomaci3.repository;

import com.raf.nwpdomaci3.domain.entities.user.Role;
import com.raf.nwpdomaci3.domain.entities.user.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByRoleIn(List<RoleType> roles);

}
