package com.raf.nwpispit.domain.dto.machine;

import com.mysema.query.BooleanBuilder;
import com.raf.nwpispit.domain.entities.machine.MachineStatus;
import lombok.Data;

import java.util.List;

@Data
public class MachineRequestFilter {

    private String name;
    private List<MachineStatus> statusList;
    private Long dateFrom;
    private Long dateTo;

    public BooleanBuilder getPredicate() {

        BooleanBuilder predicate = new BooleanBuilder();

//        if(name != null)
//            predicate.and()

        return null;
    }

}
