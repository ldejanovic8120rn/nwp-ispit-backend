package com.raf.nwpispit.listeners;

import com.raf.nwpispit.domain.dto.machine.MachineQueueDto;
import com.raf.nwpispit.domain.entities.machine.Machine;
import com.raf.nwpispit.domain.entities.machine.MachineAction;
import com.raf.nwpispit.domain.entities.machine.MachineStatus;
import com.raf.nwpispit.domain.exceptions.MachineException;
import com.raf.nwpispit.domain.exceptions.NotFoundException;
import com.raf.nwpispit.domain.mapper.MachineMapper;
import com.raf.nwpispit.repository.MachineRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Component
public class MachineQueueListener {

    private final MachineRepository machineRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public MachineQueueListener(MachineRepository machineRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.machineRepository = machineRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @RabbitListener(queues = "machineQueue")
    public void machineQueueHandler(Message message) throws InterruptedException, IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(message.getBody());
        ObjectInputStream is = new ObjectInputStream(in);

        MachineQueueDto machineQueueDto = (MachineQueueDto) is.readObject();
        if(machineQueueDto.getMachineAction() == MachineAction.START) {
            startStopMachine(machineQueueDto.getId(), machineQueueDto.getEmail(), MachineStatus.RUNNING);
        }
        else if (machineQueueDto.getMachineAction() == MachineAction.STOP) {
            startStopMachine(machineQueueDto.getId(), machineQueueDto.getEmail(), MachineStatus.STOPPED);
        }
        else if (machineQueueDto.getMachineAction() == MachineAction.RESTART) {
            restartMachine(machineQueueDto.getId(), machineQueueDto.getEmail());
        }

    }

    @Transactional
    public void startStopMachine(Long id, String email, MachineStatus status) throws InterruptedException {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new NotFoundException("invalid machine id"));
        Thread.sleep(10000);

        try {
            machine.setStatus(status);
            machine.setBusy(false);
            machine = machineRepository.saveAndFlush(machine);
        }
        catch (ObjectOptimisticLockingFailureException e) {
            throw new MachineException("someone has overtaken you in action");
        }

        simpMessagingTemplate.convertAndSend("/machine-fe/" + email, MachineMapper.INSTANCE.machineToMachineDto(machine));
    }

    @Transactional
    public void restartMachine(Long id, String email) throws InterruptedException {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new NotFoundException("invalid machine id"));

        try {
            Thread.sleep(5000);
            machine.setStatus(MachineStatus.STOPPED);
            machine = machineRepository.saveAndFlush(machine);

            Thread.sleep(5000);
            machine.setStatus(MachineStatus.RUNNING);
            machine.setBusy(false);
            machine = machineRepository.saveAndFlush(machine);
        }
        catch (ObjectOptimisticLockingFailureException e) {
            throw new MachineException("someone has overtaken you in action");
        }

        simpMessagingTemplate.convertAndSend("/machine-fe/" + email, MachineMapper.INSTANCE.machineToMachineDto(machine));
    }

}
