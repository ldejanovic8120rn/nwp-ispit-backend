package com.raf.nwpispit.listeners;

import com.raf.nwpispit.domain.dto.machine.MachineQueueDto;
import com.raf.nwpispit.repository.MachineRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

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



    }

}
