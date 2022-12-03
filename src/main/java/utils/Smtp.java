package utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Smtp implements Closeable {
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
    public Smtp(@NotNull String host, int port) throws IllegalArgumentException, IOException {
        if (port < 0 || port > 65565) throw new IllegalArgumentException("Invalid port number");
        if (host.isEmpty()) throw new IllegalArgumentException("Invalid host");
        this.host = host;
        this.port = port;

        this.socket = new Socket(this.host, this.port);
        this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.toServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    /**
     * Send a EHLO command to the server
     */
    public void sayHello() {
        toServer.printf("EHLO %s%s", host, CRLF);
    }

    /**
     * Close the connection with the server
     */
    public void quit() {
        toServer.printf("QUIT%s", CRLF);
    }

    /**
     * Send a message to the server
     *
     * @param sender     the sender of the message
     * @param recipients the list of recipients
     * @param message    the message to send
     * @throws IOException if the message cannot be sent TODO VERIFY
     */
    public void sendEmail(@NotNull Person sender, @NotNull ArrayList<Person> recipients, @NotNull Message message) throws IOException {
        toServer.write(String.format("MAIL FROM: <%s>%s", sender.getEmail(), CRLF));
        StringBuilder sb = new StringBuilder();
        for (Person p : recipients) {
            sb.append(String.format("RCPT TO:<%s>%s", p.getEmail(), CRLF));
        }
        toServer.write(sb.toString());
        toServer.printf("DATA%s", CRLF);
        sb = new StringBuilder();
        sb.append("To: ");
        for (Person p : recipients) {
            sb.append(p.getEmail()).append(", ");
        }
        sb.append(CRLF);
        sb.append("Content-Type: text/plain; charset=utf-8").append(CRLF);
        sb.append(String.format("Subject: %s%s", message.subject(), CRLF));
        toServer.printf(sb.toString());
        toServer.printf("%s%s", message.body(), CRLF);
        toServer.printf(".%s", CRLF);
    }

    @Override
    public void close() throws IOException {
        toServer.close();
        fromServer.close();
        socket.close();
    }
}
