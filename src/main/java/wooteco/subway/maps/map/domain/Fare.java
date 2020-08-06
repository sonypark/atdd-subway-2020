package wooteco.subway.maps.map.domain;

public class Fare {
    public static int calculateFare(int totalDistance) {
        if (totalDistance <= 10) {
            return 1250;
        }
        if (totalDistance <= 50) {
            return 1250 + calculateOverFare(totalDistance - 10, 5);
        }
        return 1250 + calculateOverFare(totalDistance - 50, 8);
    }

    private static int calculateOverFare(int distance, int overDistance) {
        if (distance == 0) {
            return 0;
        }
        return (int)((Math.ceil((distance - 1) / overDistance) + 1) * 100);
    }
}
