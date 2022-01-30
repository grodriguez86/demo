package com.challenge.upgrade.demo.rest;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.upgrade.demo.model.DayAvailability;
import com.challenge.upgrade.demo.service.DayAvailabilityService;

@RestController
@RequestMapping("/availability/")
public class AvailabilityController {

    @Autowired
    private DayAvailabilityService dayAvailabilityService;

    @GetMapping("/")
    public ResponseEntity<List<DayAvailability>> get(
            @RequestParam("startDate") @DateTimeFormat(pattern = DayAvailability.ID_PATTERN) Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = DayAvailability.ID_PATTERN) Date endDate) {

        List<DayAvailability> availabilities = new ArrayList<DayAvailability>();

        this.dayAvailabilityService
                .get(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .stream().forEach(da -> availabilities.add(new DayAvailability(da.toString())));
        return new ResponseEntity<List<DayAvailability>>(availabilities, HttpStatus.OK);
    }

}