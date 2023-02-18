package com.raf.nwpispit.bootstrap;

import com.raf.nwpispit.domain.entities.machine.Machine;
import com.raf.nwpispit.domain.entities.machine.MachineAction;
import com.raf.nwpispit.domain.entities.machine.MachineSchedule;
import com.raf.nwpispit.domain.entities.user.Role;
import com.raf.nwpispit.domain.entities.user.RoleType;
import com.raf.nwpispit.domain.entities.user.User;
import com.raf.nwpispit.repository.MachineRepository;
import com.raf.nwpispit.repository.MachineScheduleRepository;
import com.raf.nwpispit.repository.RoleRepository;
import com.raf.nwpispit.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MachineRepository machineRepository;
    private final MachineScheduleRepository machineScheduleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapData(UserRepository userRepository, MachineRepository machineRepository, MachineScheduleRepository machineScheduleRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.machineRepository = machineRepository;
        this.machineScheduleRepository = machineScheduleRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User userAdmin = new User();

        //ROLES
        Role roleCreate = new Role();
        roleCreate.setRole(RoleType.CAN_CREATE);

        Role roleRead = new Role();
        roleRead.setRole(RoleType.CAN_READ);

        Role roleUpdate = new Role();
        roleUpdate.setRole(RoleType.CAN_UPDATE);

        Role roleDelete = new Role();
        roleDelete.setRole(RoleType.CAN_DELETE);

        Role roleCanSearchMachine = new Role();
        roleCanSearchMachine.setRole(RoleType.CAN_SEARCH_MACHINE);

        Role roleCanCreateMachine = new Role();
        roleCanCreateMachine.setRole(RoleType.CAN_CREATE_MACHINE);

        Role roleCanDestroyMachine = new Role();
        roleCanDestroyMachine.setRole(RoleType.CAN_DESTROY_MACHINE);

        Role roleCanStartMachine = new Role();
        roleCanStartMachine.setRole(RoleType.CAN_START_MACHINE);

        Role roleCanStopMachine = new Role();
        roleCanStopMachine.setRole(RoleType.CAN_STOP_MACHINE);

        Role roleCanRestartMachine = new Role();
        roleCanRestartMachine.setRole(RoleType.CAN_RESTART_MACHINE);

        java.util.List<Role> roles = new ArrayList<>();
        roles.add(roleCreate);
        roles.add(roleRead);
        roles.add(roleUpdate);
        roles.add(roleDelete);
        roles.add(roleCanSearchMachine);
        roles.add(roleCanCreateMachine);
        roles.add(roleCanDestroyMachine);
        roles.add(roleCanStartMachine);
        roles.add(roleCanStopMachine);
        roles.add(roleCanRestartMachine);


        //MACHINES
        Machine machine1 = new Machine();
        machine1.setName("machine1");
        machine1.setCreatedBy(userAdmin);
        machine1.setCreatedDate(Date.from(Instant.now()));

        Machine machine2 = new Machine();
        machine2.setName("machine2");
        machine2.setCreatedBy(userAdmin);
        machine2.setCreatedDate(Date.from(Instant.now()));

        java.util.List<Machine> machines = new ArrayList<>();
        machines.add(machine1);
        machines.add(machine2);


        //SETTING TO USER
        userAdmin.setFirstName("Admin");
        userAdmin.setLastName("Admin");
        userAdmin.setEmail("admin@admin.com");
        userAdmin.setPassword(passwordEncoder.encode("admin"));
        userAdmin.setRoles(roles);
        userAdmin.setMachines(machines);

        MachineSchedule schedule = new MachineSchedule();
        schedule.setAction(MachineAction.START);
        schedule.setMachine(machine1);
        schedule.setScheduleDate(Date.from(Instant.ofEpochMilli(System.currentTimeMillis() + 2 * 60000)));  //after 2 min

        //posto je u useru @Cascade(CascadeType.PERSIST) -> ne sme da se radi!
//        roleRepository.saveAll(List.of(roleCreate, roleRead, roleUpdate, roleDelete));
        userRepository.save(userAdmin);
        machineScheduleRepository.save(schedule);

        System.out.println("DATA LOADED!");
    }

}
