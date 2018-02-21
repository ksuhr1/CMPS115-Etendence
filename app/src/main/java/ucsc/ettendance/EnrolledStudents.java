package ucsc.ettendance;

/**
 * Created by Matt on 2/17/18.
 */

public class EnrolledStudents {
    public String uid;

    public EnrolledStudents() {
    }

    public EnrolledStudents(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return uid;
    }
}