package kz.madnazgul.requestlimit.controller;

import kz.madnazgul.requestlimit.service.RequestLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class RequestLimitController {

    @GetMapping
    @RequestLimit(requestNumber = "${request_limit.request_number}", inTimeMin = "${request_limit.in_time_min}")
    @ResponseBody
    public String index() {
        return "";
    }

}
