package com.challenge.upgrade.demo.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.challenge.upgrade.demo.exception.ReservationException;
import com.challenge.upgrade.demo.model.Reservation;

public class ReservationValidator {

    public static void validateReservation(Reservation reservation) {

        if (ChronoUnit.DAYS.between(LocalDate.parse(reservation.getStartDate()),
                LocalDate.parse(reservation.getEndDate())) > 3)
            throw new ReservationException("Can't book reservation for more than three days.");

        LocalDate today = LocalDate.now();

        if (today.compareTo(LocalDate.parse(reservation.getStartDate())) >= 0)
            throw new ReservationException("It's too late to make this reservation.");

        LocalDate inOneMonth = today.plusMonths(1);

        if (inOneMonth.compareTo(LocalDate.parse(reservation.getEndDate())) < 0)
            throw new ReservationException("It's too early to make this reservation.");
    }

}