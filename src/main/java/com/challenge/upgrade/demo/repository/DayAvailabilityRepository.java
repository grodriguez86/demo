package com.challenge.upgrade.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.challenge.upgrade.demo.model.DayAvailability;



@Repository
public interface DayAvailabilityRepository extends CrudRepository<DayAvailability, Long> {

    List<DayAvailability> findByDayBetween(String startDate, String endDate);
}