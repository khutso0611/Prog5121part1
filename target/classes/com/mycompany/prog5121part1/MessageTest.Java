
package com.mycompany.prog5121part1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    public void testCheckMessageID() {
        Message message = new Message(
                "0012345678", 0, "+27718693002", "Hi tonight"
        );

        assertTrue(message.checkMessageID());
    }

    @Test
    public void testCheckRecipientCellCorrect() {
        Message message = new Message(
                "0012345678", 0, "+27718693002", "Hi tonight"
        );

        assertEquals(
                "Cell phone number successfully captured.",
                message.checkRecipientCell()
        );
    }

    @Test
    public void testCheckRecipientCellIncorrect() {
        Message message = new Message(
                "0012345678", 0, "08575975889", "Hi tonight"
        );

        assertEquals(
                "Cell phone number is incorrectly formatted or "
                + "does not contain an international code. "
                + "Please correct the number and try again.",
                message.checkRecipientCell()
        );
    }

    @Test
    public void testMessageLengthCorrect() {
        Message message = new Message(
                "0012345678", 0, "+27718693002", "Hi Mike"
        );

        assertEquals(
                "Message ready to send.",
                message.checkMessageLength()
        );
    }

    @Test
    public void testMessageLengthTooLong() {
        String longMessage = repeatCharacter('A', 260);

        Message message = new Message(
                "0012345678", 0, "+27718693002", longMessage
        );

        assertEquals(
                "Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength()
        );
    }

    @Test
    public void testCreateMessageHash() {
        Message message = new Message(
                "0012345678",
                0,
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?"
        );

        assertEquals(
                "00:0:HITONIGHT",
                message.createMessageHash()
        );
    }

    @Test
    public void testMessageHashChangesWithMessageNumber() {
        Message firstMessage = new Message(
                "0012345678", 0, "+27718693002", "Hi tonight"
        );

        Message secondMessage = new Message(
                "0012345678", 1, "+27718693002", "Hi tonight"
        );

        assertNotEquals(
                firstMessage.createMessageHash(),
                secondMessage.createMessageHash()
        );
    }

    @Test
    public void testReturnTotalMessagesInitially() {
        assertTrue(Message.returnTotalMessages() >= 0);
    }

    // Helper method compatible with older Java versions
    private String repeatCharacter(char character, int count) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < count; i++) {
            result.append(character);
        }

        return result.toString();
    }
}
