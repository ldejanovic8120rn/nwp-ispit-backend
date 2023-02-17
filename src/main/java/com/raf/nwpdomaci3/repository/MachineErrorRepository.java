package com.raf.nwpdomaci3.repository;

import com.raf.nwpdomaci3.domain.entities.machine.MachineError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineErrorRepository extends JpaRepository<MachineError, Long> {

}
