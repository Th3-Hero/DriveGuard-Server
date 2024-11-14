package com.group11.driveguard.app.services;

import com.group11.driveguard.api.map.Address;
import com.group11.driveguard.api.map.Location;
import com.group11.driveguard.api.map.LocationPair;
import com.group11.driveguard.api.map.Road;
import com.group11.driveguard.app.exceptions.UnexpectedMapApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
@Transactional
@RequiredArgsConstructor
public class DrivingContextService {

    private final RestClient restClient;

    @Value("${config.map-api-url}")
    private String MAP_API_BASE_URL;

    private <T, U> U callApi(String uri, T body, Class<U> responseType) {
        final var response = restClient.post()
            .uri(uri)
            .body(body)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                (clientRequest, clientResponse) -> {
                    throw new UnexpectedMapApiException("Error getting address from coordinates. Status: %s\nStatus Text: %s".formatted(clientResponse.getStatusCode(), clientResponse.getStatusText()));
                }
            )
            .toEntity(responseType);

        return response.getBody();
    }

    public Address getAddressFromCoordinates(Location location) {
        String uri = MAP_API_BASE_URL + "/map/address";

        return callApi(uri, location, Address.class);
    }

    public Road getRoadFromCoordinates(Location location) {
        String uri = MAP_API_BASE_URL + "/map/road";

        return callApi(uri, location, Road.class);
    }

    public double getDistanceBetweenCoordinates(LocationPair locationPair) {
        String uri = MAP_API_BASE_URL + "/map/distance";

        return callApi(uri, locationPair, Double.class);
    }
}
