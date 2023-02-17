package com.raf.nwpispit.domain.dto.machine;

import com.raf.nwpispit.domain.entities.machine.MachineAction;
import lombok.Data;

import java.io.Serializable;

@Data
public class MachineScheduleDto implements Serializable {

    private Long id;
    private Long scheduleDate;
    private MachineAction action;

}
