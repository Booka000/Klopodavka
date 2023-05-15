package RMI.Server;

import RMI.Shared.RMIClient;
import RMI.Shared.ActionsListener;
import RMI.Shared.Environment;
import RMI.Shared.RMIServer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerEnv extends Environment implements RMIServer, ActionsListener {

    private RMIClient rmiClient;

    public RMIServerEnv() throws IOException {
        UnicastRemoteObject.exportObject(this,0);
    }

    public void startServer() throws RemoteException, AlreadyBoundException {
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("RMI/Server",this);
        System.out.println("Server started, waiting for your opponent to connect......");
    }

    @Override
    public void onClientRegister(String color, RMIClient client) {
        this.rmiClient=client;
        initialize(this);
        grid.setColor(color);
        isItMyTurn = false;
        changeTurnLabel();
        grid.changeTurn(isItMyTurn);
        setVisible(true);
    }

    @Override
    public void onClientMakesMove(Point position){
        plotEnemyMove(position);
    }

    @Override
    public void onGameIsWon(){
        JOptionPane.showMessageDialog(this, "Victory");
        System.exit(0);
    }

    @Override
    public void handleMove(Point position) {
        try {
            this.rmiClient.OnServerMakesMove(position);
        } catch (RemoteException e) {
            throw new RuntimeException("Couldn't connect client");
        }
    }

    @Override
    public void onGameIsLost() {
        JOptionPane.showMessageDialog(this, "Defeat");
        try {
            this.rmiClient.onGameIsAWin();
        } catch (RemoteException e) {
            throw new RuntimeException("Couldn't connect client");
        }
        System.exit(0);
    }
}
