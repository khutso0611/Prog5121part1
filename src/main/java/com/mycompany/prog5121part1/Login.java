package com.mycompany.prog5121part1;

public class Login {

    // User information
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;

    // Constructor
    public Login(String firstName, String lastName, String username,
                 String password, String cellPhoneNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    // Check username
    // Username must contain an underscore
    // and must not be more than 5 characters long
    public boolean checkUserName() {

        return username.contains("_") && username.length() <= 5;
    }

    // Check password complexity
    // Password must:
    // - Have at least 8 characters
    // - Have a capital letter
    // - Have a number
    // - Have a special character
    public boolean checkPasswordComplexity() {

        boolean hasCapitalLetter = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;

        if (password.length() < 8) {
            return false;
        }

        for (int i = 0; i < password.length(); i++) {

            char character = password.charAt(i);

            if (Character.isUpperCase(character)) {
                hasCapitalLetter = true;
            }

            if (Character.isDigit(character)) {
                hasNumber = true;
            }

            if (!Character.isLetterOrDigit(character)) {
                hasSpecialCharacter = true;
            }
        }

        return hasCapitalLetter
                && hasNumber
                && hasSpecialCharacter;
    }

    // Check cellphone number
    // South African number must start with +27
    public boolean checkCellPhoneNumber() {

        String regex = "^\\+27\\d{9}$";

        return cellPhoneNumber.matches(regex);
    }

    // Register user
    public String registerUser() {

        if (!checkUserName()) {

            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity()) {

            return "Password is not correctly formatted; please ensure that your password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber()) {

            return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        }

        return "Username successfully captured.";
    }

    // Check login details
    public boolean loginUser(String enteredUsername, String enteredPassword) {

        return enteredUsername.equals(username)
                && enteredPassword.equals(password);
    }

    // Return login status
    public String returnLoginStatus(String enteredUsername,
                                    String enteredPassword) {

        if (loginUser(enteredUsername, enteredPassword)) {

            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you again.";
        }

        return "Username or password incorrect, please try again.";
    }
}