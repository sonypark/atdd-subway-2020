package wooteco.subway.maps.map.acceptance.step;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.maps.map.dto.PathResponse;
import wooteco.subway.maps.station.dto.StationResponse;

public class PathAcceptanceStep {
    public static ExtractableResponse<Response> 거리_경로_조회_요청(String type, long source, long target) {
        return RestAssured.given().log().all().
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            get("/paths?source={sourceId}&target={targetId}&type={type}", source, target, type).
            then().
            log().all().
            extract();
    }

    public static void 적절한_경로를_응답(ExtractableResponse<Response> response, ArrayList<Long> expectedPath) {
        PathResponse pathResponse = response.as(PathResponse.class);
        List<Long> stationIds = pathResponse.getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedPath);
    }

    public static void 총_거리와_소요_시간을_함께_응답함(ExtractableResponse<Response> response, int totalDistance,
        int totalDuration) {
        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getDistance()).isEqualTo(totalDistance);
        assertThat(pathResponse.getDuration()).isEqualTo(totalDuration);
    }

    public static void 거리별_요금_계산(ExtractableResponse<Response> response, int totalDistance, int extraFare, int age) {
        PathResponse pathResponse = response.as(PathResponse.class);
        int fare = calculateFare(totalDistance) + extraFare;
        int finalFare = fare;
        if (13 <= age && age < 19) {
            finalFare = (int)((fare - 350) * 0.8);
        }
        if (6 <= age && age < 13) {
            finalFare = (int)((fare - 350) * 0.5);
        }
        assertThat(pathResponse.getFare()).isEqualTo(finalFare);
    }

    private static int calculateFare(int totalDistance) {
        int basePrice = 1250;
        if (totalDistance <= 10) {
            return basePrice;
        }
        if (totalDistance <= 50) {
            return basePrice + calculateOverFare(totalDistance - 10, 5);
        }
        int middlePrice = basePrice + calculateOverFare(40, 5);
        return middlePrice + calculateOverFare(totalDistance - 50, 8);
    }

    private static int calculateOverFare(int distance, int overDistance) {
        if (distance == 0) {
            return 0;
        }
        return (int)((Math.ceil((distance - 1) / overDistance) + 1) * 100);
    }
}
