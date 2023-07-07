package kz.madnazgul.requestlimit.service;

import kz.madnazgul.requestlimit.model.RequestLimitModel;

import java.time.LocalDateTime;

public interface RequestLimitService {

    boolean inc(RequestLimitModel requestLimit, LocalDateTime requestTime);
    void removeOldRequestsByTime(LocalDateTime removeBefore);
}
