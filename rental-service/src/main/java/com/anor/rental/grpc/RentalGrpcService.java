// src/main/java/com/anor/rental/grpc/RentalGrpcService.java
package com.anor.rental.grpc;

import com.anor.rental.domain.entity.Rental;
import com.anor.rental.grpc.proto.*;
import com.anor.rental.service.RentalService;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RentalGrpcService extends RentalServiceGrpc.RentalServiceImplBase {

    private final RentalService rentalService;

    @Override
    public void createRental(CreateRentalRequest request,
                             StreamObserver<RentalResponse> responseObserver) {
        try {
            Rental rental = rentalService.createRental(
                    UUID.fromString(request.getUserId()),
                    UUID.fromString(request.getStationId()),
                    UUID.fromString(request.getSlotId()),
                    UUID.fromString(request.getCardId()),
                    request.getIdempotencyKey()
            );
            responseObserver.onNext(toProto(rental));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    io.grpc.Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getRentalStatus(GetRentalStatusRequest request,
                                StreamObserver<RentalResponse> responseObserver) {
        try {
            Rental rental = rentalService.getRentalStatus(UUID.fromString(request.getRentalId()));
            responseObserver.onNext(toProto(rental));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    io.grpc.Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getRentalHistory(GetRentalHistoryRequest request,
                                 StreamObserver<RentalHistoryResponse> responseObserver) {
        try {
            Page<Rental> page = rentalService.getRentalHistory(
                    UUID.fromString(request.getUserId()),
                    request.getPage(),
                    request.getSize() > 0 ? request.getSize() : 20
            );

            RentalHistoryResponse.Builder builder = RentalHistoryResponse.newBuilder()
                    .setTotalCount((int) page.getTotalElements())
                    .setPage(page.getNumber())
                    .setSize(page.getSize());

            page.getContent().forEach(r -> builder.addRentals(toProto(r)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    io.grpc.Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void finishRental(FinishRentalRequest request,
                             StreamObserver<RentalResponse> responseObserver) {
        try {
            Rental rental = rentalService.finishRental(
                    UUID.fromString(request.getRentalId()),
                    UUID.fromString(request.getStationId()),
                    UUID.fromString(request.getSlotId())
            );
            responseObserver.onNext(toProto(rental));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    io.grpc.Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    // ── Mapping helpers ────────────────────────────────────────────

    private RentalResponse toProto(Rental rental) {
        RentalResponse.Builder b = RentalResponse.newBuilder()
                .setRentalId(rental.getId().toString())
                .setUserId(rental.getUserId().toString())
                .setStationId(rental.getStationId().toString())
                .setStatus(rental.getStatus().name());

        if (rental.getPowerbankId() != null)
            b.setPowerbankId(rental.getPowerbankId().toString());

        if (rental.getStartedAt() != null)
            b.setStartedAt(toTimestamp(rental.getStartedAt()));

        if (rental.getFinishedAt() != null)
            b.setFinishedAt(toTimestamp(rental.getFinishedAt()));

        if (rental.getTotalCost() != null)
            b.setTotalCost(rental.getTotalCost().toPlainString());

        return b.build();
    }

    private Timestamp toTimestamp(OffsetDateTime odt) {
        return Timestamp.newBuilder()
                .setSeconds(odt.toEpochSecond())
                .setNanos(odt.getNano())
                .build();
    }
}
