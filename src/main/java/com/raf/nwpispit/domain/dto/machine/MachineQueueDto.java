package com.raf.nwpispit.domain.dto.machine;

import com.raf.nwpispit.domain.entities.machine.MachineAction;
import lombok.Data;

import java.io.Serializable;

@Data
public class MachineQueueDto implements Serializable {

    private Long id;
    private String email;
    private MachineAction machineAction;

}
