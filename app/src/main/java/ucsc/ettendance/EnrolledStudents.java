package ucsc.ettendance;

/**
 * Created by Matt on 2/17/18.
 */

public class EnrolledStudents
{
    public String firstName;
    public String lastName;
    public String email;
    public String studentId;

    public EnrolledStudents() {}

    public EnrolledStudents(String firstName, String lastName, String email, String studentId)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.studentId = studentId;
    }

    public String getFirstName()
    {
        return firstName;
    }
    public String getLastName()
    {
        return lastName;
    }
    public String getStudentId()
    {
        return studentId;
    }
    public String getEmail()
    {
        return email;
    }
}
