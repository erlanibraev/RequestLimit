package kz.madnazgul.requestlimit.exception;

import kz.madnazgul.requestlimit.model.RequestLimitModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_GATEWAY)
public class RequestLimitException extends Exception {
    public RequestLimitException(RequestLimitModel message) {
        super("Request limit reached "+ message.getMethod());
    }
}
