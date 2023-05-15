package RMI.Shared;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServer extends Remote {
    void onClientRegister(String color, RMIClient client) throws RemoteException;
    void onClientMakesMove(Point position) throws RemoteException;
    void onGameIsWon() throws RemoteException;
}
