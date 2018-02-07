package ucsc.ettendance;

public class UserInformation
{
    public String firstName;
    public String lastName;
    public String studentId;
    public boolean isProfessor;
    public String email;

    // Needed for Firebase
    public UserInformation()
    {

    }

    public UserInformation(String firstName, String lastName, String studentId, boolean isProfessor, String email){
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.isProfessor = isProfessor;
        this.email = email;
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

    public boolean isProfessor()
    {
        return isProfessor;
    }

    public String getEmail()
    {
        return email;
    }

    //public UserInformation() { }

}
