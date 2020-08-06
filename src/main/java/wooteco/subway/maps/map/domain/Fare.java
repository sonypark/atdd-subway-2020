package wooteco.subway.maps.map.domain;

public class Fare {
    public static int calculateFare(int totalDistance) {
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
