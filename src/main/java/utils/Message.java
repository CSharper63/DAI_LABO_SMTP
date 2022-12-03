package utils;

import org.jetbrains.annotations.NotNull;

public record Message(@NotNull String subject, @NotNull String body) {
    
    /**
     * Get the subject of the message
     *
     * @return the subject of the message
     */
    @Override
    public String subject() {
        return subject;
    }

    /**
     * Get the body of the message
     *
     * @return the body of the message
     */
    @Override
    public String body() {
        return body;
    }

    @Override
    public String toString() {
        return String.format("Message{subject='%s', body='%s'}", subject, body);
    }
}