package ltu;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;

public class PaymentTest {
    private PaymentImpl getPaymentImplInstanceCurrentDate() throws IOException
    {
        //TODO the provided CalendarImpl is always the current date
        return new PaymentImpl(new CalendarImpl());
    }

    @Test
    public void testSilly()
    {
        assertEquals(1, 1);
    }


    // ---------------------------------------------------------------
    // 100-series requirements
    // ---------------------------------------------------------------

    @Test
    public void validSubsidy_justTurned20Boundary() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate();
        int grantedSubsidy =
                TestNumConstants.StudentSupportType.FULL_TIME_LOAN.getAmountPerMonth() +
                TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();

        Student bornTomorrow = new StudentBuilder().birthDate(2006, 9, 5).build();
        Student birthdayStudent = new StudentBuilder().birthDate(2006, 9, 4).build();
        Student overTheAge = new StudentBuilder().birthDate(2006, 9, 3).build();

        //incredibly enough, it seems the "correct" implementation we got does not consider month and day
        //see getAge in PaymentImpl.java
        assertEquals(0, payimpl.getMonthlyAmount(bornTomorrow.ssn, bornTomorrow.income, bornTomorrow.studyRate, bornTomorrow.completionRatio));
        assertEquals(grantedSubsidy, payimpl.getMonthlyAmount(birthdayStudent.ssn, birthdayStudent.income, birthdayStudent.studyRate, birthdayStudent.completionRatio));
        assertEquals(grantedSubsidy, payimpl.getMonthlyAmount(overTheAge.ssn, overTheAge.income, overTheAge.studyRate, overTheAge.completionRatio));
    }

    // ---------------------------------------------------------------
    // 200-series requirements
    // ---------------------------------------------------------------

    // ---------------------------------------------------------------
    // 300-series requirements
    // ---------------------------------------------------------------

    // ---------------------------------------------------------------
    // 400-series requirements
    // ---------------------------------------------------------------

    // ---------------------------------------------------------------
    // 500-series requirements
    // ---------------------------------------------------------------

}
