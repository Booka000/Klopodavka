package gRPC.Server;

import java.io.IOException;

public class RunGrpcServer {


    public static void main(String[] args) throws IOException, InterruptedException {
        RpcServerEnv env = new RpcServerEnv();
        env.startServer();
    }
}
