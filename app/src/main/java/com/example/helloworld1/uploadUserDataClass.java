package com.example.helloworld1;

public class uploadUserDataClass {
    public String firstName, LastName, dateOfBirth, profilePicUrl;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public uploadUserDataClass(String firstName, String lastName, String dateOfBirth, String profilePicUrl) {
        this.firstName = firstName;
        LastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.profilePicUrl = profilePicUrl;
    }

    public uploadUserDataClass() {
    }
}
