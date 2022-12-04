package utils;

import org.jetbrains.annotations.NotNull;

public class SmtpResponse {
    private final int code;
    private final String message;

    /**
     * Create a new SmtpResponse
     *
     * @param code    the code of the response
     * @param message the message of the response
     */
    public SmtpResponse(int code, @NotNull String message) throws IllegalArgumentException {
        if (code < 0)
            throw new IllegalArgumentException("Invalid code !");
        this.code = code;
        this.message = message;
    }

    /**
     * Get the code of the response
     *
     * @return the code of the response
     */
    public int getCode() {
        return code;
    }

    /**
     * Get the message of the response
     *
     * @return the message of the response
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("SmtpResponse{code=%d, message='%s'}", code, message);
    }
}

