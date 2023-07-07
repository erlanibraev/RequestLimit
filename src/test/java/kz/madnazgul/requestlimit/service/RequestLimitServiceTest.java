package kz.madnazgul.requestlimit.service;

import kz.madnazgul.requestlimit.model.RequestLimitModel;
import kz.madnazgul.requestlimit.service.impl.RequestLimitServiceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class RequestLimitServiceTest {

    RequestLimitService requestLimitService = new RequestLimitServiceImpl();

    Method method = RequestLimitServiceTest.class.getMethod("empty");

    RequestLimitModel model1 = RequestLimitModel
            .RequestLimitModelBuilder()
            .setMethod(method)
            .setIp("127.0.0.1")
            .setRequestNumber(2)
            .setInTimeMin(1)
            .build();

    RequestLimitModel model2 = RequestLimitModel
            .RequestLimitModelBuilder()
            .setMethod(method)
            .setIp("127.0.0.2")
            .setRequestNumber(2)
            .setInTimeMin(1)
            .build();

    RequestLimitServiceTest() throws NoSuchMethodException {
    }

    @Test
    public void addRequestLimitOneModel() {
        assertTrue(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:00:00")));
        assertTrue(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:00:01")));
        assertFalse(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:00:03")));
        assertTrue(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:03:00")));
    }

    @Test
    public void addRequestLimitTwoModel() {
        assertTrue(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:00:00")));
        assertTrue(requestLimitService.inc(model2, LocalDateTime.parse("2023-01-01T00:00:00")));
        assertTrue(requestLimitService.inc(model2, LocalDateTime.parse("2023-01-01T00:00:01")));
        assertTrue(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:00:01")));
        assertFalse(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:00:03")));
        assertFalse(requestLimitService.inc(model2, LocalDateTime.parse("2023-01-01T00:00:03")));
        assertTrue(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:03:00")));
        assertTrue(requestLimitService.inc(model2, LocalDateTime.parse("2023-01-01T00:03:00")));
    }

    @Test
    public void concurrentTestTrue() throws InterruptedException {
        int numberOfThreads = 100;
        int numberInc = 2;

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        CountDownLatch testLatch = new CountDownLatch(numberOfThreads * numberInc);

        for(int  i=0; i < numberOfThreads; i++) {
            final int ipCount = i;
            service.execute(() -> {
                RequestLimitModel model = RequestLimitModel
                        .RequestLimitModelBuilder()
                        .setMethod(method)
                        .setIp("127.0.0."+ ipCount)
                        .setRequestNumber(2)
                        .setInTimeMin(1)
                        .build();

                for(int j = 0; j < numberInc ; j++) {
                    if(requestLimitService.inc(model, LocalDateTime.now())) {
                        testLatch.countDown();
                    }
                    System.out.println(model.toString() + " - true");
                }
                latch.countDown();
            });
        }
        latch.await();
        assertEquals(0, testLatch.getCount());
    }

    @Test
    public void concurrentTestFalse() throws InterruptedException {
        int numberOfThreads = 100;
        int numberInc = 3;

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        CountDownLatch testLatch = new CountDownLatch(numberOfThreads * numberInc);

        for(int  i=0; i < numberOfThreads; i++) {
            final int ipCount = i;
            service.execute(() -> {
                RequestLimitModel model = RequestLimitModel
                        .RequestLimitModelBuilder()
                        .setMethod(method)
                        .setIp("127.0.0."+ ipCount)
                        .setRequestNumber(2)
                        .setInTimeMin(1)
                        .build();

                for(int j = 0; j < numberInc; j++) {
                    if(j < model.getInTimeMin() + 1) {
                        if(requestLimitService.inc(model, LocalDateTime.now())) {
                            testLatch.countDown();
                        }
                        System.out.println(model.toString() + " - true");
                    } else {
                        if(!requestLimitService.inc(model, LocalDateTime.now())) {
                            testLatch.countDown();
                        }
                        System.out.println(model.toString() + " - false");
                    }
                }
                latch.countDown();
            });
        }

        latch.await();
        assertEquals(0, testLatch.getCount());
    }

    @Test
    public void removeOldRequestsByTimeTest() {
        assertTrue(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:00:00")));
        assertTrue(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:00:01")));
        assertFalse(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:00:03")));
        requestLimitService.removeOldRequestsByTime(LocalDateTime.parse("2023-01-01T00:02:00"));
        assertTrue(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:00:03")));
        assertTrue(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:02:01")));

        assertTrue(requestLimitService.inc(model1, LocalDateTime.parse("2023-01-01T00:03:00")));
    }

    public void empty() {

    }
}