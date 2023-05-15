package gRPC.Client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;

public class RunClient {
    public static void main(String[] args) throws IOException {
        String target = "localhost:8080";
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();
        try {
            RpcClientEnv clientEnv = new RpcClientEnv(channel);
            clientEnv.startClient();
        } finally {
            channel.shutdown();
        }
    }
}
