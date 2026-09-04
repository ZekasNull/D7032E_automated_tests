package ltu;

public class Student {
    String ssn;
    int income;
    int studyRate;
    int completionRatio;

    public Student(String ssn,
                   int income,
                   int studyRate,
                   int completionRatio)
    {
        this.ssn = ssn;
        this.income = income;
        this.studyRate = studyRate;
        this.completionRatio = completionRatio;
    }

    @Override
    public String toString()
    {
        return "Student{" +
                "ssn='" + ssn + '\'' +
                ", income=" + income +
                ", studyRate=" + studyRate +
                ", completionRatio=" + completionRatio +
                '}';
    }
}
