package wooteco.subway.maps.map.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FareTest {

    @DisplayName("거리별 요금 계산")
    @Test
    void calculateFare() {
        int distance1 = 10;
        int distance2 = 15;
        int distance3 = 58;

        assertAll(
            () -> assertThat(Fare.calculateFare(distance1)).isEqualTo(1250),
            () -> assertThat(Fare.calculateFare(distance2)).isEqualTo(1350),
            () -> assertThat(Fare.calculateFare(distance3)).isEqualTo(2150)
        );

    }
}
