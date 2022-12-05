/**
 * @author Maxime Chantemargue
 * @author Charles Matrand
 * This program aims to send a mail to a list of people selected randomly from a parsed JSON formatted file.
 * In most classes or other java file you will see @NotNull annotation. This annotation is used to specify that
 * the current attribute passed as a param must not be null. This annotation is used by the IntelliJ IDEA IDE and
 * come from org.jetbrains.annotations package, making it not portable while the user who try to run this program does not install it.
 */

import org.jetbrains.annotations.NotNull;
import utils.*;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class MailRobot {
    final private static Random random = new Random(); //use for random selection of people and messages

    public static void main(String @NotNull [] args) {
        if (args.length != 3) {
            System.err.println(
                    "Usage: MailRobot <config.properties> <victims.json> <messages.json>");
            System.exit(1);
        }
        // null by default, in case of error, the program will stop
        Properties config = null;
        ArrayList<Person> victims = null;
        ArrayList<Message> messages = null;
        try {
            config = FileParser.parseConfig(args[0]);
            victims = FileParser.parsePersons(args[1]);
            messages = FileParser.parseMessages(args[2]);
        } catch (Exception e) {
            System.err.println("Error while parsing files");
            System.exit(1);
        }
        final String HOST = config.getProperty("smtpServerAddress");
        final int PORT = Integer.parseInt(config.getProperty("smtpServerPort")),
                NUMBER_OF_GROUP = Integer.parseInt(config.getProperty("numberOfGroups"));
        try (SmtpClient smtpClient = new SmtpClient(HOST, PORT)) {
            ArrayList<SmtpResponse> rep = smtpClient.sayHello();
            System.out.println(rep);
            if (rep.get(rep.size() - 1).getCode() != 250) {
                throw new RuntimeException("Error while connecting to the SMTP server");
            } // EHLO to server

            for (Group g : MailRobot.generateGroups(victims, NUMBER_OF_GROUP)) { // send email to each group
                System.out.println(smtpClient.sendEmail(g.getSender(), g.getRecipients(), MailRobot.getRandomMessage(messages)));
            }
            rep = smtpClient.quit();
            System.out.println(rep);
            if (rep.get(rep.size() - 1).getCode() != 221) {
                throw new RuntimeException("Error while quitting to the SMTP server");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while sending emails:", e);
        }
    }

    /**
     * Select a message from a list of messages
     *
     * @param messages the list of messages
     * @return a message
     */
    static Message getRandomMessage(@NotNull ArrayList<Message> messages) {
        // info: this function has been set to package access to allow it to be used in test unit. It should be private.
        return messages.get((random.nextInt(messages.size())) % messages.size());
    }

    /**
     * Generate a list of groups from a list of persons
     *
     * @param persons        the list of persons
     * @param numberOfGroups the number of groups to generate
     * @return a list of groups
     * @throws IllegalArgumentException if the number of groups is greater than the number of persons
     */
    static @NotNull ArrayList<Group> generateGroups(@NotNull ArrayList<Person> persons, int numberOfGroups) throws IllegalArgumentException {
        // info: this function has been set to package access to allow it to be used in test unit. It should be private.

        if (numberOfGroups <= 0) {
            throw new IllegalArgumentException("The number of groups must be greater than 0 !");
        }
        if (persons.size() / numberOfGroups < 3) {
            throw new RuntimeException("Not enough persons !");
        }

        ArrayList<ArrayList<Person>> allGroups = new ArrayList<>(numberOfGroups); // represents the n groups
        for (int i = 0; i < numberOfGroups; ++i) {
            allGroups.add(new ArrayList<>());
        } // init the groups

        for (int i = 0; i < persons.size(); ++i) {
            Person p = persons.get((random.nextInt(persons.size())) % persons.size());
            allGroups.get(i % numberOfGroups).add(p); // add the random person to the groups
        }
        // generate groups from persons list
        ArrayList<Group> groups = new ArrayList<>(numberOfGroups);
        for (ArrayList<Person> u : allGroups) {
            groups.add(new Group(u.get(0), new ArrayList<>(u.subList(1, u.size())))); //sender is always the first person
        }
        return groups;
    }
}
