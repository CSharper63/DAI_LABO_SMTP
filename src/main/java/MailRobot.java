/*
 * @author Maxime Chantemargue
 * @author Charles Matrand
 * This program aims to send a mail to a list of people selected randomly from a parsed JSON formatted file.
 * In most classes or other java file you will see @NotNull annotation. This annotation is used to specify that
 * the current attribute passed as a param must not be null. This annotation is used by the IntelliJ IDEA IDE and
 * come from org.jetbrains.annotations package, making it not portable while the user who try to run this program does not install it.
 * */

import org.jetbrains.annotations.NotNull;
import utils.*;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class MailRobot {
    final private static Random random = new Random();

    public static void main(String[] args) {
        try {
            final Properties config = FileParser.parseConfig("src/main/config/config.properties");
            final String HOST = config.getProperty("smtpServerAddress");
            final int PORT = Integer.parseInt(config.getProperty("smtpServerPort")),
                    NUMBER_OF_GROUP = Integer.parseInt(config.getProperty("numberOfGroups"));


            ArrayList<Person> persons = FileParser.parsePersons("src/main/config/victims.json");
            ArrayList<Message> messages = FileParser.parseMessages("src/main/config/messages.json");

            ArrayList<Group> groups = MailRobot.generateGroups(persons, NUMBER_OF_GROUP);

            Smtp smtp = new Smtp(HOST, PORT);
            smtp.sayHello();

            for (Group g : groups) {
                System.out.println(g);
                smtp.sendEmail(g.getSender(), g.getRecipients(), MailRobot.getRandomMessage(messages));
            }
            smtp.quit(); // all streams will be closed as smtp implements Closable

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Select a message from a list of messages
     *
     * @param messages the list of messages
     * @return a message
     */
    private static Message getRandomMessage(@NotNull ArrayList<Message> messages) {
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
    private static ArrayList<Group> generateGroups(@NotNull ArrayList<Person> persons, int numberOfGroups) throws IllegalArgumentException {
        if (numberOfGroups <= 0)
            throw new IllegalArgumentException("The number of groups must be greater than 0 !");
        if (persons.size() / numberOfGroups < 3) throw new RuntimeException("Not enough persons !");

        ArrayList<ArrayList<Person>> allGroups = new ArrayList<>(numberOfGroups); // represents the n groups
        for (int i = 0; i < numberOfGroups; ++i) allGroups.add(new ArrayList<>()); // init the groups

        for (int i = 0; i < persons.size(); ++i) {
            Person p = persons.get((i + random.nextInt(persons.size())) % persons.size());
            allGroups.get(i % numberOfGroups).add(p); // add the person to the random group
        }
        //generate groups from persons list
        ArrayList<Group> groups = new ArrayList<>(numberOfGroups);
        for (ArrayList<Person> u : allGroups) {
            groups.add(new Group(u.get(0), new ArrayList<>(u.subList(1, u.size()))));
        }
        return groups;
    }
}
