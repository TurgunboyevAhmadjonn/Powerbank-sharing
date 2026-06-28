package com.anor.station.grpc;

import com.anor.station.entity.Station;
import com.anor.station.entity.Slot;
import com.anor.station.grpc.proto.*;
import com.anor.station.repository.SlotRepository;
import com.anor.station.repository.StationRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class StationGrpcService extends StationGrpcServiceGrpc.StationGrpcServiceImplBase {

    private final StationRepository stationRepository;
    private final SlotRepository    slotRepository;

    @Override
    public void getNearestStations(GetNearestStationsRequest request,
                                   StreamObserver<GetNearestStationsResponse> responseObserver) {
        try {
            int radius = request.getRadiusMeters() > 0 ? request.getRadiusMeters() : 5000;
            int limit  = request.getLimit()        > 0 ? request.getLimit()        : 20;

            List<Station> stations = stationRepository.findNearestStations(
                    request.getLatitude(), request.getLongitude(), radius, limit);

            List<StationDto> dtos = stations.stream()
                    .map(this::toStationDto)
                    .toList();

            responseObserver.onNext(GetNearestStationsResponse.newBuilder()
                    .addAllStations(dtos)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("getNearestStations failed", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getStationById(GetStationByIdRequest request,
                               StreamObserver<GetStationByIdResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getStationId());

            Station station = stationRepository.findById(id).orElseThrow(
                    () -> Status.NOT_FOUND
                            .withDescription("Station not found: " + id)
                            .asRuntimeException());

            List<SlotDto> slotDtos = slotRepository.findByStationId(id).stream()
                    .map(this::toSlotDto)
                    .toList();

            responseObserver.onNext(GetStationByIdResponse.newBuilder()
                    .setStation(toStationDto(station))
                    .addAllSlots(slotDtos)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("getStationById failed", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private StationDto toStationDto(Station s) {
        return StationDto.newBuilder()
                .setId(s.getId().toString())
                .setName(s.getName())
                .setAddress(s.getAddress())
                .setLatitude(s.getLatitude())
                .setLongitude(s.getLongitude())
                .setAvailableSlots(s.getAvailableSlots())
                .setTotalSlots(s.getTotalSlots())
                .setStatus(s.getStatus())
                .build();
    }

    private SlotDto toSlotDto(Slot slot) {
        SlotDto.Builder builder = SlotDto.newBuilder()
                .setId(slot.getId().toString())
                .setSlotNumber(slot.getSlotNumber())
                .setStatus(slot.getStatus());

        if (slot.getPowerBank() != null) {
            builder.setPowerbankId(slot.getPowerBank().getId().toString());
            builder.setBatteryLevel(slot.getPowerBank().getBatteryLevel());
        }
        return builder.build();
    }
}
