
package com.mycompany.prog5121part1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Message {

    private String messageID;
    private int messageNumber;
    private String recipient;
    private String message;
    private String messageHash;
    private String status;

    private static int totalMessages = 0;
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final Path JSON_FILE = Paths.get("messages.json");

    // Constructor for automatically generated message IDs
    public Message(int messageNumber, String recipient, String message) {
        this(String.format("%010d",
                ThreadLocalRandom.current().nextLong(10_000_000_000L)),
                messageNumber, recipient, message);
    }

    // Constructor for testing with a known message ID
    public Message(String messageID, int messageNumber,
                   String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.status = "Pending";
        this.messageHash = createMessageHash();
    }

    // Check that the message ID contains only digits
    // and is no longer than 10 characters
    public boolean checkMessageID() {
        return messageID != null
                && messageID.length() <= 10
                && messageID.matches("\\d+");
    }

    // Validate a South African cellphone number
    // Expected format: +27 followed by 9 digits
    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("\\+27\\d{9}")) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or "
                + "does not contain an international code. "
                + "Please correct the number and try again.";
    }

    // Check message length
    public String checkMessageLength() {
        if (message == null || message.length() > 250) {
            if (message == null) {
                return "Please enter a message of less than 250 characters.";
            }

            return "Message exceeds 250 characters by "
                    + (message.length() - 250)
                    + "; please reduce the size.";
        }

        return "Message ready to send.";
    }

    // Create a message hash from the first two ID digits,
    // message number, first word and last word.
    public String createMessageHash() {
        String firstTwoDigits = "";

        if (messageID != null) {
            firstTwoDigits = messageID.substring(
                    0, Math.min(2, messageID.length()));
        }

        String firstWord = "";
        String lastWord = "";

        if (message != null && !message.trim().isEmpty()) {
            String[] words = message.trim().split("\\s+");

            firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
            lastWord = words[words.length - 1]
                    .replaceAll("[^a-zA-Z0-9]", "");
        }

        return (firstTwoDigits + ":" + messageNumber + ":"
                + firstWord + lastWord).toUpperCase();
    }

    // Original method retained for compatibility
    public String SentMessage() {
        return SentMessage(new Scanner(System.in));
    }

    // Allows Main.java to reuse its Scanner
    public String SentMessage(Scanner scanner) {
        System.out.println("\nChoose what to do with the message:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        System.out.print("Enter your choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                status = "Sent";
                sentMessages.add(this);
                totalMessages++;
                return "Message successfully sent.";

            case "2":
                status = "Disregarded";
                return "Press 0 to delete the message.";

            case "3":
                status = "Stored";

                if (storeMessage()) {
                    return "Message successfully stored.";
                }

                return "Message could not be stored.";

            default:
                return "Invalid option. Message not sent.";
        }
    }

    // Store messages in a JSON array
    public boolean storeMessage() {
        try {
            String existing = "";

            if (Files.exists(JSON_FILE)) {
                existing = new String(
                        Files.readAllBytes(JSON_FILE),
                        StandardCharsets.UTF_8).trim();
            }

            String object = "{"
                    + "\"messageID\":\"" + escapeJson(messageID) + "\","
                    + "\"messageNumber\":" + messageNumber + ","
                    + "\"recipient\":\"" + escapeJson(recipient) + "\","
                    + "\"message\":\"" + escapeJson(message) + "\","
                    + "\"messageHash\":\"" + escapeJson(messageHash) + "\","
                    + "\"status\":\"" + escapeJson(status) + "\""
                    + "}";

            String updated;

            if (existing.isEmpty() || existing.equals("[]")) {
                updated = "[\n" + object + "\n]";
            } else if (existing.startsWith("[")
                    && existing.endsWith("]")) {

                String contents = existing.substring(
                        1, existing.length() - 1).trim();

                updated = "[\n" + contents
                        + (contents.isEmpty() ? "" : ",\n")
                        + object + "\n]";
            } else {
                System.out.println(
                        "Storage error: messages.json is not a JSON array.");
                return false;
            }

            Files.write(JSON_FILE,
                    updated.getBytes(StandardCharsets.UTF_8));

            return true;

        } catch (IOException e) {
            System.out.println(
                    "Error storing message: " + e.getMessage());
            return false;
        }
    }

    // Escape special characters for JSON strings
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Display messages successfully sent during this run
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent.";
        }

        StringBuilder result = new StringBuilder();

        for (Message msg : sentMessages) {
            result.append("\nMessage ID: ").append(msg.messageID);
            result.append("\nMessage Hash: ").append(msg.messageHash);
            result.append("\nRecipient: ").append(msg.recipient);
            result.append("\nMessage: ").append(msg.message);
            result.append("\n");
        }

        return result.toString();
    }

    // Return the number of messages successfully sent
    public static int returnTotalMessages() {
        return totalMessages;
    }

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getStatus() {
        return status;
    }
}

