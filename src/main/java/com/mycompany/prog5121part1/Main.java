
package com.mycompany.prog5121part1;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // QuickChat Menu
        System.out.println("\nWelcome to QuickChat!");

        int numberOfMessages;

        while (true) {
            System.out.print("How many messages would you like to send? ");

            try {
                numberOfMessages =
                        Integer.parseInt(scanner.nextLine().trim());

                if (numberOfMessages < 0) {
                    System.out.println(
                            "Please enter 0 or a positive number.");
                    continue;
                }

                break;

            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input. Please enter a whole number.");
            }
        }

        int messagesCreated = 0;

        while (true) {
            System.out.println("\n========== QUICKCHAT MENU ==========");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":

                    if (messagesCreated >= numberOfMessages) {
                        System.out.println(
                                "You have reached your message limit.");
                        break;
                    }

                    while (messagesCreated < numberOfMessages) {

                        System.out.println("\n--- New Message ---");

                        System.out.print(
                                "Enter recipient cellphone number (+27XXXXXXXXX): ");
                        String recipient = scanner.nextLine().trim();

                        System.out.print(
                                "Enter your message (maximum 250 characters): ");
                        String messageText = scanner.nextLine();

                        Message message = new Message(
                                messagesCreated + 1,
                                recipient,
                                messageText
                        );

                        // Validate message ID
                        if (!message.checkMessageID()) {
                            System.out.println(
                                    "Invalid message ID. Please try again.");
                            continue;
                        }

                        // Validate recipient cellphone number
                        String recipientResult =
                                message.checkRecipientCell();

                        System.out.println(recipientResult);

                        if (!recipientResult.equals(
                                "Cell phone number successfully captured.")) {

                            System.out.println(
                                    "Please try entering the message again.");
                            continue;
                        }

                        // Validate message length
                        String lengthResult =
                                message.checkMessageLength();

                        System.out.println(lengthResult);

                        if (!lengthResult.equals("Message ready to send.")) {
                            System.out.println(
                                    "Please enter a message of 250 characters or fewer.");
                            continue;
                        }

                        // Display message hash
                        System.out.println(
                                "Message Hash: " + message.getMessageHash());

                        // Process the message
                        String actionResult = message.SentMessage(scanner);

                        System.out.println(actionResult);

                        String status = message.getStatus();

                        // Count successfully processed messages
                        boolean processed = false;

                        if ("Sent".equals(status)
                                || "Disregarded".equals(status)) {
                            processed = true;

                        } else if ("Stored".equals(status)
                                && "Message successfully stored."
                                        .equals(actionResult)) {
                            processed = true;
                        }

                        if (!processed) {
                            System.out.println(
                                    "Message was not successfully processed. "
                                    + "Please try again.");
                            continue;
                        }

                        // Display sent message details
                        if ("Sent".equals(status)) {
                            System.out.println(
                                    "Message ID: " + message.getMessageID());
                            System.out.println(
                                    "Recipient: " + message.getRecipient());
                            System.out.println(
                                    "Message: " + message.getMessage());
                        }

                        messagesCreated++;

                        System.out.println(
                                "Messages processed: "
                                + messagesCreated + " of "
                                + numberOfMessages);

                        if (messagesCreated >= numberOfMessages) {
                            System.out.println(
                                    "You have processed all your messages.");
                            break;
                        }

                        System.out.print(
                                "Process another message? (yes/no): ");

                        String answer = scanner.nextLine().trim();

                        if (answer.equalsIgnoreCase("no")) {
                            break;
                        }
                    }

                    break;

                case "2":
                    System.out.println(Message.printMessages());
                    break;

                case "3":
                    storedMessagesMenu(scanner);
                    break;

                case "4":
                    System.out.println(
                            "Total messages sent: "
                            + Message.returnTotalMessages());

                    System.out.println(
                            "Thank you for using QuickChat!");

                    scanner.close();
                    return;

                default:
                    System.out.println(
                            "Invalid option. Please choose 1, 2, 3, or 4.");
            }
        }
    }

    // Part 3 Stored Messages submenu
    public static void storedMessagesMenu(Scanner scanner) {

        while (true) {
            System.out.println("\n======= STORED MESSAGES MENU =======");
            System.out.println("1) Display senders and recipients");
            System.out.println("2) Display longest stored message");
            System.out.println("3) Search message by ID");
            System.out.println("4) Search messages by recipient");
            System.out.println("5) Delete message by hash");
            System.out.println("6) Display full message report");
            System.out.println("7) Return to QuickChat Menu");
            System.out.print("Choose an option: ");

            String option = scanner.nextLine().trim();

            switch (option) {

                case "1":
                    System.out.println(
                            Message.displayStoredSendersAndRecipients());
                    break;

                case "2":
                    System.out.println(
                            Message.displayLongestStoredMessage());
                    break;

                case "3":
                    System.out.print("Enter the message ID to search: ");
                    String id = scanner.nextLine().trim();

                    System.out.println(
                            Message.searchMessageByID(id));
                    break;

                case "4":
                    System.out.print(
                            "Enter recipient cellphone number (+27XXXXXXXXX): ");
                    String recipient = scanner.nextLine().trim();

                    System.out.println(
                            Message.searchMessagesByRecipient(recipient));
                    break;

                case "5":
                    System.out.print("Enter the message hash to delete: ");
                    String hash = scanner.nextLine().trim();

                    System.out.println(
                            Message.deleteMessageByHash(hash));
                    break;

                case "6":
                    System.out.println(
                            Message.displayFullReport());
                    break;

                case "7":
                    return;

                default:
                    System.out.println(
                            "Invalid option. Please choose from 1 to 7.");
            }
        }
    }
}
