package gRPC.Client;

import gRPC.Shared.*;
import gRPC.Shared.Empty;
import gRPC.Shared.Move;
import gRPC.Shared.ServicesGrpc;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RpcClientEnv extends Environment implements ActionsListener {

    private final ServicesGrpc.ServicesBlockingStub servicesBlockingStub;
    private final ServicesGrpc.ServicesStub servicesStub;
    private StreamObserver<Move> streamObserver;


    public RpcClientEnv(ManagedChannel channel) throws IOException {
        servicesBlockingStub = ServicesGrpc.newBlockingStub(channel);
        servicesStub = ServicesGrpc.newStub(channel);
    }

    public void startClient(){
        initialize(this);
        grid.setColor("red");
        isItMyTurn=true;
        changeTurnLabel();
        grid.changeTurn(isItMyTurn);
        setVisible(true);
        Empty empty = servicesBlockingStub.connect(Empty.newBuilder().build());
        StreamObserver<Move> responses = new StreamObserver<>() {
            @Override
            public void onNext(Move move) {
                Point position = new Point(move.getX(), move.getY());
                plotEnemyMove(position);
            }

            @Override
            public void onError(Throwable throwable) {
                throw new RuntimeException("Connection failed");
            }

            @Override
            public void onCompleted() {
                JOptionPane.showMessageDialog(RpcClientEnv.this, "Victory");
                System.exit(0);
            }
        };
        streamObserver = servicesStub.movesStream(responses);
    }

    @Override
    public void handleMove(Point position) {
        Move move = Move.newBuilder().setX(position.x).setY(position.y).build();
        streamObserver.onNext(move);
    }

    @Override
    public void onGameIsLost() {
        streamObserver.onCompleted();
        JOptionPane.showMessageDialog(RpcClientEnv.this, "Defeat");
        System.exit(0);
    }
}
