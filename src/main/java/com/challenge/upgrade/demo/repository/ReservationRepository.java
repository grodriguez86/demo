package com.challenge.upgrade.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.challenge.upgrade.demo.model.Reservation;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long>{


}