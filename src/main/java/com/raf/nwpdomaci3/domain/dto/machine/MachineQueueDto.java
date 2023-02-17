package com.raf.nwpdomaci3.domain.dto.machine;

import com.raf.nwpdomaci3.domain.entities.machine.MachineAction;
import lombok.Data;

@Data
public class MachineQueueDto {

    private Long id;
    private String email;
    private MachineAction machineAction;

}
