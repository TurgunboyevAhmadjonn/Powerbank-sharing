package com.anor.station.grpc;

import com.anor.station.domain.Slot;
import com.anor.station.domain.Station;
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
public class StationGrpcService extends StationServiceGrpc.StationServiceImplBase {

    private final StationRepository stationRepository;
    private final SlotRepository slotRepository;

    @Override
    public void listNearbyStations(NearbyRequest request,
                                   StreamObserver<StationList> responseObserver) {
        try {
            int radius = request.getRadiusMeters() > 0 ? (int) request.getRadiusMeters() : 5000;
            int limit = 20;

            List<Station> stations = stationRepository.findNearestStations(
                    request.getLat(), request.getLng(), radius, limit);

            List<StationSummary> summaries = stations.stream()
                    .map(this::toStationSummary)
                    .toList();

            responseObserver.onNext(StationList.newBuilder()
                    .addAllStations(summaries)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("listNearbyStations failed", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getStation(StationIdRequest request,
                           StreamObserver<StationDetails> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getStationId());

            Station station = stationRepository.findById(id).orElseThrow(
                    () -> Status.NOT_FOUND
                            .withDescription("Station not found: " + id)
                            .asRuntimeException());

            List<SlotInfo> slotInfos = slotRepository.findByStationId(id).stream()
                    .map(this::toSlotInfo)
                    .toList();

            responseObserver.onNext(StationDetails.newBuilder()
                    .setId(station.getId().toString())
                    .setName(nvl(station.getName()))
                    .setLat(station.getLat())
                    .setLng(station.getLng())
                    .setStatus(station.getStatus() == null ? "" : station.getStatus().name())
                    .setTotalSlots(station.getTotalSlots())
                    .addAllSlots(slotInfos)
                    .build());
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Invalid station_id UUID").asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("getStation failed", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private StationSummary toStationSummary(Station station) {
        int totalSlots = station.getTotalSlots();
        int freeSlots = 0;
        int availablePowerBanks = Math.max(totalSlots - freeSlots, 0);

        return StationSummary.newBuilder()
                .setId(station.getId().toString())
                .setName(nvl(station.getName()))
                .setLat(station.getLat())
                .setLng(station.getLng())
                .setAvailablePowerBanks(availablePowerBanks)
                .setFreeSlots(freeSlots)
                .setDistanceMeters(0)
                .build();
    }

    private SlotInfo toSlotInfo(Slot slot) {
        SlotInfo.Builder builder = SlotInfo.newBuilder()
                .setSlotNumber(slot.getSlotNumber())
                .setStatus(slot.getStatus() == null ? "" : slot.getStatus().name());

        if (slot.getPowerBank() != null) {
            builder.setPowerBankId(slot.getPowerBank().getId().toString());
            builder.setChargeLevel(slot.getPowerBank().getChargeLevel());
        }
        return builder.build();
    }

    private String nvl(String value) {
        return value == null ? "" : value;
    }
}
