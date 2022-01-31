package com.challenge.upgrade.demo.service;

import java.sql.BatchUpdateException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.challenge.upgrade.demo.exception.ReservationException;
import com.challenge.upgrade.demo.model.DayAvailability;
import com.challenge.upgrade.demo.model.Reservation;
import com.challenge.upgrade.demo.repository.ReservationRepository;
import com.challenge.upgrade.demo.util.ReservationValidator;

@Component
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DayAvailabilityService dayAvailabilityService;

    @Transactional
    @Retryable(value = {BatchUpdateException.class, DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class}, maxAttempts = 5)
    public long create(Reservation reservation) {
        ReservationValidator.validateReservation(reservation);

        List<DayAvailability> availabilities = createAvailabilities(reservation);

        this.dayAvailabilityService.updateAvailability(availabilities);

        return this.reservationRepository.save(reservation).getId();
    }

    private List<DayAvailability> createAvailabilities(Reservation reservation) {
        List<DayAvailability> availabilities = new ArrayList<DayAvailability>();
        DayAvailability availability;
        LocalDate availabilityDate = LocalDate.parse(reservation.getStartDate());
        while (availabilityDate.compareTo(LocalDate.parse(reservation.getEndDate())) != 0) {
            availability = new DayAvailability(availabilityDate.toString());
            availability.setOccupancy(reservation.getNumberOfPeople());
            availabilities.add(availability);
            availabilityDate = availabilityDate.plusDays(1);
        }
        return availabilities;
    }

    @Transactional
    public void delete(long reservationId) {
        Reservation reservation = this.get(reservationId);
        reservation.setNumberOfPeople(-reservation.getNumberOfPeople());

        List<DayAvailability> availabilities = createAvailabilities(reservation);

        this.dayAvailabilityService.updateAvailability(availabilities);

        this.reservationRepository.delete(reservation);
    }

    public Reservation get(long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException("Reservation not found. id:" + reservationId));
    }

    @Transactional
    @Retryable(value = {BatchUpdateException.class, DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class}, maxAttempts = 5)
    public void edit(long reservationId, Reservation reservation) {
        ReservationValidator.validateReservation(reservation);

        Reservation oldReservation = this.get(reservationId);
        oldReservation.setNumberOfPeople(-oldReservation.getNumberOfPeople());

        List<DayAvailability> newAvailabilities = createAvailabilities(reservation);
        List<DayAvailability> oldAvailabilities = createAvailabilities(oldReservation);

        this.dayAvailabilityService.updateAvailability(
                this.dayAvailabilityService.mergeAvailabilities(oldAvailabilities, newAvailabilities));

        oldReservation.setEmail(reservation.getEmail());
        oldReservation.setFullName(reservation.getFullName());
        oldReservation.setNumberOfPeople(reservation.getNumberOfPeople());
        oldReservation.setStartDate(reservation.getStartDate());
        oldReservation.setEndDate(reservation.getEndDate());

        this.reservationRepository.save(oldReservation);
    }

}
