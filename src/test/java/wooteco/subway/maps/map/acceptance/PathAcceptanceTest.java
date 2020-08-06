package wooteco.subway.maps.map.acceptance;

import static org.junit.jupiter.api.DynamicTest.*;
import static wooteco.subway.maps.line.acceptance.step.LineStationAcceptanceStep.*;
import static wooteco.subway.maps.map.acceptance.step.PathAcceptanceStep.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import com.google.common.collect.Lists;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.common.acceptance.AcceptanceTest;
import wooteco.subway.maps.line.acceptance.step.LineAcceptanceStep;
import wooteco.subway.maps.line.dto.LineResponse;
import wooteco.subway.maps.station.acceptance.step.StationAcceptanceStep;
import wooteco.subway.maps.station.dto.StationResponse;

@DisplayName("지하철 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 도곡역;
    private Long 매봉역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        교대역 = 지하철역_등록되어_있음("교대역");
        강남역 = 지하철역_등록되어_있음("강남역");
        양재역 = 지하철역_등록되어_있음("양재역");
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역");
        도곡역 = 지하철역_등록되어_있음("도곡역");
        매봉역 = 지하철역_등록되어_있음("매봉역");

        이호선 = 지하철_노선_등록되어_있음("2호선", "GREEN");
        신분당선 = 지하철_노선_등록되어_있음("신분당선", "RED");
        삼호선 = 지하철_노선_등록되어_있음("3호선", "ORANGE");

        지하철_노선에_지하철역_등록되어_있음(이호선, null, 교대역, 0, 0);
        지하철_노선에_지하철역_등록되어_있음(이호선, 교대역, 강남역, 2, 2);

        지하철_노선에_지하철역_등록되어_있음(신분당선, null, 강남역, 0, 0);
        지하철_노선에_지하철역_등록되어_있음(신분당선, 강남역, 양재역, 2, 1);

        지하철_노선에_지하철역_등록되어_있음(삼호선, null, 교대역, 0, 0);
        지하철_노선에_지하철역_등록되어_있음(삼호선, 교대역, 남부터미널역, 1, 2);
        지하철_노선에_지하철역_등록되어_있음(삼호선, 남부터미널역, 양재역, 2, 2);
        지하철_노선에_지하철역_등록되어_있음(삼호선, 양재역, 도곡역, 16, 2);
        지하철_노선에_지하철역_등록되어_있음(삼호선, 도곡역, 매봉역, 50, 2);
    }

    @DisplayName("두 역의 최단 거리 경로를 조회한다.")
    @Test
    void findPathByDistance() {
        //when
        ExtractableResponse<Response> response = 거리_경로_조회_요청("DISTANCE", 1L, 3L);

        //then
        적절한_경로를_응답(response, Lists.newArrayList(교대역, 남부터미널역, 양재역));
        총_거리와_소요_시간을_함께_응답함(response, 3, 4);
    }

    @DisplayName("거리별 요금을 계산한다.")
    @TestFactory
    Stream<DynamicTest> calculateFareByDistance() {
        int 노선_추가_요금 = 100;
        return Stream.of(
            dynamicTest("10km 이내 기본 운임", () ->{
                //when
                ExtractableResponse<Response> response = 거리_경로_조회_요청("DISTANCE", 1L, 3L);

                적절한_경로를_응답(response, Lists.newArrayList(교대역, 남부터미널역, 양재역));
                거리별_요금_계산(response, 3, 노선_추가_요금);
            }),
            dynamicTest("10km 초과부터 50km 이내 운임", () ->{
                //when
                ExtractableResponse<Response> response = 거리_경로_조회_요청("DISTANCE", 1L, 5L);

                적절한_경로를_응답(response, Lists.newArrayList(교대역, 남부터미널역, 양재역, 도곡역));
                거리별_요금_계산(response, 19, 노선_추가_요금);
            }),
            dynamicTest("50km 초과 운임", () ->{
                //when
                ExtractableResponse<Response> response = 거리_경로_조회_요청("DISTANCE", 1L, 6L);

                적절한_경로를_응답(response, Lists.newArrayList(교대역, 남부터미널역, 양재역, 도곡역, 매봉역));
                거리별_요금_계산(response, 69, 노선_추가_요금);
            })
        );
    }

    @DisplayName("두 역의 최소 시간 경로를 조회한다.")
    @Test
    void findPathByDuration() {
        //when
        ExtractableResponse<Response> response = 거리_경로_조회_요청("DURATION", 1L, 3L);
        //then
        적절한_경로를_응답(response, Lists.newArrayList(교대역, 강남역, 양재역));
        총_거리와_소요_시간을_함께_응답함(response, 4, 3);
    }

    private Long 지하철_노선_등록되어_있음(String name, String color) {
        ExtractableResponse<Response> createLineResponse1 = LineAcceptanceStep.지하철_노선_등록되어_있음(name, color);
        return createLineResponse1.as(LineResponse.class).getId();
    }

    private Long 지하철역_등록되어_있음(String name) {
        ExtractableResponse<Response> createdStationResponse1 = StationAcceptanceStep.지하철역_등록되어_있음(name);
        return createdStationResponse1.as(StationResponse.class).getId();
    }
}
