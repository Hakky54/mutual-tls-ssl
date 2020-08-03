package nl.altindag.server.model;

public class ApplicationProperty {

    private String serverPort;
    private boolean sslEnabled;
    private boolean sslClientAuth;
    private String keystorePath;
    private char[] keystorePassword;
    private String truststorePath;
    private char[] truststorePassword;

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public void setSslClientAuth(boolean sslClientAuth) {
        this.sslClientAuth = sslClientAuth;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public void setKeystorePassword(char[] keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public void setTruststorePath(String truststorePath) {
        this.truststorePath = truststorePath;
    }

    public void setTruststorePassword(char[] truststorePassword) {
        this.truststorePassword = truststorePassword;
    }

    public String getServerPort() {
        return serverPort;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public boolean isSslClientAuth() {
        return sslClientAuth;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public char[] getKeystorePassword() {
        return keystorePassword;
    }

    public String getTruststorePath() {
        return truststorePath;
    }

    public char[] getTruststorePassword() {
        return truststorePassword;
    }
}
