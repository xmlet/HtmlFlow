package htmlflow.test.model;

import org.reactivestreams.Publisher;

import java.util.stream.Stream;

public class AsyncDynamicModel {
    private final String[] titles;
    private final String[] studentAndGradesTitles;
    private final Publisher<Student> students;
    private final Stream<StudentWithGrade> withGradeStream;

    public AsyncDynamicModel(String[] titles, String[] studentAndGradesTitles, Publisher<Student> students, Stream<StudentWithGrade> withGradeStream) {
        this.titles = titles;
        this.studentAndGradesTitles = studentAndGradesTitles;
        this.students = students;
        this.withGradeStream = withGradeStream;
    }

    public Publisher<Student> getStudents() {
        return students;
    }

    public Stream<StudentWithGrade> getWithGradeStream() {
        return withGradeStream;
    }

    public String[] getTitles() {
        return titles;
    }

    public String[] getStudentAndGradesTitles() {
        return studentAndGradesTitles;
    }
}
