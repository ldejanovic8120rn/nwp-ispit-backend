package com.raf.nwpispit.bootstrap;

import com.raf.nwpispit.domain.entities.user.Role;
import com.raf.nwpispit.domain.entities.user.RoleType;
import com.raf.nwpispit.domain.entities.user.User;
import com.raf.nwpispit.repository.RoleRepository;
import com.raf.nwpispit.repository.UserRepository;
import com.sun.tools.javac.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapData(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role roleCreate = new Role();
        roleCreate.setRole(RoleType.CAN_CREATE);

        Role roleRead = new Role();
        roleRead.setRole(RoleType.CAN_READ);

        Role roleUpdate = new Role();
        roleUpdate.setRole(RoleType.CAN_UPDATE);

        Role roleDelete = new Role();
        roleDelete.setRole(RoleType.CAN_DELETE);


        User userAdmin = new User();
        userAdmin.setFirstName("Admin");
        userAdmin.setLastName("Admin");
        userAdmin.setEmail("admin@admin.com");
        userAdmin.setPassword(passwordEncoder.encode("admin"));
        userAdmin.setRoles(List.of(roleCreate, roleRead, roleUpdate, roleDelete));

        //posto je u useru @Cascade(CascadeType.PERSIST) -> ne sme da se radi!
//        roleRepository.saveAll(List.of(roleCreate, roleRead, roleUpdate, roleDelete));
        userRepository.save(userAdmin);

        System.out.println("DATA LOADED!");
    }

}
