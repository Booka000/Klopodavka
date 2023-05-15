package RMI.Client;

import java.io.IOException;

public class RunClient {
    public static void main(String[] args) throws IOException {
        RMIClientEnv rmiClientEnv = new RMIClientEnv();
        rmiClientEnv.startClient();
    }

}
