package com.raf.nwpispit.domain.entities.machine;

import com.raf.nwpispit.domain.entities.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MachineStatus status = MachineStatus.STOPPED;

    private String name;
    private boolean active = true;
    private boolean busy = false;

    @ManyToOne
    private User createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column
    @Version
    private Integer version = 0;

}
