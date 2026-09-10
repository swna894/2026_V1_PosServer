package com.swna.server.sales_trasaction;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;

public class YearWeekToLocalDate {
    private final int year;
    private final int week;

    public YearWeekToLocalDate(int year, int week) {
        this.year = year;
        this.week = week;
    }

    public static YearWeekToLocalDate parse(String str) {
        String[] parts = str.split("-");
        return new YearWeekToLocalDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    public LocalDate toLocalDate() {
        return LocalDate.of(year, 1, 1)
                         .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                         .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
}
