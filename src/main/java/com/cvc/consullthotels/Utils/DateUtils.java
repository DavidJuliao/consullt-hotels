package com.cvc.consullthotels.Utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;

public class DateUtils {

    private DateUtils() {
    }

    public static int getDaysByDates(LocalDate initialDate, LocalDate finalDate){
        return Period.between(initialDate,finalDate).getDays();
    }

    public static boolean isTodayOrMoreThanCurrent(LocalDate date){
        return date.isEqual(LocalDate.now()) || date.isAfter(LocalDate.now());
    }

    public static boolean checkInDateIsMoreThanOrEqualCheckOut(LocalDate checkIn, LocalDate checkOut){
        return checkIn.isEqual(checkOut) || checkIn.isAfter(checkOut);
    }

    public static String dateToString(LocalDate localDate){
        Date date = Date.valueOf(localDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        return format.format(date);
    }
}
