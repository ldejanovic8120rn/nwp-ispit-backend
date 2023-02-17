package com.raf.nwpdomaci3.repository;

import com.raf.nwpdomaci3.domain.entities.machine.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {

}
