package kr.dove.grpcclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HttpController {

    private final String defaultName = "hello world";

    @Autowired
    public HttpController(GrpcBlockingClient grpcBlockingClient,
                          GrpcAsyncClient grpcAsyncClient,
                          GrpcFutureClient grpcFutureClient) {
        this.grpcBlockingClient = grpcBlockingClient;
        this.grpcAsyncClient = grpcAsyncClient;
        this.grpcFutureClient = grpcFutureClient;
    }

    private final GrpcBlockingClient grpcBlockingClient;

    @RequestMapping(value = "/blocking/unary")
    public void blockingUnary(@RequestParam(defaultValue = defaultName) String name) {
        grpcBlockingClient.unary(name);
    }

    @RequestMapping(value = "/blocking/serverStream")
    public void blockingServerStream(@RequestParam(defaultValue = defaultName) String name) {
        grpcBlockingClient.serverStream(name);
    }


    private final GrpcAsyncClient grpcAsyncClient;

    @RequestMapping(value = "/async/unary")
    public void asyncUnary(@RequestParam(defaultValue = defaultName) String name) {
        grpcAsyncClient.unary(name);
    }

    @RequestMapping(value = "/async/serverStream")
    public void asyncServerStream(@RequestParam(defaultValue = defaultName) String name) {
        grpcAsyncClient.serverStream(name);
    }

    @RequestMapping(value = "/async/clientStream")
    public void asyncClientStream(@RequestParam(name = "name") List<String> names) {
        grpcAsyncClient.clientStream(names);
    }

    @RequestMapping(value = "/async/biStream")
    public void asyncBiStream(@RequestParam(name = "name") List<String> names) {
        grpcAsyncClient.biStream(names);
    }


    private final GrpcFutureClient grpcFutureClient;

    @RequestMapping(value = "/future/unary")
    public void futureUnary(@RequestParam(defaultValue = defaultName) String name) {
        grpcFutureClient.unary(name);
    }
}
