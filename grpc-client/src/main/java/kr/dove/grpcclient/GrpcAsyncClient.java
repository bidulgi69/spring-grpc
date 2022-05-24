package kr.dove.grpcclient;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.examples.lib.HelloReply;
import net.devh.boot.grpc.examples.lib.HelloRequest;
import net.devh.boot.grpc.examples.lib.SimpleGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

//  supports all communication methods (unary, server stream, client stream, bi-stream)
@Service
public class GrpcAsyncClient {

    private final Logger logger = LoggerFactory.getLogger("GrpcAsyncClient");
    @GrpcClient("local-grpc-server")
    private SimpleGrpc.SimpleStub asyncStub;

    public void unary(final String name) {
        try {
            asyncStub.sayHello(
                    HelloRequest.newBuilder()
                            .setName(name)
                            .build(),
                    new StreamObserver<>() {
                        @Override
                        public void onNext(HelloReply value) {
                            logger.info("[Async, unary] Received from the server: {}", value.getMessage());
                        }

                        @Override
                        public void onError(Throwable t) {
                            logger.error("[Async, unary] Error occurred when receiving from the server. {}", t.getMessage());
                        }

                        @Override
                        public void onCompleted() {
                            logger.info("[Async, unary] Communication completed.");
                        }
                    }
            );
        } catch (final StatusRuntimeException e) {
            logger.error("FAILED with " + e.getStatus().getCode().name());
        }
    }

    public void serverStream(final String name) {
        final List<String> messages = new LinkedList<>();
        try {
            asyncStub.sayHelloServerStream(
                    HelloRequest.newBuilder()
                            .setName(name)
                            .build(),
                    new StreamObserver<>() {
                        @Override
                        public void onNext(HelloReply value) {
                            messages.add(value.getMessage());
                        }

                        @Override
                        public void onError(Throwable t) {
                            logger.error("[Async, server stream] Error occurred when receiving from the server. {}", t.getMessage());
                        }

                        @Override
                        public void onCompleted() {
                            logger.info("[Async, server stream] Communication completed.");
                        }
                    }
            );
            
            messages
                    .forEach(message -> logger.info("[Async, server stream] Received from the server: {}", message));
        } catch (final StatusRuntimeException e) {
            logger.error("FAILED with " + e.getStatus().getCode().name());
        }
    }

    public void clientStream(final List<String> names) {
        try {
            final StreamObserver<HelloReply> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(HelloReply value) {
                    logger.info("[Async, client stream] Received from server: {}", value.getMessage());
                }

                @Override
                public void onError(Throwable t) {
                    logger.error("[Async, client stream] Error occurred when receiving from the server. {}", t.getMessage());
                }

                @Override
                public void onCompleted() {
                    logger.info("[Async, client stream] Communication completed.");
                }
            };
            StreamObserver<HelloRequest> requestObserver = asyncStub.sayHelloClientStream(responseObserver);
            names
                    .forEach(name -> requestObserver.onNext(
                            HelloRequest
                                    .newBuilder()
                                    .setName(name)
                                    .build()
                    ));
            requestObserver.onCompleted();
        } catch (final StatusRuntimeException e) {
            logger.error("FAILED with " + e.getStatus().getCode().name());
        }
    }

    public void biStream(final List<String> names) {
        final List<String> messages = new LinkedList<>();
        try {
            StreamObserver<HelloReply> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(HelloReply value) {
                    messages.add(value.getMessage());
                }

                @Override
                public void onError(Throwable t) {
                    logger.error("[Async, bi-stream] Error occurred when receiving from the server. {}", t.getMessage());
                }

                @Override
                public void onCompleted() {
                    logger.info("[Async, bi-stream] Communication completed.");
                }
            };

            StreamObserver<HelloRequest> requestObserver = asyncStub.sayHelloBothStream(responseObserver);
            names
                    .forEach(name -> requestObserver.onNext(
                            HelloRequest
                                    .newBuilder()
                                    .setName(name)
                                    .build()
                    ));
            requestObserver.onCompleted();

            messages
                    .forEach(message -> logger.info("[Async, bi-stream] Received from server: {}", message));
        } catch (final StatusRuntimeException e) {
            logger.error("FAILED with " + e.getStatus().getCode().name());
        }
    }
}
