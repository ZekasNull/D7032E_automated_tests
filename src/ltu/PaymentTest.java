package ltu;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;

public class PaymentTest {
    private PaymentImpl getPaymentImplInstanceCurrentDate() throws IOException {
        //TODO the provided CalendarImpl is always the current date
        return new PaymentImpl(new CalendarImpl());
    }

    @Test
    public void testSilly() {
        assertEquals(1, 1);
    }


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
        Student fulltimeRate = new StudentBuilder().studyRate(TestNumConstants.StudyRate.FULL_TIME).build();
        Student lessthanfulltimeRate = new StudentBuilder().studyRate(TestNumConstants.StudyRate.LESS_THAN_FULL_TIME).build();
        Student halftimeRate = new StudentBuilder().studyRate(TestNumConstants.StudyRate.HALF_TIME).build();
        Student lessthanhalftimeRate = new StudentBuilder().studyRate(TestNumConstants.StudyRate.LESS_THAN_HALF_TIME).build();

        PaymentImpl payimpl = this.getPaymentImplInstanceCurrentDate();

        int fullGrant = TestNumConstants.StudentSupportType.FULL_TIME_LOAN.getAmountPerMonth() +
                TestNumConstants.StudentSupportType.FULL_TIME_SUBSIDIARY.getAmountPerMonth();
        int halfGrant = TestNumConstants.StudentSupportType.PART_TIME_LOAN.getAmountPerMonth() +
                TestNumConstants.StudentSupportType.PART_TIME_SUBSIDIARY.getAmountPerMonth();

        // Full time
        assertEquals(fullGrant, payimpl.getMonthlyAmount(fulltimeRate.ssn, fulltimeRate.income, fulltimeRate.studyRate, fulltimeRate.completionRatio));

        // Less than full time, more than half time
        assertEquals(halfGrant, payimpl.getMonthlyAmount(lessthanfulltimeRate.ssn, lessthanfulltimeRate.income, lessthanfulltimeRate.studyRate, lessthanfulltimeRate.completionRatio));
        assertEquals(halfGrant, payimpl.getMonthlyAmount(halftimeRate.ssn, halftimeRate.income, halftimeRate.studyRate, halftimeRate.completionRatio));

        // Below half time
        assertEquals(0, payimpl.getMonthlyAmount(lessthanhalftimeRate.ssn, lessthanhalftimeRate.income, lessthanhalftimeRate.studyRate, lessthanhalftimeRate.completionRatio));
    }


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
