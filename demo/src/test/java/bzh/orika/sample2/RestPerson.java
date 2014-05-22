package bzh.orika.sample2;

import java.util.Date;

public class RestPerson {
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String[][] aliases;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String[][] getAliases() {
        return aliases;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setAliases(String[][] aliases) {
        this.aliases = aliases;
    }
}
