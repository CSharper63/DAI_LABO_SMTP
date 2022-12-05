/**
 * @author Maxime Chantemargue
 * @author Charles Matrand
 */

package utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;


public class SmtpClient implements Closeable {
    private static final String CRLF = "\r\n";

    private final ArrayList<SmtpResponse> SmtpResponses = new ArrayList<SmtpResponse>(
            Arrays.asList(
                    new SmtpResponse(250, "Ok"),
                    new SmtpResponse(354, "End data with <CR><LF>.<CR><LF>"),
                    new SmtpResponse(221, "Bye"),
                    new SmtpResponse(220, "Your SMTP server is ready")
            ));
    private final String host;
    private final int port;
    private final PrintWriter toServer; // buffer to write content sent to the server
    private final BufferedReader fromServer; // buffer to read content sent by the server
    private final Socket socket;

    /**
     * Create a new SMTP client
     *
     * @param host the host of the SMTP server
     * @param port the port of the SMTP server
     * @throws IllegalArgumentException if the port is not in the range [0, 65535]
     * @throws IOException              if the connection to the server failed
     */
    public SmtpClient(@NotNull String host, int port) throws IllegalArgumentException, IOException {
        final int MIN_PORT = 0, MAX_PORT = 65535;
        if (port < MIN_PORT || port > MAX_PORT) throw new IllegalArgumentException("Invalid port number");
        if (host.isEmpty()) throw new IllegalArgumentException("Invalid host");
        this.host = host;
        this.port = port;

        this.socket = new Socket(this.host, this.port);
        this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.toServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        var lastResponse = handleServerResponses();
        if (lastResponse.get(lastResponse.size() - 1).getCode() != 220)
            throw new IOException("Connection to the server failed");
    }


    /**
     * Send a EHLO command to the server
     *
     * @return the list of responses from the server
     * @throws IOException if the connection to the server failed
     */
    public ArrayList<SmtpResponse> sayHello() throws IOException {
        toServer.printf("EHLO %s%s", host, CRLF);
        return handleServerResponses();
    }

    /**
     * Send a QUIT command to the server
     *
     * @return the list of responses from the server
     * @throws IOException if the close connection to the server failed
     */
    public ArrayList<SmtpResponse> quit() throws IOException {
        toServer.printf("QUIT%s", CRLF);
        return handleServerResponses();
    }

    /**
     * Send a message to the server
     *
     * @param sender     the sender of the message
     * @param recipients the list of recipients
     * @param message    the message to send
     * @throws IOException if the message cannot be sent
     */
    public ArrayList<SmtpResponse> sendEmail(@NotNull Person sender, @NotNull ArrayList<Person> recipients, @NotNull Message message) throws IOException {

        toServer.printf(String.format("MAIL FROM:<%s>%s", sender.getEmail(), CRLF));
        readSpecificResponse(SmtpResponses.stream().filter(r -> r.getCode() == 250).findFirst().get());

        for (Person p : recipients) {
            toServer.printf(String.format("RCPT TO:<%s>%s", p.getEmail(), CRLF));
            readSpecificResponse(SmtpResponses.stream().filter(r -> r.getCode() == 250).findFirst().get());
        }

        toServer.printf(String.format("DATA%s", CRLF));
        readSpecificResponse(SmtpResponses.stream().filter(r -> r.getCode() == 354).findFirst().get());

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("From: %s%s", sender.getEmail(), CRLF));
        sb.append("To: ");
        for (int i = 0; i < recipients.size(); ++i) {
            if (i != 0) sb.append(", ");
            sb.append(recipients.get(i).getEmail());
        }
        sb.append(CRLF);
        sb.append("Content-Type: text/plain; charset=utf-8").append(CRLF);
        sb.append(String.format("Subject: =?UTF-8?B?%s?=%s%s", message.getBase64Subject(), CRLF, CRLF));
        sb.append(String.format("%s%s.%s", message.getBody(), CRLF, CRLF));
        toServer.printf(sb.toString());

        return handleServerResponses();
    }

    /**
     * Read the response from the server
     *
     * @return the list of responses from the server
     * @throws IOException           if the connection to the server failed
     * @throws NumberFormatException if the response code is not a number
     */
    private ArrayList<SmtpResponse> handleServerResponses() throws IOException, NumberFormatException {
        ArrayList<SmtpResponse> responses = new ArrayList<>();
        final int smtpResponseLength = 2;
        String line;
        boolean end = false;
        while (!end) {
            line = fromServer.readLine();
            String[] code = line.split("-", smtpResponseLength);
            if (code.length != smtpResponseLength) { // EHLO end and QUIT end
                code = line.split(" ", smtpResponseLength);
                end = true;
            }
            responses.add(new SmtpResponse(Integer.parseInt(code[0]), code[1]));
        }
        return responses;
    }

    /**
     * Read a specific response from the server
     *
     * @param sr the response expected
     * @throws IOException if the response is not the expected one
     */
    private void readSpecificResponse(@NotNull SmtpResponse sr) throws IOException {
        String expected = String.format("%s %s", sr.getCode(), sr.getMessage());
        String line = fromServer.readLine();
        if (line == null || !line.equals(expected)) {
            throw new IOException(String.format("Expected: %s, received: %s", expected, line));
        }
    }

    @Override
    public void close() throws IOException {
        toServer.close();
        fromServer.close();
        socket.close();
    }

}
