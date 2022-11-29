import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class MailRobot {
    static final String LF = "\n";
    static final String CRLF = "\r\n";

    public static void main(String[] args) {
        try {
            final Properties config = FileParser.parseConfig(new File("src/main/config/config.properties"));
            final String HOST = config.getProperty("smtpServerAddress");
            final int PORT = Integer.parseInt(config.getProperty("smtpServerPort"));
            final int NUMBER_OF_GROUP = Integer.parseInt(config.getProperty("numberOfGroups"));

            Socket socket = new Socket(HOST, PORT);
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter toServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            String firstAdress = "test.com";
            toServer.printf("EHLO %s%s", firstAdress, CRLF);
            while (fromServer.ready()) {
                String ligne = fromServer.readLine();
                System.out.println(ligne);
            }


            // EHLO DONE NOW SENDING ALL THE EMAILS
            ArrayList<Group> groups = FileParser.parseGroup("src/main/config/victims.json");
            Message[] messages = FileParser.parseMessage("src/main/config/messages.json");
            String mail2Send = formatMail(groups, messages);

            toServer.print(mail2Send);

           /* String str;
            while ((str = fromServer.readLine()) != null) {
                System.out.println(str);
            }

            String stringSender = "bob@bob.com";
            toServer.write(String.format("MAIL FROM:<%s>\r\n", stringSender));

            while (fromServer.ready()) {
                String ligne = fromServer.readLine();
            }

            String stringReceiver = "bob@bob.com";
            toServer.write(String.format("RCPT TO:<%s>\r\n", stringReceiver));

            while (fromServer.ready()) {
                String ligne = fromServer.readLine();
            }
            toServer.write("DATA\r\n");

            while (fromServer.ready()) {
                String ligne = fromServer.readLine();

            }

            toServer.write("THE DATA TO WRITE");

            while (fromServer.ready()) {
                String ligne = fromServer.readLine();
            }
            System.out.println("Message envoyé");
          */
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private static String formatMail(ArrayList<Group> groups, Message[] messages) throws IOException {

        StringBuilder sb = new StringBuilder();

        for (Group groupActuel : groups) { // Or size of groups

            Random random = new Random();
            Message msgToSend = messages[random.nextInt(messages.length)]; // get a random message

            sb.append(String.format("MAIL FROM:<%s>\r\n", groupActuel.getSender().getEmail()));
            for (Person p : groupActuel.getRecipients()) { // add all the recipients
                sb.append(String.format("RCPT TO:<%s>%s", p.getEmail(), CRLF));
            }

            sb.append(String.format("DATA%s", CRLF));
            sb.append(String.format("From: %s%s", groupActuel.getSender().getEmail(), CRLF));

            // -- to add all the recipients
            sb.append("To: ");
            for (Person p : groupActuel.getRecipients()) {
                sb.append(p.getEmail()).append(", ");
            }
            sb.append(CRLF);
            // -- end of to add all the recipients

            sb.append("Content-Type: text/plain; charset=utf-8").append(CRLF); // Set encoding to UTF-8

            sb.append(String.format("Subject: %s\r\n", msgToSend.getSubject()));
            System.out.println(sb.toString());
            sb.append(msgToSend.getBody());
            sb.append(String.format("%s%s%s", CRLF, ".", CRLF));
            System.out.println("Message envoyé");
        }
        return sb.toString();
    }


    private static void sendMail(String sender, String receiver, String message) {

    }


}
