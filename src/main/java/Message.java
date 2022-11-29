import org.jetbrains.annotations.NotNull;

public class Message {
    private final String subject, body;


    public Message(@NotNull String subject, @NotNull String body) {
        this.subject = subject;
        this.body = body;
    }
    
    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return String.format("Message{subject='%s', body='%s'}", subject, body);
    }
}