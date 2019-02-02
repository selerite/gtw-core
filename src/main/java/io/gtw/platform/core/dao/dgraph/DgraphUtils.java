package io.gtw.platform.core.dao.dgraph;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphGrpc.DgraphStub;
import io.grpc.*;
import io.gtw.platform.core.utils.EnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class DgraphUtils {
    private static final Logger logger = LoggerFactory.getLogger(DgraphUtils.class);

    public static DgraphClient createDgraphClient() {
        String dgraphHostname = EnvUtils.DgraphHostnameFromEnv;
        String dgraphPort = EnvUtils.DgraphPortFromEnv;
        int dgraphPortInt = 9080;
        try {
            dgraphPortInt = Integer.parseInt(dgraphPort);
        } catch (Exception e) {
            logger.info("no dgraph port got, use default port 9080");
        }
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(dgraphHostname, dgraphPortInt).usePlaintext(true).build();
        DgraphStub dgraphStub = DgraphGrpc.newStub(managedChannel);
        ClientInterceptor timeoutInterceptor = new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
                return channel.newCall(methodDescriptor, callOptions.withDeadlineAfter(5000, TimeUnit.MILLISECONDS));
            }
        };
        dgraphStub.withInterceptors(timeoutInterceptor);
        return new DgraphClient(dgraphStub);
    }
}
