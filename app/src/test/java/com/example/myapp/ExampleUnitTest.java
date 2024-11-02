package com.example.myapp;

import org.junit.Test;

import static org.junit.Assert.*;

import com.jack.myapp.entities.Vacation;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testVacationEndDateValidation() {
        // Ensure that all 10 parameters are passed, matching the constructor's requirements
        Vacation vacation = new Vacation("Beach Trip", "Luxury Resort", 1, "Flight123", "Flight456",
                "2024-10-01", "2024-10-10", 2, 2, "Excursion Name");

        // This should pass because the return date is after the start date
        assertTrue(vacation.isEndDateAfterStartDate());
    }

    @Test
    public void testVacationInvalidEndDate() {
        // Ensure that all 10 parameters are passed, matching the constructor's requirements
        Vacation vacation = new Vacation("Beach Trip", "Luxury Resort", 1, "Flight123", "Flight456",
                "2024-10-10", "2024-10-01", 2, 2, "Excursion Name");

        // This should fail because the return date is before the start date
        assertFalse(vacation.isEndDateAfterStartDate());
    }


}