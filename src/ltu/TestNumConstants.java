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
        LESS_THAN_HALF_TIME(0),
        HALF_TIME(50),
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
        FULL_TIME_MAXIMUM (85813);

        private final int income;

        IncomeLevel(int income) {
            this.income = income;
        }

        public int getIncomeLevel() {
            return income;
        }
    }




}
