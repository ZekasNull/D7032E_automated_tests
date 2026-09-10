package ltu;

/**
 * Builds {@link Student} instances for tests.
 * <p>
 * If no parameters are given, the defaults are:
 * <ul>
 *     <li>SSN: SSN for 1990-01-01</li>
 *     <li>income: {@link TestNumConstants.IncomeLevel#NO_INCOME}</li>
 *     <li>studyRate: {@link TestNumConstants.StudyRate#FULL_TIME} (%)</li>
 *     <li>completionRatio: 100 (%)</li>
 * </ul>
 */
public class StudentBuilder {
    private String personId = createPersonId(1990, 1, 1);
    private TestNumConstants.IncomeLevel income = TestNumConstants.IncomeLevel.NO_INCOME;
    private TestNumConstants.StudyRate studyRate = TestNumConstants.StudyRate.FULL_TIME;
    private int completionRatio = 100;

    public StudentBuilder personId(String personId) {
        this.personId = personId;
        return this;
    }

    public StudentBuilder birthDate(int year, int month, int day) {
        this.personId = createPersonId(year, month, day);
        return this;
    }


    public StudentBuilder income(TestNumConstants.IncomeLevel income) {
        this.income = income;
        return this;
    }

    public StudentBuilder studyRate(TestNumConstants.StudyRate studyRate) {
        this.studyRate = studyRate;
        return this;
    }

    public StudentBuilder completionRatio(int completionRatio) {
        this.completionRatio = completionRatio;
        return this;
    }

    public Student build() {
        return new Student(
                personId,
                income.getIncomeLevel(),
                studyRate.getSubsidiaryPercentage(),
                completionRatio);
    }

    private String createPersonId(int year, int month, int day) {
        return String.format("%04d%02d%02d-1234", //format requires four magic numbers hyphenated at the end
                             year, month, day);
    }

    // Named presets for common test scenarios. Expand as needed!

    public static StudentBuilder fullTimeStudentNoIncome() {
        return new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.FULL_TIME)
                .income(TestNumConstants.IncomeLevel.NO_INCOME);
    }

    public static StudentBuilder halfTimeStudentAtIncomeCap() {
        return new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.HALF_TIME)
                .income(TestNumConstants.IncomeLevel.HALF_TIME_MAXIMUM);
    }

    public static StudentBuilder fullTimeStudentAtIncomeCap() {
        return new StudentBuilder()
                .studyRate(TestNumConstants.StudyRate.FULL_TIME)
                .income(TestNumConstants.IncomeLevel.FULL_TIME_MAXIMUM);
    }
}