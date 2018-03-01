package ucsc.ettendance;

public class UserInformation
{
    public String firstName;
    public String lastName;
    public String studentId;
    public String password;
    public boolean isProfessor;
    public String email;

    // Needed for Firebase
    public UserInformation()
    {}

    public UserInformation(String firstName, String lastName, String studentId, boolean isProfessor, String email, String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.isProfessor = isProfessor;
        this.email = email;
        this.password = password;
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
    public String getPassword() { return password;}

    //public UserInformation() { }

}
