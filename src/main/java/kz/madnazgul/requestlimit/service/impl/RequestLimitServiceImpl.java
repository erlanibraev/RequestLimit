package kz.madnazgul.requestlimit.service.impl;

import kz.madnazgul.requestlimit.model.RequestLimitModel;
import kz.madnazgul.requestlimit.model.RequestsPerMinute;
import kz.madnazgul.requestlimit.service.RequestLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class RequestLimitServiceImpl implements RequestLimitService {

    private final ReentrantLock lock = new ReentrantLock();

    private static final Logger log = LoggerFactory.getLogger(RequestLimitServiceImpl.class);

    private final ConcurrentHashMap<RequestLimitModel, RequestsPerMinute> requestLimits = new ConcurrentHashMap<>();

    @Override
    public boolean inc(RequestLimitModel requestLimit, LocalDateTime requestTime) {
        boolean result = false;
        lock.lock();
        try {
            log.info(requestTime.toString() + " - requestLimit: " + requestLimit);
            RequestsPerMinute orequestsPerMinute = requestLimits.computeIfAbsent(requestLimit, k -> new RequestsPerMinute(requestTime, requestTime, 0));
            orequestsPerMinute.reqalculateRequestsPerMinute(requestTime, requestLimit.getInTimeMin());
            result = orequestsPerMinute.requestsPerMinute()<= requestLimit.requestsPerMinute();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            lock.unlock();
        }
        return result;
    }

    public void removeOldRequestsByTime(LocalDateTime now) {
        log.info("Remove old requestModel");
        lock.lock();
        try {
            requestLimits.entrySet().removeIf(entry -> {
                log.info("Remove: "+entry.getKey());
                LocalDateTime removeBefore = now.minusMinutes(entry.getKey().getInTimeMin());
                return removeBefore.isAfter(entry.getValue().getEnd());
            });
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            lock.unlock();
        }
    }
}
