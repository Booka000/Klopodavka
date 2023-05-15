package gRPC.Server;

import gRPC.Shared.ActionsListener;
import gRPC.Shared.Environment;
import io.grpc.ServerBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RpcServerEnv extends Environment implements gRPClientActionsListener, ActionsListener {

    private final io.grpc.Server server;
    private final ServicesImpl services;

    public RpcServerEnv() throws IOException {
        services = new ServicesImpl(this);
        server = ServerBuilder.forPort(8080).addService(services).build();
    }

    public void startServer() throws IOException, InterruptedException {
        server.start();
        System.out.println("Server started, waiting for your opponent to connect......");
        server.awaitTermination();
    }

    @Override
    public void onClientConnect() {
        initialize(this);
        grid.setColor("blue");
        isItMyTurn = false;
        changeTurnLabel();
        grid.changeTurn(isItMyTurn);
        setVisible(true);
    }

    @Override
    public void onConnectionEnd() {
        throw new RuntimeException("Connection Failed");
    }

    @Override
    public void onClientMakesMove(Point position) {
        plotEnemyMove(position);
    }

    @Override
    public void onGameIsAWon() {
        JOptionPane.showMessageDialog(this, "Victory");
        server.shutdown();
        System.exit(0);
    }

    @Override
    public void handleMove(Point position) {
        services.sendMove(position);
    }

    @Override
    public void onGameIsLost() {
        services.endGame();
        server.shutdown();
        JOptionPane.showMessageDialog(this, "Defeat");
        System.exit(0);
    }
}
