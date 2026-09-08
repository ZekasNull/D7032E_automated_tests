package ltu;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class PaymentTest {
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
    public void invalidPersonId_null() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        payimpl.getMonthlyAmount(null, 0, 100, 100);
    }

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

    // ---------------------------------------------------------------
    // 100-series requirements
    // ---------------------------------------------------------------

    /**
     * Evaluates 101 (full time)
     * @throws IOException
     */
    @Test
    public void fullTime_noIncome_fullCompletion_noGrantBefore20() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        int grantedSubsidy =
                TestNumConstants.StudentSupportType.FULL_TIME_LOAN.getAmountPerMonth() +
                        TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();

        Student tooYoung = new StudentBuilder().birthDate(1997, 1, 1).build();
        Student ok = new StudentBuilder().birthDate(1996, 1, 1).build();
        Student ok_over = new StudentBuilder().birthDate(1995, 1, 1).build();


        assertEquals(0, payimpl.getMonthlyAmount(tooYoung.ssn, tooYoung.income, tooYoung.studyRate, tooYoung.completionRatio));
        assertEquals(grantedSubsidy, payimpl.getMonthlyAmount(ok.ssn, ok.income, ok.studyRate, ok.completionRatio));
        assertEquals(grantedSubsidy, payimpl.getMonthlyAmount(ok_over.ssn, ok_over.income, ok_over.studyRate, ok_over.completionRatio));
    }

    /**
     * Evaluates 101 (half pace)
     * @throws IOException
     */
    @Test
    public void halfTime_noIncome_fullCompletion_noGrantBefore20() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        int halfTimeGrant =
                TestNumConstants.StudentSupportType.PART_TIME_LOAN.getAmountPerMonth() +
                TestNumConstants.StudentSupportType.PART_TIME_SUBSIDIARY.getAmountPerMonth();

        Student tooYoung = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1997, 1, 1).build();
        Student ok = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1996, 1, 1).build();

        assertEquals(0, payimpl.getMonthlyAmount(tooYoung.ssn, tooYoung.income, tooYoung.studyRate, tooYoung.completionRatio));
        assertEquals(halfTimeGrant, payimpl.getMonthlyAmount(ok.ssn, ok.income, ok.studyRate, ok.completionRatio));
    }

    /**
     * Evaluates 102 (full time)
     * Until 56 = <57
     */
    @Test
    public void fullTime_noIncome_fullCompletion_noGrantAfter56() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        Student ok_under = StudentBuilder.fullTimeStudentNoIncome()
                                   .birthDate(1961, 1, 1) //55 years old
                                   .build();
        Student ok_border = StudentBuilder.fullTimeStudentNoIncome()
                .birthDate(1960, 1, 1) //56 years old
                .build();
        Student notOk_over = StudentBuilder
                .fullTimeStudentNoIncome()
                .birthDate(1959, 1, 1).build(); //57 years old

        int onlysubsidy = TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();


        assertEquals(onlysubsidy,
                payimpl.getMonthlyAmount(ok_under.ssn, ok_under.income, ok_under.studyRate, ok_under.completionRatio));
        assertEquals(onlysubsidy,
                payimpl.getMonthlyAmount(ok_border.ssn, ok_border.income, ok_border.studyRate, ok_border.completionRatio));
        assertEquals(0,
                payimpl.getMonthlyAmount(notOk_over.ssn, notOk_over.income, notOk_over.studyRate, notOk_over.completionRatio));
    }

    /**
     * Evaluates 102
     * Half-time study rate at the age-57 boundary
     */
    @Test
    public void halfTime_noIncome_fullCompletion_noGrantAfter57() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        int halfTimeSubsidyOnly = TestNumConstants.StudentSupportType.PART_TIME_SUBSIDIARY.getAmountPerMonth();

        Student ok_under = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1961, 1, 1).build(); //55 years old
        Student ok_border = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1960, 1, 1).build(); //56 years old
        Student notOk_over = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1959, 1, 1).build(); //57 years old

        assertEquals(halfTimeSubsidyOnly, payimpl.getMonthlyAmount(ok_under.ssn, ok_under.income, ok_under.studyRate, ok_under.completionRatio));
        assertEquals(halfTimeSubsidyOnly, payimpl.getMonthlyAmount(ok_border.ssn, ok_border.income, ok_border.studyRate, ok_border.completionRatio));
        assertEquals(0, payimpl.getMonthlyAmount(notOk_over.ssn, notOk_over.income, notOk_over.studyRate, notOk_over.completionRatio));
    }

    /**
     * Evaluates 103
     * From the year 47 = <47
     */
    @Test
    public void fullTime_noIncome_fullCompletion_noLoanAfter47() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);

        Student ok_under = StudentBuilder.fullTimeStudentNoIncome()
                .birthDate(1970, 1, 1) //46 years old
                .build();
        Student notOk_border = StudentBuilder.fullTimeStudentNoIncome()
                .birthDate(1969, 1, 1) //47 years old
                .build();
        Student notOk_over = StudentBuilder
                .fullTimeStudentNoIncome()
                .birthDate(1968, 1, 1).build(); //48 years old

        int fullGrant = TestNumConstants.StudentSupportType.FULL_TIME_LOAN.getAmountPerMonth() +
                TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();

        int onlySubsidy = TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();

        assertEquals(fullGrant,
                payimpl.getMonthlyAmount(ok_under.ssn, ok_under.income, ok_under.studyRate, ok_under.completionRatio));
        assertEquals(onlySubsidy,
                payimpl.getMonthlyAmount(notOk_border.ssn, notOk_border.income, notOk_border.studyRate, notOk_border.completionRatio));
        assertEquals(onlySubsidy,
                payimpl.getMonthlyAmount(notOk_over.ssn, notOk_over.income, notOk_over.studyRate, notOk_over.completionRatio));
    }

    /**
     * Evaluates 103
     * Half-time study rate at the age-47 boundary
     */
    @Test
    public void halfTime_noIncome_fullCompletion_noLoanAfter47() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        int halfTimeGrant =
                TestNumConstants.StudentSupportType.PART_TIME_LOAN.getAmountPerMonth() +
                TestNumConstants.StudentSupportType.PART_TIME_SUBSIDIARY.getAmountPerMonth();
        int halfTimeSubsidyOnly = TestNumConstants.StudentSupportType.PART_TIME_SUBSIDIARY.getAmountPerMonth();

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

        assertEquals(halfTimeGrant, payimpl.getMonthlyAmount(ok_under.ssn, ok_under.income, ok_under.studyRate, ok_under.completionRatio));
        assertEquals(halfTimeSubsidyOnly, payimpl.getMonthlyAmount(notOk_border.ssn, notOk_border.income, notOk_border.studyRate, notOk_border.completionRatio));
        assertEquals(halfTimeSubsidyOnly, payimpl.getMonthlyAmount(notOk_over.ssn, notOk_over.income, notOk_over.studyRate, notOk_over.completionRatio));
    }

    /**
     * Evaluates 102+ 103
     * Subsidies only: age 47-56
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
     * Halftime version
     */
    @Test
    public void halfTime_noIncome_fullCompletion_subsidyOnly() throws IOException
    {
        PaymentImpl payimpl = this.getPaymentImplCustomDate(2016, 1, 1);
        int halfTimeSubsidyOnly = TestNumConstants.StudentSupportType.PART_TIME_SUBSIDIARY.getAmountPerMonth();

        Student midZone = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME)
                .birthDate(1966, 1, 1).build(); //50 years old

        assertEquals(halfTimeSubsidyOnly, payimpl.getMonthlyAmount(midZone.ssn, midZone.income, midZone.studyRate, midZone.completionRatio));
    }

    // ---------------------------------------------------------------
    // 200-series requirements
    // ---------------------------------------------------------------
    @Test
    public void atLeastHalfTimeStudies() throws IOException {
        for (int i = 0; i < 50; i++) {
            Student lessThenHalfTime = new Student("20000101-1234", 0, i, 100);
            PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate();

            assertEquals(0, payimpl.getMonthlyAmount(lessThenHalfTime.ssn, lessThenHalfTime.income, lessThenHalfTime.studyRate, lessThenHalfTime.completionRatio));
        }

        for (int i = 50; i < 100; i++) {
            Student moreThenHalfTime = new Student("20000101-1234", 0, i, 100);
            PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate();

            assertNotSame(0, payimpl.getMonthlyAmount(moreThenHalfTime.ssn, moreThenHalfTime.income, moreThenHalfTime.studyRate, moreThenHalfTime.completionRatio));
            assertEquals(halfGrant, payimpl.getMonthlyAmount(moreThenHalfTime.ssn, moreThenHalfTime.income, moreThenHalfTime.studyRate, moreThenHalfTime.completionRatio));
        }

        Student fulltimeRate = new Student("20000101-1234", 0, 100, 100);
        PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate();
        assertEquals(fullGrant, payimpl.getMonthlyAmount(fulltimeRate.ssn, fulltimeRate.income, fulltimeRate.studyRate, fulltimeRate.completionRatio));
    }


    // ---------------------------------------------------------------
    // 300-series requirements
    // ---------------------------------------------------------------
    @Test
    public void maxIncome() throws IOException {
        Student maxIncomeFullTime = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.FULL_TIME)
                .income(TestNumConstants.IncomeLevel.FULL_TIME_MAXIMUM)
                .build();
        Student maxIncomeFullTime_over = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.FULL_TIME)
                .income(TestNumConstants.IncomeLevel.FULL_TIME_OVER_MAXIMUM)
                .build();

        Student maxIncomeHalfTime = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.HALF_TIME_MAXIMUM)
                .build();
        Student maxIncomeHalfTime_over = new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.HALF_TIME_OVER_MAXIMUM)
                .build();
        PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate();

        assertEquals(fullGrant, payimpl.getMonthlyAmount(maxIncomeFullTime.ssn, maxIncomeFullTime.income, maxIncomeFullTime.studyRate, maxIncomeFullTime.completionRatio));
        assertEquals(0, payimpl.getMonthlyAmount(maxIncomeFullTime_over.ssn, maxIncomeFullTime_over.income, maxIncomeFullTime_over.studyRate, maxIncomeFullTime_over.completionRatio));
        assertEquals(halfGrant, payimpl.getMonthlyAmount(maxIncomeHalfTime.ssn, maxIncomeHalfTime.income, maxIncomeHalfTime.studyRate, maxIncomeHalfTime.completionRatio));
        assertEquals(0, payimpl.getMonthlyAmount(maxIncomeHalfTime_over.ssn, maxIncomeHalfTime_over.income, maxIncomeHalfTime_over.studyRate, maxIncomeHalfTime_over.completionRatio));
    }


