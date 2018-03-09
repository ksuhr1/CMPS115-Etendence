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

    //Returns the first name of user
    public String getFirstName()
    {
        return firstName;
    }

    //Returns the last name of the user
    public String getLastName()
    {
        return lastName;
    }

    //Returns the student ID of the student
    public String getStudentId()
    {
        return studentId;
    }

    //Returns the boolean that is based on if the user is a professor or student
    public boolean isProfessor()
    {
        return isProfessor;
    }

    //Return user email
    public String getEmail()
    {
        return email;
    }

    //Return user password
    public String getPassword() { return password;}



}
