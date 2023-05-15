package RMI.Server;

import java.io.IOException;
import java.rmi.AlreadyBoundException;

public class RunServer {

    public static void main(String[] args) throws IOException, AlreadyBoundException {
        RMIServerEnv rmiServerEnv = new RMIServerEnv();
        rmiServerEnv.startServer();
    }

}
