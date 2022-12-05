import com.google.gson.JsonParseException;
import org.junit.Before;
import org.junit.Test;
import utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class MailRobotTest {
    Properties config = null;
    ArrayList<Person> victims = null;
    ArrayList<Message> messages = null;
    SmtpClient smtpClient = null;

    String HOST;
    int PORT, NUMBER_OF_GROUP;

    @Before
    public void setup() throws Exception {
        // these tests have been called here to ensure the test order and avoid null exception
        parsingFiles();
        ehloSmtp();
    }

    @Test
    public void parsingFiles() throws IOException, JsonParseException {
        config = FileParser.parseConfig("src/main/config/config.properties");
        victims = FileParser.parsePersons("src/main/config/victims.json");
        messages = FileParser.parseMessages("src/main/config/messages.json");
        HOST = config.getProperty("smtpServerAddress");
        PORT = Integer.parseInt(config.getProperty("smtpServerPort"));
        NUMBER_OF_GROUP = Integer.parseInt(config.getProperty("numberOfGroups"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidEmailNotAllowed() {
        Email e = new Email("maxime.chantemargueheig-vs.ch");
    }

    @Test
    public void ehloSmtp() throws IOException {
        smtpClient = new SmtpClient(config.getProperty("smtpServerAddress"), Integer.parseInt(config.getProperty("smtpServerPort")));
        var resps = smtpClient.sayHello();
        assertEquals(250, resps.get(resps.size() - 1).getCode());
    }


    @Test
    public void sendEmail() throws IOException {
        for (Group g : MailRobot.generateGroups(victims, NUMBER_OF_GROUP)) { // send email to each group
            smtpClient.sendEmail(g.getSender(), g.getRecipients(), MailRobot.getRandomMessage(messages));
        }
    }

    @Test
    public void byeByeSmtp() throws IOException {
        var resps = smtpClient.quit();
        assertEquals(221, resps.get(resps.size() - 1).getCode());
    }
}
