/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.ExtendedMetadataProcessWorker.dates;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.junit.Test;
import uk.ac.core.common.util.TextToDateTime;

import static org.junit.Assert.*;

/**
 * 
 * @author samuel
 */
public class TextToDateTest {

    public TextToDateTest() {
    }

    /**
     * Test of asLocalDateTime method, of class TextToDateTime.
     * @param stringValues
     * @param expectedResult
     */
    public void testAsLocalDateTime(String stringValues, LocalDateTime expectedResult) {
        TextToDateTime instance = new TextToDateTime(stringValues);
        LocalDateTime expResult = instance.asLocalDateTime();
        LocalDateTime result = expectedResult;
        assertEquals(expResult, result);
    }

    @Test
    public void testAsLocalDateTimeAll() {
        // todo: how to use junit parameterised testing?
        this.testAsLocalDateTime("23 Mar 2016 12:43:46", LocalDateTime.of(2016, Month.MARCH, 23, 12, 43, 46));
        this.testAsLocalDateTime("31 January 2004 12:43:46", LocalDateTime.of(2004, Month.JANUARY, 31, 12, 43, 46));
        this.testAsLocalDateTime("1 Jan 2026 12:43:46", LocalDateTime.of(2026, Month.JANUARY, 1, 12, 43, 46));
        this.testAsLocalDateTime("25 Mar 2010 10:05", LocalDateTime.of(2010, Month.MARCH, 25, 10, 05));
        this.testAsLocalDateTime("29 Aug 2009", LocalDateTime.of(2009, Month.AUGUST, 29, 00, 00));
        this.testAsLocalDateTime("12 May 2011 17:05 UTC", LocalDateTime.of(2011, Month.MAY, 12, 17, 05));
        this.testAsLocalDateTime("03 August 2019 16:20:17+11:00", ZonedDateTime.of(LocalDateTime.of(2019, Month.AUGUST, 3, 16, 20, 17),ZoneId.of("Asia/Dubai")).toLocalDateTime());
        this.testAsLocalDateTime("2019-01-01", LocalDateTime.of(2019, Month.JANUARY, 1, 0, 0));
        this.testAsLocalDateTime("01-07-2001", LocalDateTime.of(2001, Month.JULY, 1, 0, 0));
        this.testAsLocalDateTime("info:eu-repo/date/embargoEnd/2021-05-22", LocalDateTime.of(2021, Month.MAY, 22, 0, 0));

        this.testAsLocalDateTime("Oct 31, 2016", LocalDateTime.of(2016, Month.OCTOBER, 31, 0, 0));
        this.testAsLocalDateTime("Mar 20, 2008", LocalDateTime.of(2008, Month.MARCH, 20, 0, 0));
        this.testAsLocalDateTime("Dec 02, 2016", LocalDateTime.of(2016, Month.DECEMBER, 2, 0, 0));
        this.testAsLocalDateTime("8 June 2021", LocalDateTime.of(2021, Month.JUNE, 8, 0, 0));

    }

}
