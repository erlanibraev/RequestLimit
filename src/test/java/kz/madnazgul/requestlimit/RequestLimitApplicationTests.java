package kz.madnazgul.requestlimit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RequestLimitApplicationTests {

    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void badGatewayError() {
        ResponseEntity<String> result = restTemplate.getForEntity("http://localhost:"+port+"/", String.class);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());

        result = restTemplate.getForEntity("http://localhost:"+port+"/", String.class);
        Assertions.assertEquals(HttpStatus.BAD_GATEWAY, result.getStatusCode());
    }

}
