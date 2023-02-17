package com.raf.nwpispit.services;

import com.raf.nwpispit.domain.dto.machine.MachineDto;
import com.raf.nwpispit.domain.dto.machine.MachineQueueDto;
import com.raf.nwpispit.domain.entities.machine.Machine;
import com.raf.nwpispit.domain.entities.machine.MachineAction;
import com.raf.nwpispit.domain.entities.machine.MachineStatus;
import com.raf.nwpispit.domain.entities.user.RoleType;
import com.raf.nwpispit.domain.entities.user.User;
import com.raf.nwpispit.domain.exceptions.MachineException;
import com.raf.nwpispit.domain.exceptions.NotFoundException;
import com.raf.nwpispit.domain.exceptions.UserException;
import com.raf.nwpispit.domain.mapper.MachineMapper;
import com.raf.nwpispit.repository.MachineRepository;
import com.raf.nwpispit.repository.UserRepository;
import com.raf.nwpispit.utils.PermissionUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MachineService {

    private final MachineRepository machineRepository;
    private final UserRepository userRepository;
    private final AmqpTemplate rabbitTemplate;

    public MachineService(MachineRepository machineRepository, UserRepository userRepository, AmqpTemplate rabbitTemplate) {
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public List<MachineDto> searchMachines(String name, List<MachineStatus> statusList, Long dateFrom, Long dateTo) {
        PermissionUtils.checkRole(RoleType.CAN_SEARCH_MACHINE);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Machine> machines = machineRepository.findAllMachinesByParams(email, name, statusList,
                Date.from(Instant.ofEpochMilli(dateFrom)), Date.from(Instant.ofEpochMilli(dateTo)));
        return machines.stream().map(MachineMapper.INSTANCE::machineToMachineDto).collect(Collectors.toList());
    }

    @Transactional
    public MachineDto createMachine(String name) {
        PermissionUtils.checkRole(RoleType.CAN_CREATE_MACHINE);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("user not found"));

        Machine machine = new Machine();
        machine.setName(name);
        machine.setCreatedBy(user);
        machine.setCreatedDate(Date.from(Instant.now()));

        return MachineMapper.INSTANCE.machineToMachineDto(machineRepository.save(machine));
    }

    @Transactional
    public MachineDto deleteMachine(Long id) {
        PermissionUtils.checkRole(RoleType.CAN_DESTROY_MACHINE);

        Machine machine = machineRepository.findById(id).orElseThrow(() -> new NotFoundException("invalid id for machine"));
        checkMachineOwner(machine);

        if(machine.isBusy())
            throw new MachineException("machine is busy");

        if(machine.getStatus() != MachineStatus.STOPPED) {
            throw new MachineException("machine isn't stopped");
        }

        try {
            machine.setActive(false);
            return MachineMapper.INSTANCE.machineToMachineDto(machineRepository.save(machine));
        }
        catch (ObjectOptimisticLockingFailureException e) {
            throw new MachineException("someone has overtaken you in action");
        }
    }

    public void startMachine(Long id) {
        PermissionUtils.checkRole(RoleType.CAN_START_MACHINE);
        performAnActionForMachine(id, MachineStatus.STOPPED, MachineAction.START);
    }

    public void stopMachine(Long id) {
        PermissionUtils.checkRole(RoleType.CAN_STOP_MACHINE);
        performAnActionForMachine(id, MachineStatus.RUNNING, MachineAction.STOP);
    }

    public void restartMachine(Long id) {
        PermissionUtils.checkRole(RoleType.CAN_RESTART_MACHINE);
        performAnActionForMachine(id, MachineStatus.RUNNING, MachineAction.RESTART);
    }

    @Transactional
    public void performAnActionForMachine(Long id, MachineStatus requiredStatus, MachineAction action) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new NotFoundException("invalid machine id"));
        checkMachineOwner(machine);

        if(machine.isBusy())
            throw new MachineException("machine is busy");

        if(machine.getStatus() != requiredStatus)
            throw new MachineException("machine isn't " + requiredStatus.name().toLowerCase());

        try {
            machine.setBusy(true);
            machineRepository.saveAndFlush(machine);
        }
        catch (ObjectOptimisticLockingFailureException e) {
            throw new MachineException("someone has overtaken you in action");
        }

        sendToQueue(machine.getId(), SecurityContextHolder.getContext().getAuthentication().getName(), action);
    }

    private void checkMachineOwner(Machine machine) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!email.equals(machine.getCreatedBy().getEmail()))
            throw new UserException("you aren't owner of this machine");
    }

    public void sendToQueue(Long id, String email, MachineAction machineAction) {
        MachineQueueDto machineQueueDto = new MachineQueueDto();
        machineQueueDto.setId(id);
        machineQueueDto.setEmail(email);
        machineQueueDto.setMachineAction(machineAction);

        rabbitTemplate.convertAndSend("machineQueue", machineQueueDto);
    }

}
