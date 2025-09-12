package htmlflow.test.model;

/**
 * @author Pedro Fialho
 **/
public class Student {
    private final long nr;
    private final String name;
    
    public Student(long nr, String name) {
        this.nr = nr;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return String.format("Student nr %d with name %s",nr, name);
    }
    
    public long getNr() {
        return nr;
    }
    
    public String getName() {
        return name;
    }
}
