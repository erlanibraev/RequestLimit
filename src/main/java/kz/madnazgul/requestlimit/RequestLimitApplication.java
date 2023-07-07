package kz.madnazgul.requestlimit;

import kz.madnazgul.requestlimit.service.RequestLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class RequestLimitApplication {

    @Autowired
    private RequestLimitService requestLimitService;

    @Scheduled( fixedDelayString = "${request_limit.cron_remove_old_millis}")
    public void removeRequestsByTime() {
        requestLimitService.removeOldRequestsByTime(LocalDateTime.now());
    }

    public static void main(String[] args) {
        SpringApplication.run(RequestLimitApplication.class, args);
    }

}
