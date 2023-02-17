package com.raf.nwpispit.domain.dto.machine;

import com.raf.nwpispit.domain.entities.machine.MachineStatus;
import lombok.Data;

@Data
public class MachineDto {

    private Long id;
    private String name;
    private MachineStatus status;
    private Long createdDate;

}