// ---------------------------------------------------------------
// 400-series requirements
// ---------------------------------------------------------------
    @Test
    public void fiftyPercentCompletion() throws IOException {
        for (int i = 0; i < 50; i++) {
            Student lessThenFiftyPercentComletion = new StudentBuilder().completionRatio(i).build();
            PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate();

            assertEquals(0, payimpl.getMonthlyAmount(lessThenFiftyPercentComletion.ssn, lessThenFiftyPercentComletion.income, lessThenFiftyPercentComletion.studyRate, lessThenFiftyPercentComletion.completionRatio));
        }

        for (int i = 50; i <= 100; i++) {
            Student lessThenFiftyPercentComletion = new StudentBuilder().completionRatio(i).build();
            PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate();

            assertNotSame(0, payimpl.getMonthlyAmount(lessThenFiftyPercentComletion.ssn, lessThenFiftyPercentComletion.income, lessThenFiftyPercentComletion.studyRate, lessThenFiftyPercentComletion.completionRatio));
        }
    }

// ---------------------------------------------------------------
// 500-series requirements
// ---------------------------------------------------------------
    /**
     * Evaluates requirements 501 and 502.
     * Full-time students receive:
     * Loan = 7088 SEK
     * Subsidiary = 2816 SEK
     */
    @Test
    public void fullTimePaymentAmount() throws IOException {
        Student student = StudentBuilder.fullTimeStudentNoIncome().build();
        PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate();

        int expected =
                TestNumConstants.StudentSupportType.FULL_TIME_LOAN.getAmountPerMonth() +
                TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();

        assertEquals(expected,
                payimpl.getMonthlyAmount(
                        student.ssn,
                        student.income,
                        student.studyRate,
                        student.completionRatio));
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

        assertEquals(expected,
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
        assertEquals("20160229", payimpl.getNextPaymentDay());
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
