package com.raf.nwpispit.domain.mapper;

import com.raf.nwpispit.domain.dto.machine.MachineDto;
import com.raf.nwpispit.domain.dto.machine.MachineErrorDto;
import com.raf.nwpispit.domain.entities.machine.Machine;
import com.raf.nwpispit.domain.entities.machine.MachineError;
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

    @Mapping(target="dateError", source="machineError", qualifiedByName = "getErrorDate")
    @Mapping(target="machine", source="machineError", qualifiedByName = "getMachineDto")
    MachineErrorDto machineErrorToMachineErrorDto(MachineError machineError);

    @Named("getCreatedDate")
    default Long getCreatedDate(Machine machine) {
        return machine.getCreatedDate().getTime();
    }

    @Named("getErrorDate")
    default Long getErrorDate(MachineError machineError) {
        return machineError.getDateError().getTime();
    }

    @Named("getMachineDto")
    default MachineDto getMachineDto(MachineError machineError) {
        return machineToMachineDto(machineError.getMachine());
    }

}
