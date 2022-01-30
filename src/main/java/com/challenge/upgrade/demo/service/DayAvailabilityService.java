package com.challenge.upgrade.demo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.challenge.upgrade.demo.exception.ReservationException;
import com.challenge.upgrade.demo.model.DayAvailability;
import com.challenge.upgrade.demo.repository.DayAvailabilityRepository;

@Component
public class DayAvailabilityService {

    @Value("${island.max_occupancy}")
    private int max_occupancy;

    @Autowired
    private DayAvailabilityRepository dayAvailabilityRepository;

    public List<DayAvailability> get(LocalDate startDate, LocalDate endDate) {
        return this.dayAvailabilityRepository.findByDayBetween(startDate.toString(), endDate.toString());
    }


    public void updateAvailability(List<DayAvailability> newAvailabilities) {
        List<DayAvailability> savedAvailabilities = this.dayAvailabilityRepository.findByDayBetween(newAvailabilities.get(0).getDay(), newAvailabilities.get(newAvailabilities.size()-1).getDay());
        this.dayAvailabilityRepository.saveAll(this.mergeAvailabilities(savedAvailabilities, newAvailabilities));
    }

    public List<DayAvailability> mergeAvailabilities(List<DayAvailability> oldAvailabilities,
                                                     List<DayAvailability> newAvailabilities) {
        List<DayAvailability> mergedAvailabilities = new ArrayList<DayAvailability>();
        int newIndex = 0;
        int oldIndex = 0;

        while (newIndex < newAvailabilities.size() && oldIndex < oldAvailabilities.size()) {
            if (newAvailabilities.get(newIndex).getDay().compareTo(oldAvailabilities.get(oldIndex).getDay()) == 0) {
                DayAvailability merged = oldAvailabilities.get(oldIndex);
                merged.setOccupancy(newAvailabilities.get(newIndex).getOccupancy()
                        + oldAvailabilities.get(oldIndex).getOccupancy());
                if (merged.getOccupancy()>this.max_occupancy)
                    throw new ReservationException("Max occupancy exceeded for day: "+merged.getDay());
                mergedAvailabilities.add(merged);
                newIndex++;
                oldIndex++;
            } else {
                if (newAvailabilities.get(0).getDay().compareTo(oldAvailabilities.get(0).getDay()) < 0) {
                    mergedAvailabilities.add((newAvailabilities.get(newIndex)));
                    newIndex++;
                } else {
                    mergedAvailabilities.add(oldAvailabilities.get(oldIndex));
                    oldIndex++;
                }
            }
        }
        while (newIndex < newAvailabilities.size()) {
            mergedAvailabilities.add(newAvailabilities.get(newIndex));
            newIndex++;
        }
        while (oldIndex < oldAvailabilities.size()) {
            mergedAvailabilities.add(oldAvailabilities.get(oldIndex));
            oldIndex++;
        }
        return mergedAvailabilities;
    }
}