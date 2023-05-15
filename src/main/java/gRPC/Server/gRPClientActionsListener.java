package gRPC.Server;

import java.awt.*;

public interface gRPClientActionsListener {

     void onClientConnect();
     void onConnectionEnd();
     void onClientMakesMove(Point position);
     void onGameIsAWon();
}
