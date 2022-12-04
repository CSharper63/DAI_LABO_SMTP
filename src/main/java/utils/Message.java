package utils;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Message {
    private final String subject, body;

    /**
     * Create a new Message
     *
     * @param subject the subject of the message
     * @param body    the body of the message
     */
    public Message(@NotNull String subject, @NotNull String body) {
        this.subject = subject;
        this.body = body;
    }

    /**
     * Get the subject of the message
     *
     * @return the subject of the message
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Get the body of the message
     *
     * @return the body of the message
     */
    public String getBody() {
        return body;
    }

    public String getBase64Subject() {
        return new String(Base64.getEncoder().encode(subject.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public String toString() {
        return String.format("Message{subject='%s', body='%s'}", subject, body);
    }
}