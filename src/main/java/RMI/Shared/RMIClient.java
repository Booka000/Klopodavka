package RMI.Shared;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClient extends Remote {
    void OnServerMakesMove(Point position) throws RemoteException;
    void onGameIsAWin() throws RemoteException;
}
