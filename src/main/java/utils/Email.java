/**
 * @author Maxime Chantemargue
 * @author Charles Matrand
 */

package utils;

public class Email {
    private final String email;

    /**
     * Create a new Email
     *
     * @param email the email
     */
    public Email(String email) {
        // regex use to validate email
        if (!email.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"))
            throw new IllegalArgumentException(String.format("'%s' is an invalid email !", email));
        this.email = email;
    }

    /**
     * Get the email
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return email;
    }
}
