package utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SmtpClient implements Closeable {
    private static final String LF = "\n", CRLF = "\r\n";
    private final String host;
    private final int port;
    private final PrintWriter toServer;
    private final BufferedReader fromServer;
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
        if (port < 0 || port > 65535) throw new IllegalArgumentException("Invalid port number");
        if (host.isEmpty()) throw new IllegalArgumentException("Invalid host");
        this.host = host;
        this.port = port;

        this.socket = new Socket(this.host, this.port);
        this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.toServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        handleServerResponses();
    }

    /**
     * Send a EHLO command to the server
     */
    public ArrayList<SmtpResponse> sayHello() throws IOException {
        toServer.printf("EHLO %s%s", host, CRLF);
        return handleServerResponses();
    }

    /**
     * Close the connection with the server
     */
    public ArrayList<SmtpResponse> quit() throws IOException {
        toServer.printf("QUIT%s", CRLF);
        var rep = handleServerResponses();
        return rep;
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
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("MAIL FROM: <%s>%s", sender.getEmail(), CRLF));
        for (Person p : recipients) {
            sb.append(String.format("RCPT TO:<%s>%s", p.getEmail(), CRLF));
        }

        sb.append(String.format("DATA%s", CRLF)).append("To: ");
        for (Person p : recipients) {
            sb.append(p.getEmail()).append(", ");
        }
        sb.append(CRLF);
        sb.append("Content-Type: text/plain; charset=utf-8").append(CRLF);
        sb.append(String.format("Subject: =?UTF-8?B?%s?=%s%s", message.getBase64Subject(), CRLF, CRLF));
        sb.append(String.format("%s%s", message.getBody().replace(CRLF, LF), CRLF));
        sb.append(String.format("%s.%s", CRLF, CRLF));
        toServer.printf(sb.toString());
        return handleServerResponses();
    }

    private ArrayList<SmtpResponse> handleServerResponses() throws IOException, NumberFormatException {
        ArrayList<SmtpResponse> responses = new ArrayList<>();
        final int smtpResponseLength = 2;
        String line;
        boolean end = false;
        while (!end && (line = fromServer.readLine()) != null) {

            String[] code = line.split("-", smtpResponseLength);
            if (code.length != smtpResponseLength) { // EHLO end and QUIT end
                code = line.split(" ", smtpResponseLength);
                end = true;
            }
            System.out.println(line);
            responses.add(new SmtpResponse(Integer.parseInt(code[0]), code[1]));
        }
        return responses;
    }

    @Override
    public void close() throws IOException {
        toServer.close();
        fromServer.close();
        socket.close();
    }
}
