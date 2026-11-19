
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

    // Part 3 arrays (implemented using ArrayLists)
    private static final List<Message> allMessages = new ArrayList<>();
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();

    private static final Path JSON_FILE = Paths.get("messages.json");

    // Load stored messages from JSON when the class starts
    static {
        loadStoredMessages();
    }

    // Constructor with automatically generated ID
    public Message(int messageNumber, String recipient, String message) {
        this(String.format("%010d",
                ThreadLocalRandom.current().nextLong(10_000_000_000L)),
                messageNumber, recipient, message);
    }

    // Constructor with a known ID (used for tests)
    public Message(String messageID, int messageNumber,
                 String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.status = "Pending";
        this.messageHash = createMessageHash();
    }

    // Check message ID
    public boolean checkMessageID() {
        return messageID != null
                && messageID.length() <= 10
                && messageID.matches("\\d+");
    }

    // Check South African cellphone number
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
        if (message == null) {
            return "Please enter a message of less than 250 characters.";
        }

        if (message.length() > 250) {
            return "Message exceeds 250 characters by "
                    + (message.length() - 250)
                    + "; please reduce the size.";
        }

        return "Message ready to send.";
    }

    // Create message hash
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

        return (firstTwoDigits + ":" + messageNumber
                + ":" + firstWord + lastWord).toUpperCase();
    }

    // Original method retained for compatibility
    public String SentMessage() {
        return SentMessage(new Scanner(System.in));
    }

    // Process message using the scanner from Main
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
                addMessageToArrays();
                totalMessages++;
                return "Message successfully sent.";

            case "2":
                status = "Disregarded";
                addMessageToArrays();
                return "Message disregarded.";

            case "3":
                status = "Stored";
                if (storeMessage()) {
                    addMessageToArrays();
                    return "Message successfully stored.";
                }
                return "Message could not be stored.";

            default:
                return "Invalid option. Message not processed.";
        }
    }

    
    // Add processed messages to arrays
    private void addMessageToArrays() {
        allMessages.add(this);

        // Prevent duplicate message hashes and IDs
        if (!messageHashes.contains(messageHash)) {
            messageHashes.add(messageHash);
        }

        if (!messageIDs.contains(messageID)) {
            messageIDs.add(messageID);
        }

        switch (status) {
            case "Sent":
                if (!containsID(sentMessages, messageID)) {
                    sentMessages.add(this);
                }
                break;

            case "Disregarded":
                if (!containsID(disregardedMessages, messageID)) {
                    disregardedMessages.add(this);
                }
                break;

            case "Stored":
                if (!containsID(storedMessages, messageID)) {
                    storedMessages.add(this);
                }
                break;
        }
    }


    // Save stored messages to JSON
    public boolean storeMessage() {
        if (!"Stored".equals(status)) {
            status = "Stored";
        }

        storedMessages.removeIf(m -> m.messageID.equals(messageID));
        storedMessages.add(this);

        return saveStoredMessages();
    }

    // Save all stored messages to JSON file
    private static boolean saveStoredMessages() {
        try {
            StringBuilder json = new StringBuilder("[\n");

            for (int i = 0; i < storedMessages.size(); i++) {
                Message msg = storedMessages.get(i);

                json.append("  {")
                    .append("\"messageID\":\"").append(escapeJson(msg.messageID)).append("\",")
                    .append("\"messageNumber\":").append(msg.messageNumber).append(",")
                    .append("\"recipient\":\"").append(escapeJson(msg.recipient)).append("\",")
                    .append("\"message\":\"").append(escapeJson(msg.message)).append("\",")
                    .append("\"messageHash\":\"").append(escapeJson(msg.messageHash)).append("\",")
                    .append("\"status\":\"").append(escapeJson(msg.status)).append("\"")
                    .append("}");

                if (i < storedMessages.size() - 1) {
                    json.append(",");
                }

                json.append("\n");
            }

            json.append("]");

            Files.writeString(JSON_FILE, json.toString(),
                    StandardCharsets.UTF_8);
            return true;

        } catch (IOException e) {
            System.out.println("Error saving JSON file: " + e.getMessage());
            return false;
        }
    }

    // Load stored messages from JSON file into arrays
    private static void loadStoredMessages() {
        if (!Files.exists(JSON_FILE)) {
            return;
        }

        try {
            String json = Files.readString(JSON_FILE, StandardCharsets.UTF_8);
            List<String> objects = extractJsonObjects(json);

            for (String object : objects) {
                String id = getJsonValue(object, "messageID");
                String number = getJsonValue(object, "messageNumber");
                String recipient = getJsonValue(object, "recipient");
                String text = getJsonValue(object, "message");
                String hash = getJsonValue(object, "messageHash");
                String status = getJsonValue(object, "status");

                if (id != null && number != null && recipient != null
                        && text != null && hash != null && status != null) {

                    Message msg = new Message(id, Integer.parseInt(number),
                            recipient, text);
                    msg.messageHash = hash;
                    msg.status = status;

                    if (!containsID(storedMessages, id)) {
                        storedMessages.add(msg);
                        allMessages.add(msg);
                        messageHashes.add(hash);
                        messageIDs.add(id);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading stored messages: "
                    + e.getMessage());
        }
    }

    // Extract JSON objects from a JSON array
    private static List<String> extractJsonObjects(String json) {
        List<String> objects = new ArrayList<>();
        boolean inString = false;
        boolean escaped = false;
        int depth = 0;
        int start = -1;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    inString = false;
                }
            } else {
                if (c == '"') {
                    inString = true;
                } else if (c == '{') {
                    if (depth == 0) {
                        start = i;
                    }
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0 && start >= 0) {
                        objects.add(json.substring(start, i + 1));
                        start = -1;
                    }
                }
            }
        }

        return objects;
    }

    // Read one value from a JSON object
    private static String getJsonValue(String object, String key) {
        String marker = "\"" + key + "\":";
        int start = object.indexOf(marker);

        if (start == -1) {
            return null;
        }

        start += marker.length();

        while (start < object.length()
                && Character.isWhitespace(object.charAt(start))) {
            start++;
        }

        if (start >= object.length()) {
            return null;
        }

        if (object.charAt(start) == '"') {
            start++;
            StringBuilder value = new StringBuilder();
            boolean escaped = false;

            for (int i = start; i < object.length(); i++) {
                char c = object.charAt(i);

                if (escaped) {
                    switch (c) {
                        case 'n': value.append('\n'); break;
                        case 'r': value.append('\r'); break;
                        case 't': value.append('\t'); break;
                        case '"': value.append('"'); break;
                        case '\\': value.append('\\'); break;
                        default: value.append(c);
                    }
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    return value.toString();
                } else {
                    value.append(c);
                }
            }

            return null;
        }

        int end = start;
        while (end < object.length()
                && object.charAt(end) != ','
                && object.charAt(end) != '}') {
            end++;
        }

        return object.substring(start, end).trim();
    }

    // Check if a list already contains an ID
    private static boolean containsID(List<Message> list, String id) {
        for (Message msg : list) {
            if (msg.messageID.equals(id)) {
                return true;
            }
        }
        return false;
    }

    // Escape characters for JSON
    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Display sent messages
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent.";
        }

        StringBuilder result = new StringBuilder();

        for (Message msg : sentMessages) {
            result.append("\nMessage ID: ").append(msg.messageID)
                    .append("\nMessage Hash: ").append(msg.messageHash)
                    .append("\nRecipient: ").append(msg.recipient)
                    .append("\nMessage: ").append(msg.message)
                    .append("\n");
        }

        return result.toString();
    }

    // Part 3: Display sender and recipient of stored messages
    public static String displayStoredSendersAndRecipients() {
        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }

        StringBuilder result = new StringBuilder();

        for (Message msg : storedMessages) {
            result.append("\nSender: Developer")
                    .append("\nRecipient: ").append(msg.recipient)
                    .append("\n");
        }

        return result.toString();
    }

    // Part 3: Display the longest stored message
    public static String displayLongestStoredMessage() {
        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }

        Message longest = storedMessages.get(0);

        for (Message msg : storedMessages) {
            if (msg.message.length() > longest.message.length()) {
                longest = msg;
            }
        }

        return "Longest stored message:\n"
                + longest.message
                + "\nLength: " + longest.message.length();
    }

    // Part 3: Search by message ID
    public static String searchMessageByID(String id) {
        for (Message msg : allMessages) {
            if (msg.messageID.equals(id)) {
                return "Recipient: " + msg.recipient
                        + "\nMessage: " + msg.message;
            }
        }

        return "Message ID not found.";
    }

    // Part 3: Search messages by recipient
    public static String searchMessagesByRecipient(String recipient) {
        StringBuilder result = new StringBuilder();
        boolean found = false;

        for (Message msg : allMessages) {
            if (msg.recipient.equals(recipient)) {
                result.append("\nStatus: ").append(msg.status)
                        .append("\nMessage: ").append(msg.message)
                        .append("\n");
                found = true;
            }
        }

        if (!found) {
            return "No messages found for this recipient.";
        }

        return result.toString();
    }

    // Part 3: Delete message using its hash
    public static String deleteMessageByHash(String hash) {
        Message found = null;

        for (Message msg : allMessages) {
            if (msg.messageHash.equals(hash)) {
                found = msg;
                break;
            }
        }

        if (found == null) {
            return "Message hash not found.";
        }

        allMessages.remove(found);
        sentMessages.remove(found);
        disregardedMessages.remove(found);
        storedMessages.remove(found);
        messageHashes.remove(found.messageHash);
        messageIDs.remove(found.messageID);

        if ("Sent".equals(found.status) && totalMessages > 0) {
            totalMessages--;
        }

        if (!saveStoredMessages()) {
            return "Message was removed from memory, but JSON file could not be updated.";
        }

        return "Message successfully deleted.";
    }

    // Part 3: Full report of all messages
    public static String displayFullReport() {
        if (allMessages.isEmpty()) {
            return "No messages available for the report.";
        }

        StringBuilder report = new StringBuilder();

        report.append("========== QUICKCHAT MESSAGE REPORT ==========\n");

        for (Message msg : allMessages) {
            report.append("\nMessage ID: ").append(msg.messageID)
                    .append("\nMessage Hash: ").append(msg.messageHash)
                    .append("\nRecipient: ").append(msg.recipient)
                    .append("\nMessage: ").append(msg.message)
                    .append("\nStatus: ").append(msg.status)
                    .append("\n------------------------------------------\n");
        }

        report.append("\nTotal messages in report: ").append(allMessages.size());
        return report.toString();
    }

    // Getters used by Main and tests
    public static int returnTotalMessages() {
        return totalMessages;
    }

    public static List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public static List<Message> getDisregardedMessages() {
        return new ArrayList<>(disregardedMessages);
    }

    public static List<Message> getStoredMessages() {
        return new ArrayList<>(storedMessages);
    }

    public static List<Message> getAllMessages() {
        return new ArrayList<>(allMessages);
    }

    public static List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public static List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
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
