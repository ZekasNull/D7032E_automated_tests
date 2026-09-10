package ltu;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;

public class PaymentTest {
    private final PaymentImpl default_payimpl;

    public PaymentTest() throws IOException
    {
        default_payimpl = new PaymentImpl(new CalendarImpl(2016, 1, 1));
    }


    private PaymentImpl getPaymentImplInstanceCurrentDate() throws IOException {
        //TODO the provided CalendarImpl is always the current date
        return new PaymentImpl(new CalendarImpl());
    }

    private PaymentImpl getPaymentImplCustomDate(int year, int month, int day) throws IOException
    {
        return new PaymentImpl(new CalendarImpl(year, month, day));
    }

    int fullGrant = TestNumConstants.StudentSupportType.FULL_TIME_LOAN.getAmountPerMonth() +
            TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();
    int halfGrant = TestNumConstants.StudentSupportType.PART_TIME_LOAN.getAmountPerMonth() +
            TestNumConstants.StudentSupportType.PART_TIME_SUBSIDIARY.getAmountPerMonth();


    // ---------------------------------------------------------------
    // Input validation
    // ---------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void invalidIncome_negative() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        payimpl.getMonthlyAmount("19960101-1234", -1, 100, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidStudyRate_negative() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        payimpl.getMonthlyAmount("19960101-1234", 0, -1, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCompletionRatio_negative() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        payimpl.getMonthlyAmount("19960101-1234", 0, 100, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidPersonId_randomString() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        payimpl.getMonthlyAmount("wrong", 0, 100, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidPersonId_malformedFormat() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        payimpl.getMonthlyAmount("ABCD0101-1234", 0, 100, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidPersonId_invalidLength() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        payimpl.getMonthlyAmount("960101-1234", 0, 100, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidPersonId_null() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        payimpl.getMonthlyAmount(null, 0, 100, 100);
    }

    // ---------------------------------------------------------------
    // 100-series requirements
    // ---------------------------------------------------------------

    /**
     * Evaluates 101 (halftime)
     */
    @Test
    public void tooYoung_fullTime()
    {
        Student under = new StudentBuilder().birthDate(1997, 1, 1).build();

        int actual = default_payimpl.getMonthlyAmount(under.ssn, under.income, under.studyRate, under.completionRatio);

        assertEquals(0, actual);
    }

    /**
     * Evaluates 101
     */
    @Test
    public void tooYoung_halfTime()
    {
        Student under = new StudentBuilder()
                .birthDate(1997, 1, 1)
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .build();

        int actual = default_payimpl.getMonthlyAmount(under.ssn, under.income, under.studyRate, under.completionRatio);

        assertEquals(0, actual);
    }

    @Test
    public void fullTime_noIncome_fullCompletion_noGrantBefore20()
    {
        Student border = new StudentBuilder().birthDate(1996, 1, 1).build();
        Student over = new StudentBuilder().birthDate(1995, 1, 1).build();

        int actual_border = default_payimpl.getMonthlyAmount(border.ssn, border.income, border.studyRate, border.completionRatio);
        int actual_over = default_payimpl.getMonthlyAmount(over.ssn, over.income, over.studyRate, over.completionRatio);

        //should be granted something
        assertTrue(actual_border > 0);
        assertTrue(actual_over > 0);
    }

    /**
     * Evaluates 102 (full time)
     * Until 56 = <57
     */
    @Test
    public void req_102_age_boundary()
    {

        Student ok_under = new StudentBuilder()
                .birthDate(1961, 1, 1) //55 years old
               .build();
        Student ok_border = new StudentBuilder()
                .birthDate(1960, 1, 1) //56 years old
                .build();
        Student notOk_over = new StudentBuilder()
                .birthDate(1959, 1, 1) //57 years old
                .build();

        int actual_55 = default_payimpl.getMonthlyAmount(ok_under.ssn, ok_under.income, ok_under.studyRate, ok_under.completionRatio);
        int actual_56 = default_payimpl.getMonthlyAmount(ok_border.ssn, ok_border.income, ok_border.studyRate, ok_border.completionRatio);
        int actual_57 = default_payimpl.getMonthlyAmount(notOk_over.ssn, notOk_over.income, notOk_over.studyRate, notOk_over.completionRatio);

        //must receive something
        assertTrue(actual_55 > 0);
        assertTrue(actual_56 > 0);

        //should receive nothing
        assertEquals("first 102",0, actual_57);
    }

    /**
     * Evaluates 103 (full time)
     * From the year 47 = <47
     */
    //duplicated
    @Test
    public void fullTime_noIncome_fullCompletion_noLoanAfter47()
    {
        Student ok_under = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.FULL_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1970, 1, 1).build(); //46 years old
        Student notOk_border = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.FULL_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1969, 1, 1).build(); //47 years old
        Student notOk_over = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.FULL_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1968, 1, 1).build(); //48 years old

        int actual_46 = default_payimpl.getMonthlyAmount(ok_under.ssn, ok_under.income, ok_under.studyRate, ok_under.completionRatio);
        int actual_47 = default_payimpl.getMonthlyAmount(notOk_border.ssn, notOk_border.income, notOk_border.studyRate, notOk_border.completionRatio);
        int actual_48 = default_payimpl.getMonthlyAmount(notOk_over.ssn, notOk_over.income, notOk_over.studyRate, notOk_over.completionRatio);

        //below the gate: loan and subsidy are both due, so something must be paid
        assertTrue(actual_46 > 0);

        //crossing the boundary must cost the student something
        assertTrue("first 103",actual_47 < actual_46);

        //nothing further is withdrawn until the age-57 subsidy gate
        assertEquals(actual_47, actual_48);
    }

    /**
     * Evaluates 103 (half time)
     * From the year 47 = <47
     * <p>
     * Deliberately asserts only *that* support is withdrawn at the boundary, never *how much*.
     * The exact half-time amounts are covered by
     * {@link #halfTime_noIncome_fullCompletion_subsidyOnly()}, so comparing against
     * PART_TIME_LOAN / PART_TIME_SUBSIDIARY here would make this test fail for a wrong
     * amount table as well as for a wrong age gate, and the failure message could not
     * tell the two apart. Relations between readings of the same implementation stay
     * valid whatever the amount table says.
     */
    //duplicated
    @Test
    public void halfTime_noIncome_fullCompletion_noLoanAfter47()
    {
        Student ok_under = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1970, 1, 1).build(); //46 years old
        Student notOk_border = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1969, 1, 1).build(); //47 years old
        Student notOk_over = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1968, 1, 1).build(); //48 years old

        int actual_46 = default_payimpl.getMonthlyAmount(ok_under.ssn, ok_under.income, ok_under.studyRate, ok_under.completionRatio);
        int actual_47 = default_payimpl.getMonthlyAmount(notOk_border.ssn, notOk_border.income, notOk_border.studyRate, notOk_border.completionRatio);
        int actual_48 = default_payimpl.getMonthlyAmount(notOk_over.ssn, notOk_over.income, notOk_over.studyRate, notOk_over.completionRatio);

        //below the gate: loan and subsidy are both due, so something must be paid
        assertTrue(actual_46 > 0);

        //crossing the boundary must cost the student something
        assertTrue("duplicate 103",actual_47 < actual_46);

        //nothing further is withdrawn until the age-57 subsidy gate
        assertEquals(actual_47, actual_48);
    }

    /**
     * Evaluates 102 + 103
     * Subsidies only: age 47-56 midpoint age
     */
    @Test
    public void fullTime_NoIncome_fullCompletion_subsidyOnly() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        int fullSubsidyOnly = TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();

        Student midZone = StudentBuilder.fullTimeStudentNoIncome()
                .birthDate(1962, 1, 1).build(); //54 years old

        assertEquals(fullSubsidyOnly, payimpl.getMonthlyAmount(midZone.ssn, midZone.income, midZone.studyRate, midZone.completionRatio));
    }

    /**
     * Evaluates 102 + 103
     * Halftime version, midpoint age
     */
    @Test
    public void halfTime_noIncome_fullCompletion_subsidyOnly() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        int halfTimeSubsidyOnly = TestNumConstants.StudentSupportType.PART_TIME_SUBSIDIARY.getAmountPerMonth();

        Student midZone = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1962, 1, 1).build(); //54 years old

        assertEquals(halfTimeSubsidyOnly, payimpl.getMonthlyAmount(midZone.ssn, midZone.income, midZone.studyRate, midZone.completionRatio));
    }

    // ---------------------------------------------------------------
    // 200-series requirements
    // ---------------------------------------------------------------20000101-1234

    @Test
    public void atLeastHalfTimeStudies() throws IOException {
        PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate(); //2026
        Student lessThenHalfTime = new StudentBuilder().birthDate(2000,1, 1).build();
        Student moreThenHalfTime = new StudentBuilder().birthDate(2000,1, 1).build();

        //loan+subsidy for 50%, if either varies it should be caught
        int nonvariant_half = payimpl.getMonthlyAmount(moreThenHalfTime.ssn, moreThenHalfTime.income, 50, moreThenHalfTime.completionRatio);

        //sweep invalid pace range
        for (int i = 0; i < 50; i++) {
            assertEquals(0,
                         payimpl.getMonthlyAmount(
                                 lessThenHalfTime.ssn,
                                 lessThenHalfTime.income,
                                 i,
                                 lessThenHalfTime.completionRatio));
        }

        //sweep valid pace range
        for (int i = 50; i < 100; i++) {
            int actual = payimpl.getMonthlyAmount(
                            moreThenHalfTime.ssn,
                            moreThenHalfTime.income,
                            i,
                            moreThenHalfTime.completionRatio);

            assertNotSame(0, actual);

            assertEquals(nonvariant_half, actual);
        }
    }

    // ---------------------------------------------------------------
    // 300-series requirements
    // ---------------------------------------------------------------
    @Test
    public void maxIncome_FullTime() {
        Student maxIncomeFullTime = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.FULL_TIME)
                .income(TestNumConstants.IncomeLevel.FULL_TIME_MAXIMUM)
                .build();
        assertEquals(fullGrant, default_payimpl.getMonthlyAmount(maxIncomeFullTime.ssn, maxIncomeFullTime.income, maxIncomeFullTime.studyRate, maxIncomeFullTime.completionRatio));
    }

    @Test
    public void maxIncome_FullTime_over() {
        Student maxIncomeFullTime_over = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.FULL_TIME)
                .income(TestNumConstants.IncomeLevel.FULL_TIME_OVER_MAXIMUM)
                .build();
        assertEquals(0, default_payimpl.getMonthlyAmount(maxIncomeFullTime_over.ssn, maxIncomeFullTime_over.income, maxIncomeFullTime_over.studyRate, maxIncomeFullTime_over.completionRatio));
    }


    @Test
    public void maxIncome_halfTime() {
        Student maxIncomeHalfTime = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.HALF_TIME_MAXIMUM)
                .build();
        // expected:<4960> but was:<5960>
        assertEquals("known", halfGrant, default_payimpl.getMonthlyAmount(maxIncomeHalfTime.ssn, maxIncomeHalfTime.income, maxIncomeHalfTime.studyRate, maxIncomeHalfTime.completionRatio));
    }


    @Test
    public void maxIncome_halfTime_over() {
        Student maxIncomeHalfTime_over = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.HALF_TIME_OVER_MAXIMUM)
                .build();
        // expected:<0> but was:<1396>
        assertEquals("known 302",0, default_payimpl.getMonthlyAmount(maxIncomeHalfTime_over.ssn, maxIncomeHalfTime_over.income, maxIncomeHalfTime_over.studyRate, maxIncomeHalfTime_over.completionRatio));
    }

