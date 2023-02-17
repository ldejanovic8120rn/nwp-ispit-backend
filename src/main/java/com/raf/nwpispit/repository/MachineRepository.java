package com.raf.nwpispit.repository;

import com.raf.nwpispit.domain.entities.machine.Machine;
import com.raf.nwpispit.domain.entities.machine.MachineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {

    @Query("select m from Machine m where m.active = true " +
            "and (m.createdBy.email = :email) " +
            "and (:machineName is null or m.name like %:machineName%) " +
            "and ((:statuses is null) or m.status in (:statuses)) " +
            "and (cast(:dateFrom as date) is null or cast(:dateTo as date) is null or (m.createdDate between :dateFrom and :dateTo))"
    )
    List<Machine> findAllMachinesByParams(String email, String machineName, List<MachineStatus> statuses, Date dateFrom, Date dateTo);

}
