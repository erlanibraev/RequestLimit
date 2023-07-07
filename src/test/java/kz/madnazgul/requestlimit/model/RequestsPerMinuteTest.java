package kz.madnazgul.requestlimit.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestsPerMinuteTest {

    @Test
    public void startTest() {
        RequestsPerMinute request = new RequestsPerMinute(
                LocalDateTime.parse("2023-01-01T00:00:00"),
                LocalDateTime.parse("2023-01-01T00:00:00"),
                0
        );

        assertEquals(0, request.requestsPerMinute());

        request.reqalculateRequestsPerMinute(LocalDateTime.parse("2023-01-01T00:00:00"), 1);


        int result = request.requestsPerMinute();
        assertEquals(1, result);
    }

    @Test
    public void manyRequestTest() {
        RequestsPerMinute request = new RequestsPerMinute(
                LocalDateTime.parse("2023-01-01T00:00:00"),
                LocalDateTime.parse("2023-01-01T00:00:00"),
                1
        );

        request.reqalculateRequestsPerMinute(LocalDateTime.parse("2023-01-01T00:00:01"), 1);

        assertEquals(2, request.requestsPerMinute());

        request.reqalculateRequestsPerMinute(LocalDateTime.parse("2023-01-01T00:02:00"), 1);

        assertEquals(1, request.requestsPerMinute());

        request.reqalculateRequestsPerMinute(LocalDateTime.parse("2023-01-01T00:02:30"), 1);

        assertEquals(2, request.requestsPerMinute());

        request.reqalculateRequestsPerMinute(LocalDateTime.parse("2023-01-01T00:03:00"), 1);

        assertEquals(2, request.requestsPerMinute());
    }
}
