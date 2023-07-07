package kz.madnazgul.requestlimit.model;

import kz.madnazgul.requestlimit.service.RequestLimit;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.time.temporal.ChronoUnit;

public class RequestLimitModel {

    private String method;
    private String ip;
    private int requestNumber;
    private int inTimeMin;

    private RequestLimitModel(String method, String ip, int requestNumber, int inTime) {
        this.method = method;
        this.ip = ip;
        this.requestNumber = requestNumber;
        this.inTimeMin = inTime;
    }

    public String getMethod() {
        return method;
    }

    public String getIp() {
        return ip;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public int getInTimeMin() {
        return inTimeMin;
    }

    public int requestsPerMinute() {
        return Double.valueOf(Math.ceil((double) requestNumber / inTimeMin)).intValue();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestLimitModel)) return false;

        RequestLimitModel that = (RequestLimitModel) o;

        if (requestNumber != that.requestNumber) return false;
        if (inTimeMin != that.inTimeMin) return false;
        if (!method.equals(that.method)) return false;
        return ip.equals(that.ip);
    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + ip.hashCode();
        result = 31 * result + requestNumber;
        result = 31 * result + inTimeMin;
        return result;
    }

    @Override
    public String toString() {
        return "RequestLimitModel{" +
                "method='" + method + '\'' +
                ", ip='" + ip + '\'' +
                ", requestNumber=" + requestNumber +
                ", inTime=" + inTimeMin +
                '}';
    }

    public static RequestLimitModelBuilder RequestLimitModelBuilder() {
        return new RequestLimitModelBuilder();
    }

    public static class RequestLimitModelBuilder {
        private String methodName;
        private String ip;

        private int requestNumber;

        private int inTimeMin;

        public RequestLimitModelBuilder setMethod(Method method) {
            if(method == null) {
                throw new NullPointerException("methodName not be null");
            }
            this.methodName = method.getDeclaringClass().getName() + "." + method.getName();
            return this;
        }

        public RequestLimitModelBuilder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public RequestLimitModelBuilder setRequestNumber(int requestNumber) {
            this.requestNumber = requestNumber;
            return this;
        }

        public RequestLimitModelBuilder setInTimeMin(int inTimeMin) {
            this.inTimeMin = inTimeMin;
            return this;
        }

        public RequestLimitModel build() {
            if (methodName == null) {
                throw new NullPointerException("methodName is not be null");
            }
            if (ip == null) {
                throw new NullPointerException("ip is not be null");
            }
            return new RequestLimitModel(
                    methodName,
                    ip,
                    requestNumber,
                    inTimeMin
            );
        }
    }
}
