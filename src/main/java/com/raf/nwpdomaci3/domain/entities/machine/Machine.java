package com.raf.nwpdomaci3.domain.entities.machine;

import com.raf.nwpdomaci3.domain.entities.user.User;
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
    private boolean active = false;
    private boolean busy = false;

    @ManyToOne
    private User createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column
    @Version
    private Integer version = 0;

}