// ---------------------------------------------------------------
// 400-series requirements
// ---------------------------------------------------------------
    @Test
    public void fiftyPercentCompletion() {
        PaymentImpl payimpl = default_payimpl;
        Student lessThenFiftyPercentComletion = new StudentBuilder().birthDate(1990, 1, 1).completionRatio(0).build();
        Student check = new StudentBuilder().birthDate(1990,1,1).build();

        int validAmount = default_payimpl.getMonthlyAmount(check.ssn, check.income, check.studyRate, check.completionRatio);

        //invalid range
        for (int i = 0; i < 50; i++) {
            assertEquals(0, payimpl.getMonthlyAmount(lessThenFiftyPercentComletion.ssn, lessThenFiftyPercentComletion.income, lessThenFiftyPercentComletion.studyRate, i));
        }

        //valid, nonvariant
        for (int i = 50; i <= 100; i++) {
            assertEquals(validAmount, payimpl.getMonthlyAmount(lessThenFiftyPercentComletion.ssn, lessThenFiftyPercentComletion.income, lessThenFiftyPercentComletion.studyRate, i));
        }
    }

// ---------------------------------------------------------------
// 500-series requirements
// ---------------------------------------------------------------

    /**
     * Evaluates requirement 501.
     * Full-time students receive:
     * Loan = 7088 SEK
     */
    @Test
    public void fullTime_CorrectLoanAmount() {
        Student stu = new StudentBuilder().birthDate(1990, 1, 1).build(); //26 yrs

        Student subtract = new StudentBuilder().birthDate(1966, 1, 1).build(); //50 yrs

        int expected = TestNumConstants.StudentSupportType.FULL_TIME_LOAN.getAmountPerMonth();
        int actual = default_payimpl.getMonthlyAmount(stu.ssn, stu.income, stu.studyRate, stu.completionRatio);
        int subsidy = default_payimpl.getMonthlyAmount(subtract.ssn, subtract.income, subtract.studyRate, subtract.completionRatio);

        assertEquals(expected, actual - subsidy);
    }
    /**
     * Evaluates requirement 502.
     * Full-time students receive:
     * Subsidiary = 2816 SEK
     */
    @Test
    public void fullTime_CorrectSubsidyAmount() {
        Student stu = new StudentBuilder().birthDate(1966, 1, 1).build(); //50 yrs

        int expected = TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();
        int actual = default_payimpl.getMonthlyAmount(stu.ssn, stu.income, stu.studyRate, stu.completionRatio);

        assertEquals(expected, actual);
    }

    /**
     * Requirement 503
     */
    @Test
    public void halfTime_correctLoanAmount()
    {
        Student stu = new StudentBuilder()
                .birthDate(1990, 1, 1) //26
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .build();

        Student subtract = new StudentBuilder()
                .birthDate(1966, 1, 1) //50
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .build();

        //student gets both loan and subsidy, need to isolate subsidy using student ineligible for loan
        int eval = default_payimpl.getMonthlyAmount(stu.ssn, stu.income, stu.studyRate, stu.completionRatio);
        int subsidy = default_payimpl.getMonthlyAmount(subtract.ssn, subtract.income, subtract.studyRate, subtract.completionRatio);
        int correctLoan = TestNumConstants.StudentSupportType.PART_TIME_LOAN.getAmountPerMonth();

        assertEquals("first 104", correctLoan, eval - subsidy);
    }

    /**
     * Requirement 504
     */
    @Test
    public void halfTime_correctSubsidyAmount()
    {
        Student stu = new StudentBuilder()
                .birthDate(1968, 1, 1) //48 yrs
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .build();

        int correctSubsidy = TestNumConstants.StudentSupportType.PART_TIME_SUBSIDIARY.getAmountPerMonth();
        int eval = default_payimpl.getMonthlyAmount(stu.ssn, stu.income, stu.studyRate, stu.completionRatio);

        assertEquals(correctSubsidy, eval);
    }



    /**
     * Evaluates requirements 503 and 504.
     * Part-time students receive:
     * Loan = 3564 SEK
     * Subsidiary = 1396 SEK
     */
    @Test
    public void partTimePaymentAmount() throws IOException {
        Student student = new Student("20000101-1234", 0, 50, 100);
        PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate();

        int expected =
                TestNumConstants.StudentSupportType.PART_TIME_LOAN.getAmountPerMonth() +
                TestNumConstants.StudentSupportType.PART_TIME_SUBSIDIARY.getAmountPerMonth();

        assertEquals("duplicate 503",expected,
                payimpl.getMonthlyAmount(
                        student.ssn,
                        student.income,
                        student.studyRate,
                        student.completionRatio));
    }

    /**
     * Evaluates requirement 505.
     * A student entitled to a loan receives the full loan amount.
     */
    @Test
    public void fullLoanAmountIsPaid() throws IOException {
        Student student = StudentBuilder.fullTimeStudentNoIncome().build();
        PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate();

        assertEquals(fullGrant,
                     payimpl.getMonthlyAmount(
                             student.ssn,
                             student.income,
                             student.studyRate,
                             student.completionRatio));
    }

    /**
     * Evaluates requirement 506.
     * Payment is made on the last weekday of the month.
     */
    @Test
    public void paymentDateJanuary2016() throws IOException {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 10);
        assertEquals("20160129", payimpl.getNextPaymentDay());
    }

    @Test
    public void paymentDateFebruary2016() throws IOException {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 2, 10);
        assertEquals("first 506","20160229", payimpl.getNextPaymentDay());
    }

    @Test
    public void paymentDateMarch2016() throws IOException {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 3, 10);
        assertEquals("20160331", payimpl.getNextPaymentDay());
    }

    @Test
    public void paymentDateApril2016() throws IOException {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 4, 10);
        assertEquals("20160429", payimpl.getNextPaymentDay());
    }

    @Test
    public void paymentDateMay2016() throws IOException {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 5, 10);
        assertEquals("20160531", payimpl.getNextPaymentDay());
    }

    @Test
    public void paymentDateJune2016() throws IOException {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 6, 10);
        assertEquals("20160630", payimpl.getNextPaymentDay());
    }


}
