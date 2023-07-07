package kz.madnazgul.requestlimit.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class RequestsPerMinute {

    private LocalDateTime start;

    private LocalDateTime end;

    private int requestNumbers;

    public RequestsPerMinute(LocalDateTime start, LocalDateTime end, int requestNumbers) {
        this.start = start;
        this.end = end;
        this.requestNumbers = requestNumbers;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public int getRequestNumbers() {
        return requestNumbers;
    }

    public int requestsPerMinute() {
        int result = 0;
        if(getRequestNumbers() > 0) {
            int minutes = 1 + Long.valueOf(ChronoUnit.MINUTES.between(getStart(), getEnd())).intValue();
            result =  Double.valueOf(Math.ceil((double) requestNumbers / minutes)).intValue();
        }
        return result;
    }

    public RequestsPerMinute reqalculateRequestsPerMinute(LocalDateTime newRequestTime, int inTimeMin) {
        if (newRequestTime.minusMinutes(inTimeMin).isAfter(start)) {
            start = newRequestTime;
            requestNumbers = 1;
        } else {
            requestNumbers = requestNumbers + 1;
        }
        end = newRequestTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestsPerMinute)) return false;

        RequestsPerMinute that = (RequestsPerMinute) o;

        if (requestNumbers != that.requestNumbers) return false;
        if (!Objects.equals(start, that.start)) return false;
        return Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + requestNumbers;
        return result;
    }

    @Override
    public String toString() {
        return "RequestsPerMinute{" +
                "start=" + start +
                ", end=" + end +
                ", requestNumbers=" + requestNumbers +
                '}';
    }
}
