package com.raf.nwpispit.repository;

import com.raf.nwpispit.domain.entities.machine.MachineSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface MachineScheduleRepository extends JpaRepository<MachineSchedule, Long> {
    List<MachineSchedule> findAllByScheduleDateBefore(Date date);

}
