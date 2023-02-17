package com.raf.nwpispit.controllers;

import com.raf.nwpispit.domain.entities.machine.MachineStatus;
import com.raf.nwpispit.services.MachineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/machines")
public class MachineController {

    private final MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    @GetMapping
    public ResponseEntity<?> searchMachines(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) List<MachineStatus> statusList,
                                         @RequestParam(required = false) Long dateFrom,
                                         @RequestParam(required = false) Long dateTo
    ) {

        return ResponseEntity.ok(machineService.searchMachines(name, statusList, dateFrom, dateTo));
    }

    @PostMapping("/create/{name}")
    public ResponseEntity<?> createMachine(@PathVariable("name") String name) {
        return ResponseEntity.ok(machineService.createMachine(name));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMachine(@PathVariable("id") Long id) {
        return ResponseEntity.ok(machineService.deleteMachine(id));
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<?> startMachine(@PathVariable("id") Long id) {
        machineService.startMachine(id);

        Map<String, String> body = new HashMap<>();
        body.put("message", "action accepted");

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping("/stop/{id}")
    public ResponseEntity<?> stopMachine(@PathVariable("id") Long id) {
        machineService.stopMachine(id);

        Map<String, String> body = new HashMap<>();
        body.put("message", "action accepted");

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping("/restart/{id}")
    public ResponseEntity<?> restartMachine(@PathVariable("id") Long id) {
        machineService.restartMachine(id);

        Map<String, String> body = new HashMap<>();
        body.put("message", "action accepted");

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
