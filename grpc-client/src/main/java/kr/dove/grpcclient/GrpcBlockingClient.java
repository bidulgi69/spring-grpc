package kr.dove.grpcclient;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.examples.lib.HelloReply;
import net.devh.boot.grpc.examples.lib.HelloRequest;
import net.devh.boot.grpc.examples.lib.SimpleGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Iterator;

//  Unary communication and server stream communication are supported only.
@Service
public class GrpcBlockingClient {

    private final Logger logger = LoggerFactory.getLogger("GrpcBlockingClient");

    @GrpcClient("local-grpc-server")
    private SimpleGrpc.SimpleBlockingStub blockingStub;

    public void unary(final String name) {
        try {
            final HelloReply response = blockingStub.sayHello(HelloRequest.newBuilder().setName(name).build());
            logger.info("[Blocking, unary] Received from the server: {}", response.getMessage());
        } catch (final StatusRuntimeException e) {
            logger.error("FAILED with " + e.getMessage());
        }
    }

    public void serverStream(final String name) {
        try {
            final Iterator<HelloReply> responses = blockingStub.sayHelloServerStream(
                    HelloRequest.newBuilder()
                            .setName(name)
                            .build()
            );
            while (responses.hasNext()) {
                logger.info("[Blocking, server stream] Received from the server: {}", responses.next().getMessage());
            }
        } catch (final StatusRuntimeException e) {
            logger.error("FAILED with " + e.getMessage());
        }
    }
}
