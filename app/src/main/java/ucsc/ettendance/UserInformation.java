package ucsc.ettendance;

/**
 * Created by katelynsuhr on 1/31/18.
 */

public class UserInformation {
    public String firstName;
    public String lastName;
    public String email;
    public String studentId;
    public boolean isProfessor;

    public UserInformation(String firstName, String lastName, boolean isProfessor, String email){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        //this.studentId = studentId;
        this.isProfessor = isProfessor;
    }

    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }

    public String getId(){
        return studentId;
    }

    public boolean isProf(){
        return isProfessor;
    }


}
