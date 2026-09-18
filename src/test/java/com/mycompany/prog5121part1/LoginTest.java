package com.mycompany.prog5121part1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    @Test
    public void testCheckUserNameCorrect() {

        Login user = new Login(
                "Kyle",
                "Smith",
                "kyl_1",
                "Ch&sec0ke99!",
                "+27833869876"
        );

        assertTrue(user.checkUserName());
    }

    @Test
    public void testCheckUserNameIncorrect() {

        Login user = new Login(
                "Kyle",
                "Smith",
                "kyle",
                "Ch&sec0ke99!",
                "+27833869876"
        );

        assertFalse(user.checkUserName());
    }

    @Test
    public void testCheckPasswordComplexityCorrect() {

        Login user = new Login(
                "Kyle",
                "Smith",
                "kyl_1",
                "Ch&sec0ke99!",
                "+27833869876"
        );

        assertTrue(user.checkPasswordComplexity());
    }

    @Test
    public void testCheckPasswordComplexityIncorrect() {

        Login user = new Login(
                "Kyle",
                "Smith",
                "kyl_1",
                "password",
                "+27833869876"
        );

        assertFalse(user.checkPasswordComplexity());
    }

    @Test
    public void testCheckCellPhoneNumberCorrect() {

        Login user = new Login(
                "Kyle",
                "Smith",
                "kyl_1",
                "Ch&sec0ke99!",
                "+27833869876"
        );

        assertTrue(user.checkCellPhoneNumber());
    }

    @Test
    public void testCheckCellPhoneNumberIncorrect() {

        Login user = new Login(
                "Kyle",
                "Smith",
                "kyl_1",
                "Ch&sec0ke99!",
                "0833869876"
        );

        assertFalse(user.checkCellPhoneNumber());
    }

    @Test
    public void testLoginCorrect() {

        Login user = new Login(
                "Kyle",
                "Smith",
                "kyl_1",
                "Ch&sec0ke99!",
                "+27833869876"
        );

        assertEquals(
                "Welcome Kyle, Smith it is great to see you again.",
                user.returnLoginStatus("kyl_1", "Ch&sec0ke99!")
        );
    }

    @Test
    public void testLoginIncorrect() {

        Login user = new Login(
                "Kyle",
                "Smith",
                "kyl_1",
                "Ch&sec0ke99!",
                "+27833869876"
        );

        assertEquals(
                "Username or password incorrect, please try again.",
                user.returnLoginStatus("wrong", "wrong")
        );
    }
}