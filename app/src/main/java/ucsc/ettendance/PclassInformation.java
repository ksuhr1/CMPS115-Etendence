package ucsc.ettendance;

/**
 * Created by katelynsuhr on 2/16/18.
 */

public class PclassInformation {
    public String className;
    public String classQuarter;
    public String classCode;
    public String classPin;
    public String owner;

    // Needed for Firebase
    public PclassInformation() {}

    public PclassInformation(String className, String classQuarter, String classCode, String classPin, String owner)
    {
        this.className = className;
        this.classQuarter = classQuarter;
        this.classCode = classCode;
        this.classPin = classPin;
        this.owner = owner;
    }

    public String getClassName()
    {
        return className;
    }
    public String getClassQuarter()
    {
        return classQuarter;
    }
    public String getClassCode()
    {
        return classCode;
    }
    public String getClassPin()
    {
        return classPin;
    }
    public String getOwner()
    {
        return owner;
    }
}
