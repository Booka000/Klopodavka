package RMI.Client;

import RMI.Shared.ActionsListener;
import RMI.Shared.Environment;
import RMI.Shared.RMIClient;
import RMI.Shared.RMIServer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIClientEnv extends Environment implements ActionsListener, RMIClient {

    private RMIServer server;

    public RMIClientEnv() throws IOException {
        UnicastRemoteObject.exportObject(this,0);
    }

    public void startClient() {
        String color = "red";
        try {
            Registry registry = LocateRegistry.getRegistry("localhost",1099);
            server = (RMIServer) registry.lookup("RMI/Server");
            server.onClientRegister("blue",this);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException("Couldn't connect server...");
        }
        initialize(this);
        grid.setColor(color);
        isItMyTurn=true;
        changeTurnLabel();
        grid.changeTurn(isItMyTurn);
        setVisible(true);
    }

    @Override
    public void handleMove(Point position) {
        try {
            server.onClientMakesMove(position);
        } catch (RemoteException e) {
            throw new RuntimeException("Couldn't connect server...");
        }
    }

    @Override
    public void onGameIsLost() {
        JOptionPane.showMessageDialog(this, "Defeat");
        try {
            server.onGameIsWon();
        } catch (RemoteException e) {
            throw new RuntimeException("Couldn't connect server...");
        }
        System.exit(0);
    }

    @Override
    public void OnServerMakesMove(Point position) {
        plotEnemyMove(position);
    }

    @Override
    public void onGameIsAWin() {
        JOptionPane.showMessageDialog(this, "Victory");
        System.exit(0);
    }
}
