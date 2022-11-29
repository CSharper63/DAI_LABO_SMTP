import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Group {
    private final Person sender;
    private final ArrayList<Person> recipients;

    public Group(@NotNull Person sender, @NotNull ArrayList<Person> recipients) throws IllegalArgumentException {
        if (recipients.size() < 2)
            throw new IllegalArgumentException("A group must contains at least 2 recipients !");
        this.sender = sender;
        this.recipients = recipients;
    }

    public Person getSender() {
        return sender;
    }


    public ArrayList<Person> getRecipients() {
        return recipients;
    }

    @Override
    public String toString() {
        return String.format("Group{sender=%s, recipients=%s}", sender, recipients);
    }
}