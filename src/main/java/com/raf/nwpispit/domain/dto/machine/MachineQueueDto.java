package com.raf.nwpispit.domain.dto.machine;

import com.raf.nwpispit.domain.entities.machine.MachineAction;
import lombok.Data;

@Data
public class MachineQueueDto {

    private Long id;
    private String email;
    private MachineAction machineAction;

}
