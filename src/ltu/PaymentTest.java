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

    int fullGrant = TestNumConstants.StudentSupportType.FULL_TIME_LOAN.getAmountPerMonth() +
            TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();
    int halfGrant = TestNumConstants.StudentSupportType.PART_TIME_LOAN.getAmountPerMonth() +
            TestNumConstants.StudentSupportType.PART_TIME_SUBSIDIARY.getAmountPerMonth();


    // ---------------------------------------------------------------
    // 100-series requirements
    // ---------------------------------------------------------------

    /**
     * Evaluates 101
     *
     * @throws IOException
     */
    @Test
    public void validSubsidy_justTurned20Boundary() throws IOException {
        PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate(); //FIXME use 2016 + adjust birthdays
        int grantedSubsidy =
                TestNumConstants.StudentSupportType.FULL_TIME_LOAN.getAmountPerMonth() +
                        TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();

        //exact birthday is not relevant
        Student tooYoung = new StudentBuilder().birthDate(2007, 1, 1).build();
        Student ok = new StudentBuilder().birthDate(2006, 1, 1).build();
        Student ok_over = new StudentBuilder().birthDate(2005, 1, 1).build();


        assertEquals(0, payimpl.getMonthlyAmount(tooYoung.ssn, tooYoung.income, tooYoung.studyRate, tooYoung.completionRatio));
        assertEquals(grantedSubsidy, payimpl.getMonthlyAmount(ok.ssn, ok.income, ok.studyRate, ok.completionRatio));
        assertEquals(grantedSubsidy, payimpl.getMonthlyAmount(ok_over.ssn, ok_over.income, ok_over.studyRate, ok_over.completionRatio));
    }

    /**
     * Evaluates 102
     * Until 56 = <57
     */
    @Test
    public void noSubsidyAfter56() throws IOException {
        PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate(); //FIXME use 2016 + adjust birthdays

        Student ok_under = StudentBuilder.fullTimeStudentNoIncome()
                .birthDate(1971, 1, 1) //55 years old
                .build();
        Student ok_border = StudentBuilder.fullTimeStudentNoIncome()
                .birthDate(1970, 1, 1) //56 years old
                .build();
        Student notOk_over = StudentBuilder
                .fullTimeStudentNoIncome()
                .birthDate(1969, 1, 1).build(); //57 years old

        int onlysubsidy = TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();


        assertEquals(onlysubsidy,
                payimpl.getMonthlyAmount(ok_under.ssn, ok_under.income, ok_under.studyRate, ok_under.completionRatio));
        assertEquals(onlysubsidy,
                payimpl.getMonthlyAmount(ok_border.ssn, ok_border.income, ok_border.studyRate, ok_border.completionRatio));
        assertEquals(0,
                payimpl.getMonthlyAmount(notOk_over.ssn, notOk_over.income, notOk_over.studyRate, notOk_over.completionRatio));
    }

    /**
     * Evaluates 103
     * From the year 47 = <47
     */
    @Test
    public void noLoanAfter47() throws IOException {
        PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate(); //FIXME use 2016 + adjust birthdays

        Student ok_under = StudentBuilder.fullTimeStudentNoIncome()
                .birthDate(1980, 1, 1) //46 years old
                .build();
        Student notOk_border = StudentBuilder.fullTimeStudentNoIncome()
                .birthDate(1979, 1, 1) //47 years old
                .build();
        Student notOk_over = StudentBuilder
                .fullTimeStudentNoIncome()
                .birthDate(1978, 1, 1).build(); //48 years old

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

}
