/**
 * @author Maxime Chantemargue
 * @author Charles Matrand
 */

package utils;

import org.jetbrains.annotations.NotNull;

public class Person {
    private final Email email; // final as it is immutable in this case

    /**
     * Create a new Person
     *
     * @param email the email of the person
     */
    public Person(@NotNull Email email) {
        this.email = email;
    }

    /**
     * Get the email of the person
     *
     * @return the email of the person
     */
    public String getEmail() {
        return email.getEmail();
    }

    @Override
    public String toString() {
        return String.format("Person{email=%s}", email);
    }
}
