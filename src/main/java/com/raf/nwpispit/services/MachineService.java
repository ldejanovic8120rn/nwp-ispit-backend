package com.raf.nwpispit.services;

import com.raf.nwpispit.domain.dto.machine.MachineDto;
import com.raf.nwpispit.domain.dto.machine.MachineErrorDto;
import com.raf.nwpispit.domain.dto.machine.MachineQueueDto;
import com.raf.nwpispit.domain.dto.machine.MachineScheduleDto;
import com.raf.nwpispit.domain.entities.machine.*;
import com.raf.nwpispit.domain.entities.user.RoleType;
import com.raf.nwpispit.domain.entities.user.User;
import com.raf.nwpispit.domain.exceptions.MachineException;
import com.raf.nwpispit.domain.exceptions.NotFoundException;
import com.raf.nwpispit.domain.exceptions.UserException;
import com.raf.nwpispit.domain.mapper.MachineMapper;
import com.raf.nwpispit.repository.MachineErrorRepository;
import com.raf.nwpispit.repository.MachineRepository;
import com.raf.nwpispit.repository.MachineScheduleRepository;
import com.raf.nwpispit.repository.UserRepository;
import com.raf.nwpispit.utils.PermissionUtils;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MachineService {

    private final MachineRepository machineRepository;
    private final MachineScheduleRepository machineScheduleRepository;
    private final MachineErrorRepository machineErrorRepository;
    private final UserRepository userRepository;
    private final AmqpTemplate rabbitTemplate;

    public MachineService(MachineRepository machineRepository, MachineScheduleRepository machineScheduleRepository, MachineErrorRepository machineErrorRepository, UserRepository userRepository, AmqpTemplate rabbitTemplate) {
        this.machineRepository = machineRepository;
        this.machineScheduleRepository = machineScheduleRepository;
        this.machineErrorRepository = machineErrorRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public List<MachineDto> searchMachines(String name, List<MachineStatus> statusList, Long dateFrom, Long dateTo) {
        PermissionUtils.checkRole(RoleType.CAN_SEARCH_MACHINE);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Date dateFromParam = dateFrom == null ? null : Date.from(Instant.ofEpochMilli(dateFrom));
        Date dateToParam = dateTo == null ? null : Date.from(Instant.ofEpochMilli(dateTo));

        List<Machine> machines = machineRepository.findAllMachinesByParams(email, name, statusList, dateFromParam, dateToParam);
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

    public List<MachineErrorDto> getErrors() {
        List<MachineError> errors = machineErrorRepository.findAll();
        return errors.stream().map(MachineMapper.INSTANCE::machineErrorToMachineErrorDto).collect(Collectors.toList());
    }

    public void startMachine(Long id) {
        PermissionUtils.checkRole(RoleType.CAN_START_MACHINE);
        performAnActionForMachine(id, MachineStatus.STOPPED, MachineAction.START, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public void stopMachine(Long id) {
        PermissionUtils.checkRole(RoleType.CAN_STOP_MACHINE);
        performAnActionForMachine(id, MachineStatus.RUNNING, MachineAction.STOP, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public void restartMachine(Long id) {
        PermissionUtils.checkRole(RoleType.CAN_RESTART_MACHINE);
        performAnActionForMachine(id, MachineStatus.RUNNING, MachineAction.RESTART, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional(dontRollbackOn = MachineException.class)
    @Scheduled(cron = "0 * * * * *")  //every minute
    @SchedulerLock(name = "machineTasksScheduler")
    public void performScheduledTasks() {
        System.out.println("Scheduling starts");

        Date date = Date.from(Instant.now());
        List<MachineSchedule> machineSchedules = machineScheduleRepository.findAllByScheduleDateBefore(date);
        for(MachineSchedule machineSchedule: machineSchedules) {
            String email = machineSchedule.getMachine().getCreatedBy().getEmail();
            MachineAction action = machineSchedule.getAction();
            MachineStatus requiredStatus = action == MachineAction.START ? MachineStatus.STOPPED : MachineStatus.RUNNING;

            try {
                System.out.println("performing action");
                performAnActionForMachine(machineSchedule.getMachine().getId(), requiredStatus, action, email);
            }
            catch (MachineException e) {
                MachineError error = new MachineError();
                error.setMessage(e.getMessage());
                error.setAction(action);
                error.setMachine(machineSchedule.getMachine());
                error.setDateError(date);

                machineErrorRepository.save(error);
            }
        }

        machineScheduleRepository.deleteAll(machineSchedules);
    }

    @Transactional
    public void performAnActionForMachine(Long id, MachineStatus requiredStatus, MachineAction action, String email) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new NotFoundException("invalid machine id"));

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

        sendToQueue(machine.getId(), email, action);
    }

    @Transactional
    public void addScheduleTaskForMachine(MachineScheduleDto machineScheduleDto) {
        if(machineScheduleDto.getAction() == MachineAction.START)
            PermissionUtils.checkRole(RoleType.CAN_START_MACHINE);
        if(machineScheduleDto.getAction() == MachineAction.STOP)
            PermissionUtils.checkRole(RoleType.CAN_STOP_MACHINE);
        if(machineScheduleDto.getAction() == MachineAction.RESTART)
            PermissionUtils.checkRole(RoleType.CAN_RESTART_MACHINE);

        Machine machine = machineRepository.findById(machineScheduleDto.getId())
                .orElseThrow(() -> new NotFoundException("invalid machine id"));

        MachineSchedule machineSchedule = new MachineSchedule();
        machineSchedule.setScheduleDate(Date.from(Instant.ofEpochMilli(machineScheduleDto.getScheduleDate())));
        machineSchedule.setMachine(machine);
        machineSchedule.setAction(machineScheduleDto.getAction());

        machineScheduleRepository.save(machineSchedule);
    }

    public void sendToQueue(Long id, String email, MachineAction machineAction) {
        MachineQueueDto machineQueueDto = new MachineQueueDto();
        machineQueueDto.setId(id);
        machineQueueDto.setEmail(email);
        machineQueueDto.setMachineAction(machineAction);

        rabbitTemplate.convertAndSend("machineQueue", machineQueueDto);
    }

}
