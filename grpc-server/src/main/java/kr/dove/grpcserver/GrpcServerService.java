package kr.dove.grpcserver;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.examples.lib.HelloReply;
import net.devh.boot.grpc.examples.lib.HelloRequest;
import net.devh.boot.grpc.server.service.GrpcService;
import net.devh.boot.grpc.examples.lib.SimpleGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class GrpcServerService extends SimpleGrpc.SimpleImplBase {

    private final Logger logger = LoggerFactory.getLogger("GrpcServerService");

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        responseObserver.onNext(
                HelloReply.newBuilder()
                        .setMessage(String.format("Good to meet you %s!", request.getName()))
                        .build()
        );
        responseObserver.onCompleted();
    }

    //  1 request
    //  N responses
    @Override
    public void sayHelloServerStream(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        int rand = (int) (Math.random() * 8) + 3;

        for (int i = 0; i < rand; i++) {
            responseObserver
                    .onNext(HelloReply
                            .newBuilder()
                            .setMessage(String.format("Good to meet you %s! (%d)", request.getName(), i))
                            .build());
        }

        responseObserver.onCompleted();
    }

    //  N requests
    //  1 response
    //  Notice that this function has a return type.
    @Override
    public StreamObserver<HelloRequest> sayHelloClientStream(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(HelloRequest value) {
                logger.info("Received from the client: {}", value.getName());
            }

            @Override
            public void onError(Throwable t) {
                logger.error("Failed to receive: {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                logger.info("Receive completed.");
                responseObserver
                        .onNext(HelloReply
                                .newBuilder()
                                .setMessage("Good to meet you guys!")
                                .build());
                responseObserver.onCompleted();
            }
        };
    }

    //  N requests
    //  N responses
    @Override
    public StreamObserver<HelloRequest> sayHelloBothStream(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(HelloRequest value) {
                responseObserver
                        .onNext(HelloReply
                                .newBuilder()
                                .setMessage(String.format("Good to meet you %s!", value.getName()))
                                .build());
            }

            @Override
            public void onError(Throwable t) {
                logger.error("Failed to receive: {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                logger.info("Receive completed.");
                responseObserver.onCompleted();
            }
        };
    }
}
