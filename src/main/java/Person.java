import org.jetbrains.annotations.NotNull;

public class Person {
    private final Email email; //final as it is immutable in this case

    Person(@NotNull Email email) {
        this.email = email;
    }

    public String getEmail() {
        return email.getEmail();
    }

    @Override
    public String toString() {
        return String.format("Person{email=%s}", email);
    }
}
