package com.anor.station.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: station.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class StationServiceGrpc {

  private StationServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "station.StationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.anor.station.grpc.NearbyRequest,
      com.anor.station.grpc.StationList> getListNearbyStationsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListNearbyStations",
      requestType = com.anor.station.grpc.NearbyRequest.class,
      responseType = com.anor.station.grpc.StationList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.anor.station.grpc.NearbyRequest,
      com.anor.station.grpc.StationList> getListNearbyStationsMethod() {
    io.grpc.MethodDescriptor<com.anor.station.grpc.NearbyRequest, com.anor.station.grpc.StationList> getListNearbyStationsMethod;
    if ((getListNearbyStationsMethod = StationServiceGrpc.getListNearbyStationsMethod) == null) {
      synchronized (StationServiceGrpc.class) {
        if ((getListNearbyStationsMethod = StationServiceGrpc.getListNearbyStationsMethod) == null) {
          StationServiceGrpc.getListNearbyStationsMethod = getListNearbyStationsMethod =
              io.grpc.MethodDescriptor.<com.anor.station.grpc.NearbyRequest, com.anor.station.grpc.StationList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListNearbyStations"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.station.grpc.NearbyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.station.grpc.StationList.getDefaultInstance()))
              .setSchemaDescriptor(new StationServiceMethodDescriptorSupplier("ListNearbyStations"))
              .build();
        }
      }
    }
    return getListNearbyStationsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.anor.station.grpc.StationIdRequest,
      com.anor.station.grpc.StationDetails> getGetStationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetStation",
      requestType = com.anor.station.grpc.StationIdRequest.class,
      responseType = com.anor.station.grpc.StationDetails.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.anor.station.grpc.StationIdRequest,
      com.anor.station.grpc.StationDetails> getGetStationMethod() {
    io.grpc.MethodDescriptor<com.anor.station.grpc.StationIdRequest, com.anor.station.grpc.StationDetails> getGetStationMethod;
    if ((getGetStationMethod = StationServiceGrpc.getGetStationMethod) == null) {
      synchronized (StationServiceGrpc.class) {
        if ((getGetStationMethod = StationServiceGrpc.getGetStationMethod) == null) {
          StationServiceGrpc.getGetStationMethod = getGetStationMethod =
              io.grpc.MethodDescriptor.<com.anor.station.grpc.StationIdRequest, com.anor.station.grpc.StationDetails>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetStation"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.station.grpc.StationIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.station.grpc.StationDetails.getDefaultInstance()))
              .setSchemaDescriptor(new StationServiceMethodDescriptorSupplier("GetStation"))
              .build();
        }
      }
    }
    return getGetStationMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static StationServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StationServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StationServiceStub>() {
        @java.lang.Override
        public StationServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StationServiceStub(channel, callOptions);
        }
      };
    return StationServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static StationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StationServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StationServiceBlockingStub>() {
        @java.lang.Override
        public StationServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StationServiceBlockingStub(channel, callOptions);
        }
      };
    return StationServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static StationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StationServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StationServiceFutureStub>() {
        @java.lang.Override
        public StationServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StationServiceFutureStub(channel, callOptions);
        }
      };
    return StationServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void listNearbyStations(com.anor.station.grpc.NearbyRequest request,
        io.grpc.stub.StreamObserver<com.anor.station.grpc.StationList> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListNearbyStationsMethod(), responseObserver);
    }

    /**
     */
    default void getStation(com.anor.station.grpc.StationIdRequest request,
        io.grpc.stub.StreamObserver<com.anor.station.grpc.StationDetails> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetStationMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service StationService.
   */
  public static abstract class StationServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return StationServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service StationService.
   */
  public static final class StationServiceStub
      extends io.grpc.stub.AbstractAsyncStub<StationServiceStub> {
    private StationServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StationServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StationServiceStub(channel, callOptions);
    }

    /**
     */
    public void listNearbyStations(com.anor.station.grpc.NearbyRequest request,
        io.grpc.stub.StreamObserver<com.anor.station.grpc.StationList> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListNearbyStationsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getStation(com.anor.station.grpc.StationIdRequest request,
        io.grpc.stub.StreamObserver<com.anor.station.grpc.StationDetails> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetStationMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service StationService.
   */
  public static final class StationServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<StationServiceBlockingStub> {
    private StationServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StationServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StationServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.anor.station.grpc.StationList listNearbyStations(com.anor.station.grpc.NearbyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListNearbyStationsMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.anor.station.grpc.StationDetails getStation(com.anor.station.grpc.StationIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetStationMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service StationService.
   */
  public static final class StationServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<StationServiceFutureStub> {
    private StationServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StationServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StationServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.anor.station.grpc.StationList> listNearbyStations(
        com.anor.station.grpc.NearbyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListNearbyStationsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.anor.station.grpc.StationDetails> getStation(
        com.anor.station.grpc.StationIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetStationMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_LIST_NEARBY_STATIONS = 0;
  private static final int METHODID_GET_STATION = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_LIST_NEARBY_STATIONS:
          serviceImpl.listNearbyStations((com.anor.station.grpc.NearbyRequest) request,
              (io.grpc.stub.StreamObserver<com.anor.station.grpc.StationList>) responseObserver);
          break;
        case METHODID_GET_STATION:
          serviceImpl.getStation((com.anor.station.grpc.StationIdRequest) request,
              (io.grpc.stub.StreamObserver<com.anor.station.grpc.StationDetails>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getListNearbyStationsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.anor.station.grpc.NearbyRequest,
              com.anor.station.grpc.StationList>(
                service, METHODID_LIST_NEARBY_STATIONS)))
        .addMethod(
          getGetStationMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.anor.station.grpc.StationIdRequest,
              com.anor.station.grpc.StationDetails>(
                service, METHODID_GET_STATION)))
        .build();
  }

  private static abstract class StationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    StationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.anor.station.grpc.StationProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("StationService");
    }
  }

  private static final class StationServiceFileDescriptorSupplier
      extends StationServiceBaseDescriptorSupplier {
    StationServiceFileDescriptorSupplier() {}
  }

  private static final class StationServiceMethodDescriptorSupplier
      extends StationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    StationServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (StationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new StationServiceFileDescriptorSupplier())
              .addMethod(getListNearbyStationsMethod())
              .addMethod(getGetStationMethod())
              .build();
        }
      }
    }
    return result;
  }
}
