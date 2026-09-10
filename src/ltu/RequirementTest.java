package ltu;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RequirementTest {

    private final PaymentImpl default_payimpl;

    public RequirementTest() throws IOException
    {
        default_payimpl = new PaymentImpl(new CalendarImpl(2016, 1, 1));
    }

    private PaymentImpl getPaymentImplCustomDate(int year, int month, int day) throws IOException
    {
        return new PaymentImpl(new CalendarImpl(year, month, day));
    }

    // ---------------------------------------------------------------
    // Confirmed requirement failures
    // ---------------------------------------------------------------

    /**
     * Requirement 102 fails because the student still receives subsidy despite being over the age.
     */
    @Test
    public void requirement_102_fails() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016,1,1);
        Student student = new StudentBuilder().birthDate(1959, 1, 1).build();

        //57 yr. old should not receive anything
        int expected = 0;
        int actual = payimpl.getMonthlyAmount(student.ssn, student.income, student.studyRate, student.completionRatio);

        assertEquals(expected, actual);
    }

    /**
     * Requirement 103 fails because the student is also awarded a loan.
     */
    @Test
    public void requirement_103_fails() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016,1,1);
        Student student = new StudentBuilder().birthDate(1969, 1, 1).build();

        //should not receive a loan after 47, only subsidy
        int expected = TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();
        int actual = payimpl.getMonthlyAmount(student.ssn, student.income, student.studyRate, student.completionRatio);

        assertEquals(expected, actual);
    }

    /**
     * Requirement 302 fails because a part time student over the income cap is awarded a part-time subsidy.
     */
    @Test
    public void requirement_302_fails() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016,1,1);
        Student student = new StudentBuilder()
                .birthDate(1960, 1, 1) //56
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.HALF_TIME_OVER_MAXIMUM)
                .build();

        //should receive nothing due to being above half-time income cap: 128723
        int expected = 0;
        int actual = payimpl.getMonthlyAmount(student.ssn, student.income, student.studyRate, student.completionRatio);

        assertEquals(expected, actual);

    }

    /**
     * Requirement 503 fails because the awarded loan is the incorrect amount (1000 more).
     */
    @Test
    public void requirement_503_fails() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016,1,1);
        Student student = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .birthDate(1986, 1, 1) //30
                .build();

        Student subsidy = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .birthDate(1966, 1, 1) //50
                .build();

        //a part time student loan should be exactly 3564 kronor
        int expectedLoan = TestNumConstants.StudentSupportType.PART_TIME_LOAN.getAmountPerMonth();
        int expectedSubsidy = default_payimpl.getMonthlyAmount(subsidy.ssn, subsidy.income, subsidy.studyRate, subsidy.completionRatio);

        int actualGrant = payimpl.getMonthlyAmount(student.ssn, student.income, student.studyRate, student.completionRatio);

        assertEquals(expectedLoan, actualGrant - expectedSubsidy);
    }

    /**
     * Requirement fails because the payment date is on the wrong weekday.
     */
    @Test
    public void requirement506_fails() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 2, 10);
        assertEquals("20160229", payimpl.getNextPaymentDay());
    }
    @Test
public void requirement_101_fails() throws IOException {
    PaymentImpl payimpl = new PaymentImpl(new CalendarImpl(2016, 1, 15));

    Student age19 = StudentBuilder.fullTimeStudentNoIncome()
            .birthDate(1997, 1, 1)
            .build();

    Student age20 = StudentBuilder.fullTimeStudentNoIncome()
            .birthDate(1996, 1, 1)
            .build();

    Student age21 = StudentBuilder.fullTimeStudentNoIncome()
            .birthDate(1995, 1, 1)
            .build();

    int amount19 = payimpl.getMonthlyAmount(
            age19.ssn, age19.income, age19.studyRate, age19.completionRatio);

    int amount20 = payimpl.getMonthlyAmount(
            age20.ssn, age20.income, age20.studyRate, age20.completionRatio);

    int amount21 = payimpl.getMonthlyAmount(
            age21.ssn, age21.income, age21.studyRate, age21.completionRatio);

    // Under 20 should receive nothing
    assertEquals(0, amount19);

    // A 20-year-old should receive the same entitlement as a 21-year-old
    assertEquals(amount21, amount20);
    }
}
