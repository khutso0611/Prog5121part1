package com.mycompany.prog5121part1;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=================================");
        System.out.println("     REGISTRATION AND LOGIN");
        System.out.println("=================================");

        // Get first name
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        // Get last name
        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        // Get username
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        
        // Get password
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // Get cellphone number
        System.out.print("Enter your South African cell phone number: ");
        String cellPhoneNumber = scanner.nextLine();

        // Create Login object
        Login user = new Login(
                firstName,
                lastName,
                username,
                password,
                cellPhoneNumber
        );

        // Display registration result
        System.out.println();
        System.out.println(user.registerUser());

        // Only allow login if registration was successful
        if (user.checkUserName()
                && user.checkPasswordComplexity()
                && user.checkCellPhoneNumber()) {

            System.out.println();
            System.out.println("Registration successful!");

            System.out.println();
            System.out.println("=================================");
            System.out.println("             LOGIN");
            System.out.println("=================================");

            // Get login username
            System.out.print("Enter your username: ");
            String loginUsername = scanner.nextLine();

            // Get login password
            System.out.print("Enter your password: ");
            String loginPassword = scanner.nextLine();

            // Display login result
            System.out.println();
            System.out.println(
                    user.returnLoginStatus(
                            loginUsername,
                            loginPassword
                    )
            );
        }

        scanner.close();
    }
}