package com.raf.nwpispit.repository;

import com.raf.nwpispit.domain.entities.machine.MachineError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineErrorRepository extends JpaRepository<MachineError, Long> {

}
