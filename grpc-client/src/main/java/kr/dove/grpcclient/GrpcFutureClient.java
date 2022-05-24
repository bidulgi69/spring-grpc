package kr.dove.grpcclient;

import com.google.common.util.concurrent.ListenableFuture;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.examples.lib.HelloReply;
import net.devh.boot.grpc.examples.lib.HelloRequest;
import net.devh.boot.grpc.examples.lib.SimpleGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//  Unary communication is supported only.
@Service
public class GrpcFutureClient {

    private final Logger logger = LoggerFactory.getLogger("GrpcFutureClient");
    @GrpcClient("local-grpc-server")
    private SimpleGrpc.SimpleFutureStub futureStub;

    public void unary(final String name) {
        ListenableFuture<HelloReply> future = futureStub.sayHello(
                HelloRequest
                        .newBuilder()
                        .setName(name)
                        .build()
        );
        try {
            final HelloReply response = future.get(2, TimeUnit.SECONDS);
            logger.info("[Future, unary] Received from the server: {}", response.getMessage());
        } catch (final InterruptedException | TimeoutException | ExecutionException e) {
            logger.error("FAILED with " + e.getMessage());
        }
    }
}
