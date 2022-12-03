package utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Group {
    private final Person sender;
    private final ArrayList<Person> recipients;

    /**
     * Create a new group
     *
     * @param sender     the sender of the message
     * @param recipients the list of recipients
     * @throws IllegalArgumentException if the sender does not contain at least 2 recipients
     */
    public Group(@NotNull Person sender, @NotNull ArrayList<Person> recipients) throws IllegalArgumentException {
        if (recipients.size() < 2)
            throw new IllegalArgumentException("A group must contains at least 2 recipients !");
        this.sender = sender;
        this.recipients = recipients;
    }

    /**
     * Get the sender of the group
     *
     * @return the sender of the group
     */
    public Person getSender() {
        return sender;
    }


    /**
     * Get the list of recipients
     *
     * @return the list of recipients
     */
    public ArrayList<Person> getRecipients() {
        return recipients;
    }

    @Override
    public String toString() {
        return String.format("Group{sender=%s, recipients=%s}", sender, recipients);
    }
}