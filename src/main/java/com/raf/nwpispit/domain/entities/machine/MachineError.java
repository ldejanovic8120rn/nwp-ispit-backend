package com.raf.nwpispit.domain.entities.machine;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class MachineError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Enumerated(EnumType.STRING)
    private MachineAction action;

    @ManyToOne
    private Machine machine;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateError;

}
