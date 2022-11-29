public class SmtpConfig {
    private final String host;
    private final int port;

    public SmtpConfig(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;

    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }


}
