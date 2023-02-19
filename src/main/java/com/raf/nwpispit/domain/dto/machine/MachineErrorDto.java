package com.raf.nwpispit.domain.dto.machine;

import com.raf.nwpispit.domain.entities.machine.MachineAction;
import lombok.Data;

@Data
public class MachineErrorDto {

    private Long id;
    private String message;
    private MachineAction action;
    private MachineDto machine;
    private Long dateError;
}
