package com.anor.rental.grpc.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: rental.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class RentalServiceGrpc {

  private RentalServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "rental.RentalService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.CreateRentalRequest,
      com.anor.rental.grpc.proto.RentalResponse> getCreateRentalMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateRental",
      requestType = com.anor.rental.grpc.proto.CreateRentalRequest.class,
      responseType = com.anor.rental.grpc.proto.RentalResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.CreateRentalRequest,
      com.anor.rental.grpc.proto.RentalResponse> getCreateRentalMethod() {
    io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.CreateRentalRequest, com.anor.rental.grpc.proto.RentalResponse> getCreateRentalMethod;
    if ((getCreateRentalMethod = RentalServiceGrpc.getCreateRentalMethod) == null) {
      synchronized (RentalServiceGrpc.class) {
        if ((getCreateRentalMethod = RentalServiceGrpc.getCreateRentalMethod) == null) {
          RentalServiceGrpc.getCreateRentalMethod = getCreateRentalMethod =
              io.grpc.MethodDescriptor.<com.anor.rental.grpc.proto.CreateRentalRequest, com.anor.rental.grpc.proto.RentalResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateRental"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.rental.grpc.proto.CreateRentalRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.rental.grpc.proto.RentalResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RentalServiceMethodDescriptorSupplier("CreateRental"))
              .build();
        }
      }
    }
    return getCreateRentalMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.GetRentalStatusRequest,
      com.anor.rental.grpc.proto.RentalResponse> getGetRentalStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetRentalStatus",
      requestType = com.anor.rental.grpc.proto.GetRentalStatusRequest.class,
      responseType = com.anor.rental.grpc.proto.RentalResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.GetRentalStatusRequest,
      com.anor.rental.grpc.proto.RentalResponse> getGetRentalStatusMethod() {
    io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.GetRentalStatusRequest, com.anor.rental.grpc.proto.RentalResponse> getGetRentalStatusMethod;
    if ((getGetRentalStatusMethod = RentalServiceGrpc.getGetRentalStatusMethod) == null) {
      synchronized (RentalServiceGrpc.class) {
        if ((getGetRentalStatusMethod = RentalServiceGrpc.getGetRentalStatusMethod) == null) {
          RentalServiceGrpc.getGetRentalStatusMethod = getGetRentalStatusMethod =
              io.grpc.MethodDescriptor.<com.anor.rental.grpc.proto.GetRentalStatusRequest, com.anor.rental.grpc.proto.RentalResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetRentalStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.rental.grpc.proto.GetRentalStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.rental.grpc.proto.RentalResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RentalServiceMethodDescriptorSupplier("GetRentalStatus"))
              .build();
        }
      }
    }
    return getGetRentalStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.GetRentalHistoryRequest,
      com.anor.rental.grpc.proto.RentalHistoryResponse> getGetRentalHistoryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetRentalHistory",
      requestType = com.anor.rental.grpc.proto.GetRentalHistoryRequest.class,
      responseType = com.anor.rental.grpc.proto.RentalHistoryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.GetRentalHistoryRequest,
      com.anor.rental.grpc.proto.RentalHistoryResponse> getGetRentalHistoryMethod() {
    io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.GetRentalHistoryRequest, com.anor.rental.grpc.proto.RentalHistoryResponse> getGetRentalHistoryMethod;
    if ((getGetRentalHistoryMethod = RentalServiceGrpc.getGetRentalHistoryMethod) == null) {
      synchronized (RentalServiceGrpc.class) {
        if ((getGetRentalHistoryMethod = RentalServiceGrpc.getGetRentalHistoryMethod) == null) {
          RentalServiceGrpc.getGetRentalHistoryMethod = getGetRentalHistoryMethod =
              io.grpc.MethodDescriptor.<com.anor.rental.grpc.proto.GetRentalHistoryRequest, com.anor.rental.grpc.proto.RentalHistoryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetRentalHistory"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.rental.grpc.proto.GetRentalHistoryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.rental.grpc.proto.RentalHistoryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RentalServiceMethodDescriptorSupplier("GetRentalHistory"))
              .build();
        }
      }
    }
    return getGetRentalHistoryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.FinishRentalRequest,
      com.anor.rental.grpc.proto.RentalResponse> getFinishRentalMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "FinishRental",
      requestType = com.anor.rental.grpc.proto.FinishRentalRequest.class,
      responseType = com.anor.rental.grpc.proto.RentalResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.FinishRentalRequest,
      com.anor.rental.grpc.proto.RentalResponse> getFinishRentalMethod() {
    io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.FinishRentalRequest, com.anor.rental.grpc.proto.RentalResponse> getFinishRentalMethod;
    if ((getFinishRentalMethod = RentalServiceGrpc.getFinishRentalMethod) == null) {
      synchronized (RentalServiceGrpc.class) {
        if ((getFinishRentalMethod = RentalServiceGrpc.getFinishRentalMethod) == null) {
          RentalServiceGrpc.getFinishRentalMethod = getFinishRentalMethod =
              io.grpc.MethodDescriptor.<com.anor.rental.grpc.proto.FinishRentalRequest, com.anor.rental.grpc.proto.RentalResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "FinishRental"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.rental.grpc.proto.FinishRentalRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.rental.grpc.proto.RentalResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RentalServiceMethodDescriptorSupplier("FinishRental"))
              .build();
        }
      }
    }
    return getFinishRentalMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.GetNearbyStationsRequest,
      com.anor.rental.grpc.proto.StationListResponse> getGetNearbyStationsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetNearbyStations",
      requestType = com.anor.rental.grpc.proto.GetNearbyStationsRequest.class,
      responseType = com.anor.rental.grpc.proto.StationListResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.GetNearbyStationsRequest,
      com.anor.rental.grpc.proto.StationListResponse> getGetNearbyStationsMethod() {
    io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.GetNearbyStationsRequest, com.anor.rental.grpc.proto.StationListResponse> getGetNearbyStationsMethod;
    if ((getGetNearbyStationsMethod = RentalServiceGrpc.getGetNearbyStationsMethod) == null) {
      synchronized (RentalServiceGrpc.class) {
        if ((getGetNearbyStationsMethod = RentalServiceGrpc.getGetNearbyStationsMethod) == null) {
          RentalServiceGrpc.getGetNearbyStationsMethod = getGetNearbyStationsMethod =
              io.grpc.MethodDescriptor.<com.anor.rental.grpc.proto.GetNearbyStationsRequest, com.anor.rental.grpc.proto.StationListResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetNearbyStations"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.rental.grpc.proto.GetNearbyStationsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.rental.grpc.proto.StationListResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RentalServiceMethodDescriptorSupplier("GetNearbyStations"))
              .build();
        }
      }
    }
    return getGetNearbyStationsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.GetStationDetailsRequest,
      com.anor.rental.grpc.proto.StationDetailsResponse> getGetStationDetailsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetStationDetails",
      requestType = com.anor.rental.grpc.proto.GetStationDetailsRequest.class,
      responseType = com.anor.rental.grpc.proto.StationDetailsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.GetStationDetailsRequest,
      com.anor.rental.grpc.proto.StationDetailsResponse> getGetStationDetailsMethod() {
    io.grpc.MethodDescriptor<com.anor.rental.grpc.proto.GetStationDetailsRequest, com.anor.rental.grpc.proto.StationDetailsResponse> getGetStationDetailsMethod;
    if ((getGetStationDetailsMethod = RentalServiceGrpc.getGetStationDetailsMethod) == null) {
      synchronized (RentalServiceGrpc.class) {
        if ((getGetStationDetailsMethod = RentalServiceGrpc.getGetStationDetailsMethod) == null) {
          RentalServiceGrpc.getGetStationDetailsMethod = getGetStationDetailsMethod =
              io.grpc.MethodDescriptor.<com.anor.rental.grpc.proto.GetStationDetailsRequest, com.anor.rental.grpc.proto.StationDetailsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetStationDetails"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.rental.grpc.proto.GetStationDetailsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.anor.rental.grpc.proto.StationDetailsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RentalServiceMethodDescriptorSupplier("GetStationDetails"))
              .build();
        }
      }
    }
    return getGetStationDetailsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RentalServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RentalServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RentalServiceStub>() {
        @java.lang.Override
        public RentalServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RentalServiceStub(channel, callOptions);
        }
      };
    return RentalServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RentalServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RentalServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RentalServiceBlockingStub>() {
        @java.lang.Override
        public RentalServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RentalServiceBlockingStub(channel, callOptions);
        }
      };
    return RentalServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RentalServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RentalServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RentalServiceFutureStub>() {
        @java.lang.Override
        public RentalServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RentalServiceFutureStub(channel, callOptions);
        }
      };
    return RentalServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void createRental(com.anor.rental.grpc.proto.CreateRentalRequest request,
        io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.RentalResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateRentalMethod(), responseObserver);
    }

    /**
     */
    default void getRentalStatus(com.anor.rental.grpc.proto.GetRentalStatusRequest request,
        io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.RentalResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetRentalStatusMethod(), responseObserver);
    }

    /**
     */
    default void getRentalHistory(com.anor.rental.grpc.proto.GetRentalHistoryRequest request,
        io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.RentalHistoryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetRentalHistoryMethod(), responseObserver);
    }

    /**
     */
    default void finishRental(com.anor.rental.grpc.proto.FinishRentalRequest request,
        io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.RentalResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFinishRentalMethod(), responseObserver);
    }

    /**
     */
    default void getNearbyStations(com.anor.rental.grpc.proto.GetNearbyStationsRequest request,
        io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.StationListResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetNearbyStationsMethod(), responseObserver);
    }

    /**
     */
    default void getStationDetails(com.anor.rental.grpc.proto.GetStationDetailsRequest request,
        io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.StationDetailsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetStationDetailsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service RentalService.
   */
  public static abstract class RentalServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return RentalServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service RentalService.
   */
  public static final class RentalServiceStub
      extends io.grpc.stub.AbstractAsyncStub<RentalServiceStub> {
    private RentalServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RentalServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RentalServiceStub(channel, callOptions);
    }

    /**
     */
    public void createRental(com.anor.rental.grpc.proto.CreateRentalRequest request,
        io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.RentalResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateRentalMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getRentalStatus(com.anor.rental.grpc.proto.GetRentalStatusRequest request,
        io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.RentalResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetRentalStatusMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getRentalHistory(com.anor.rental.grpc.proto.GetRentalHistoryRequest request,
        io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.RentalHistoryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetRentalHistoryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void finishRental(com.anor.rental.grpc.proto.FinishRentalRequest request,
        io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.RentalResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFinishRentalMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getNearbyStations(com.anor.rental.grpc.proto.GetNearbyStationsRequest request,
        io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.StationListResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetNearbyStationsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getStationDetails(com.anor.rental.grpc.proto.GetStationDetailsRequest request,
        io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.StationDetailsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetStationDetailsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service RentalService.
   */
  public static final class RentalServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<RentalServiceBlockingStub> {
    private RentalServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RentalServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RentalServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.anor.rental.grpc.proto.RentalResponse createRental(com.anor.rental.grpc.proto.CreateRentalRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateRentalMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.anor.rental.grpc.proto.RentalResponse getRentalStatus(com.anor.rental.grpc.proto.GetRentalStatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetRentalStatusMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.anor.rental.grpc.proto.RentalHistoryResponse getRentalHistory(com.anor.rental.grpc.proto.GetRentalHistoryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetRentalHistoryMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.anor.rental.grpc.proto.RentalResponse finishRental(com.anor.rental.grpc.proto.FinishRentalRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFinishRentalMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.anor.rental.grpc.proto.StationListResponse getNearbyStations(com.anor.rental.grpc.proto.GetNearbyStationsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetNearbyStationsMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.anor.rental.grpc.proto.StationDetailsResponse getStationDetails(com.anor.rental.grpc.proto.GetStationDetailsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetStationDetailsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service RentalService.
   */
  public static final class RentalServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<RentalServiceFutureStub> {
    private RentalServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RentalServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RentalServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.anor.rental.grpc.proto.RentalResponse> createRental(
        com.anor.rental.grpc.proto.CreateRentalRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateRentalMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.anor.rental.grpc.proto.RentalResponse> getRentalStatus(
        com.anor.rental.grpc.proto.GetRentalStatusRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetRentalStatusMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.anor.rental.grpc.proto.RentalHistoryResponse> getRentalHistory(
        com.anor.rental.grpc.proto.GetRentalHistoryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetRentalHistoryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.anor.rental.grpc.proto.RentalResponse> finishRental(
        com.anor.rental.grpc.proto.FinishRentalRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFinishRentalMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.anor.rental.grpc.proto.StationListResponse> getNearbyStations(
        com.anor.rental.grpc.proto.GetNearbyStationsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetNearbyStationsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.anor.rental.grpc.proto.StationDetailsResponse> getStationDetails(
        com.anor.rental.grpc.proto.GetStationDetailsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetStationDetailsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_RENTAL = 0;
  private static final int METHODID_GET_RENTAL_STATUS = 1;
  private static final int METHODID_GET_RENTAL_HISTORY = 2;
  private static final int METHODID_FINISH_RENTAL = 3;
  private static final int METHODID_GET_NEARBY_STATIONS = 4;
  private static final int METHODID_GET_STATION_DETAILS = 5;

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
        case METHODID_CREATE_RENTAL:
          serviceImpl.createRental((com.anor.rental.grpc.proto.CreateRentalRequest) request,
              (io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.RentalResponse>) responseObserver);
          break;
        case METHODID_GET_RENTAL_STATUS:
          serviceImpl.getRentalStatus((com.anor.rental.grpc.proto.GetRentalStatusRequest) request,
              (io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.RentalResponse>) responseObserver);
          break;
        case METHODID_GET_RENTAL_HISTORY:
          serviceImpl.getRentalHistory((com.anor.rental.grpc.proto.GetRentalHistoryRequest) request,
              (io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.RentalHistoryResponse>) responseObserver);
          break;
        case METHODID_FINISH_RENTAL:
          serviceImpl.finishRental((com.anor.rental.grpc.proto.FinishRentalRequest) request,
              (io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.RentalResponse>) responseObserver);
          break;
        case METHODID_GET_NEARBY_STATIONS:
          serviceImpl.getNearbyStations((com.anor.rental.grpc.proto.GetNearbyStationsRequest) request,
              (io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.StationListResponse>) responseObserver);
          break;
        case METHODID_GET_STATION_DETAILS:
          serviceImpl.getStationDetails((com.anor.rental.grpc.proto.GetStationDetailsRequest) request,
              (io.grpc.stub.StreamObserver<com.anor.rental.grpc.proto.StationDetailsResponse>) responseObserver);
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
          getCreateRentalMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.anor.rental.grpc.proto.CreateRentalRequest,
              com.anor.rental.grpc.proto.RentalResponse>(
                service, METHODID_CREATE_RENTAL)))
        .addMethod(
          getGetRentalStatusMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.anor.rental.grpc.proto.GetRentalStatusRequest,
              com.anor.rental.grpc.proto.RentalResponse>(
                service, METHODID_GET_RENTAL_STATUS)))
        .addMethod(
          getGetRentalHistoryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.anor.rental.grpc.proto.GetRentalHistoryRequest,
              com.anor.rental.grpc.proto.RentalHistoryResponse>(
                service, METHODID_GET_RENTAL_HISTORY)))
        .addMethod(
          getFinishRentalMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.anor.rental.grpc.proto.FinishRentalRequest,
              com.anor.rental.grpc.proto.RentalResponse>(
                service, METHODID_FINISH_RENTAL)))
        .addMethod(
          getGetNearbyStationsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.anor.rental.grpc.proto.GetNearbyStationsRequest,
              com.anor.rental.grpc.proto.StationListResponse>(
                service, METHODID_GET_NEARBY_STATIONS)))
        .addMethod(
          getGetStationDetailsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.anor.rental.grpc.proto.GetStationDetailsRequest,
              com.anor.rental.grpc.proto.StationDetailsResponse>(
                service, METHODID_GET_STATION_DETAILS)))
        .build();
  }

  private static abstract class RentalServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RentalServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.anor.rental.grpc.proto.Rental.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RentalService");
    }
  }

  private static final class RentalServiceFileDescriptorSupplier
      extends RentalServiceBaseDescriptorSupplier {
    RentalServiceFileDescriptorSupplier() {}
  }

  private static final class RentalServiceMethodDescriptorSupplier
      extends RentalServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    RentalServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (RentalServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RentalServiceFileDescriptorSupplier())
              .addMethod(getCreateRentalMethod())
              .addMethod(getGetRentalStatusMethod())
              .addMethod(getGetRentalHistoryMethod())
              .addMethod(getFinishRentalMethod())
              .addMethod(getGetNearbyStationsMethod())
              .addMethod(getGetStationDetailsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
