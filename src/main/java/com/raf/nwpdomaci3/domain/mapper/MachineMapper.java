package com.raf.nwpdomaci3.domain.mapper;

import com.raf.nwpdomaci3.domain.dto.machine.MachineDto;
import com.raf.nwpdomaci3.domain.entities.machine.Machine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MachineMapper {

    MachineMapper INSTANCE = Mappers.getMapper(MachineMapper.class);

    @Mapping(target="createdDate", source="machine", qualifiedByName = "getCreatedDate")
    MachineDto machineToMachineDto(Machine machine);

    @Named("getCreatedDate")
    default Long getCreatedDate(Machine machine) {
        return machine.getCreatedDate().getTime();
    }

}
