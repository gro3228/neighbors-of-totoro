package com.example.backend.Controller;

import com.example.backend.Model.Day;
import com.example.backend.Service.DayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DayController {

    @Autowired
    private DayService dayService;

    //CREATE
    @CrossOrigin
    @PostMapping(value = "/days", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createDay(@RequestBody Day newDay) {
        Object[] results = dayService.createDay(newDay);
        if (results[0] == (Integer) 1 && results[1] != null) {
            return new ResponseEntity<>(results[1], HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(results[1], HttpStatus.BAD_REQUEST);
        }
    }

    //READ
    @CrossOrigin
    @GetMapping("/days/{dayID}")
    public ResponseEntity<Day> getDay(@PathVariable UUID dayID) {
        Day day = dayService.getDay(dayID);
        return new ResponseEntity<>(day, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/days")
    public ResponseEntity<List<Day>> getDays() {
        List<Day> days = dayService.getDays();
        return new ResponseEntity<>(days, HttpStatus.OK);
    }

    //UPDATE
    @CrossOrigin
    @PutMapping(value = "/days/{dayID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> updateDay(@PathVariable UUID dayID, @RequestBody Day dayDetails) {
        int rowsAffected = dayService.updateDay(dayID, dayDetails);
        if (rowsAffected == 1) {
            return new ResponseEntity<>(rowsAffected, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(rowsAffected, HttpStatus.BAD_REQUEST);
        }
    }

    //DELETE
    @CrossOrigin
    @DeleteMapping("/days/{dayID}")
    public ResponseEntity<Integer> deleteDay(@PathVariable UUID dayID) {
        int rowsAffected = dayService.deleteDay(dayID);
        if (rowsAffected == 1) {
            return new ResponseEntity<>(rowsAffected, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(rowsAffected, HttpStatus.BAD_REQUEST);
        }
    }
}
