package com.challenge.upgrade.demo.rest;

import java.sql.BatchUpdateException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.upgrade.demo.exception.ReservationException;
import com.challenge.upgrade.demo.model.Reservation;
import com.challenge.upgrade.demo.service.ReservationService;

@RestController
@RequestMapping("/reservation/")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/{reservationId}")
    public ResponseEntity<Reservation> get(@PathVariable("reservationId") long reservationId) {

        Reservation reservation = this.reservationService.get(reservationId);
        return new ResponseEntity<Reservation>(reservation, HttpStatus.OK);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<Reservation> editReservation(@PathVariable("reservationId") long reservationId,
                                                       @RequestBody Reservation reservation) {

        this.reservationService.edit(reservationId, reservation);
        return new ResponseEntity<Reservation>(HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Long> createReservation(@RequestBody Reservation reservation) {

        return new ResponseEntity<Long>(this.reservationService.create(reservation), HttpStatus.OK);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Reservation> delete(@PathVariable("reservationId") long reservationId) {

        this.reservationService.delete(reservationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<?> handleReservationException(ReservationException exc) {
        return ResponseEntity.badRequest().body(exc.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<?> handleObjectOptimisticLockingFailureException(
            ObjectOptimisticLockingFailureException exc) {
        return ResponseEntity.badRequest()
                .body("Your reservation could not be processed. Please retry. " + exc.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException exc) {
        return ResponseEntity.badRequest()
                .body("Your reservation could not be processed. Please retry. " + exc.getMessage());
    }

    @ExceptionHandler(BatchUpdateException.class)
    public ResponseEntity<?> handleBatchUpdateException(BatchUpdateException exc) {
        return ResponseEntity.badRequest()
                .body("Your reservation could not be processed. Please retry. " + exc.getMessage());
    }
}