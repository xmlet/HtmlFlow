package htmlflow.test.model;

public class StudentWithGrade {
    private final String name;
    private final double grade;

    public StudentWithGrade(String name, double grade) {
        this.name = name;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public double getGrade() {
        return grade;
    }
}
