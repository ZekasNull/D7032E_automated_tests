package ltu;

public class TestNumConstants {
    public enum StudentSupportType {
        FULL_TIME_LOAN(7088),
        FULL_TIME_SUBSIDIARY(2816),
        PART_TIME_LOAN(3564),
        PART_TIME_SUBSIDIARY(1396);

        private final int amountPerMonth;

        StudentSupportType(int amountPerMonth) {
            this.amountPerMonth = amountPerMonth;
        }

        public int getAmountPerMonth() {
            return amountPerMonth;
        }
    }

    public enum StudyRate {
        LESS_THAN_HALF_TIME(49),
        HALF_TIME(50),
        LESS_THAN_FULL_TIME(99),
        FULL_TIME(100);

        private final int subsidiaryPercentage;

        StudyRate(int subsidiaryPercentage) {
            this.subsidiaryPercentage = subsidiaryPercentage;
        }

        public int getSubsidiaryPercentage() {
            return subsidiaryPercentage;
        }
    }

    public enum IncomeLevel {
        NO_INCOME(0),
        HALF_TIME_MAXIMUM(128722),
        FULL_TIME_MAXIMUM (85813),
        HALF_TIME_OVER_MAXIMUM(128723),
        FULL_TIME_OVER_MAXIMUM(85814);

        private final int income;

        IncomeLevel(int income) {
            this.income = income;
        }

        public int getIncomeLevel() {
            return income;
        }
    }

    public enum CompletionRatio {
        FIFTY_PERCENT(50),
        LESS_THAN_FIFTY(49),
        MORE_THAN_FIFTY(51);

        private final int completionRatio;
        CompletionRatio(int completionRatio) {
            this.completionRatio = completionRatio;
        }
        public int getCompletionRatio() {
            return completionRatio;
        }
    }

}
