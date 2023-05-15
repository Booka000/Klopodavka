package gRPC.Server;


import gRPC.Shared.Empty;
import gRPC.Shared.Move;
import gRPC.Shared.ServicesGrpc;
import io.grpc.stub.StreamObserver;

import java.awt.*;

public class ServicesImpl extends ServicesGrpc.ServicesImplBase {
    private StreamObserver<Move> observer;
    private final gRPClientActionsListener listener;

    public ServicesImpl(gRPClientActionsListener listener) {
        this.listener = listener;
    }


    @Override
    public void connect(Empty request, StreamObserver<Empty> responseObserver) {
        listener.onClientConnect();
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Move> movesStream(StreamObserver<Move> responseObserver) {
        if (observer == null) {
            observer = responseObserver;
        }
        return new StreamObserver<Move>() {
            @Override
            public void onNext(Move move) {
                Point point = new Point(move.getX(),move.getY());
                listener.onClientMakesMove(point);
            }

            @Override
            public void onError(Throwable throwable) {
                listener.onConnectionEnd();
            }

            @Override
            public void onCompleted() {
                listener.onGameIsAWon();
            }
        };
    }

    public void sendMove(Point point){
        Move move = Move.newBuilder().setX(point.x).setY(point.y).build();
        observer.onNext(move);
    }
    public void endGame(){
        observer.onCompleted();
    }
}
