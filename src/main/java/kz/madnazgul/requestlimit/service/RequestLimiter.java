package kz.madnazgul.requestlimit.service;

import kz.madnazgul.requestlimit.exception.RequestLimitException;
import kz.madnazgul.requestlimit.model.RequestLimitModel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class RequestLimiter implements EmbeddedValueResolverAware {

    private RequestLimitService requestLimitService;

    private ClientIpService clientIpService;

    private StringValueResolver resolver;

    public RequestLimiter(RequestLimitService requestLimitService,
                          ClientIpService clientIpService) {
        this.requestLimitService = requestLimitService;
        this.clientIpService = clientIpService;
    }

    @Around("@annotation(RequestLimit)")
    public Object requestLimit(ProceedingJoinPoint joinPoint) throws Exception, Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        Object proceed;

        RequestLimitModel requestLimitModel = get(method, getClientIp());

        if(!requestLimitService.inc(requestLimitModel, LocalDateTime.now())) {
            throw new RequestLimitException(requestLimitModel);
        }

        proceed = joinPoint.proceed();

        return proceed;
    }

    private RequestLimitModel get(Method method, String ip) {
        RequestLimit requestLimit = method.getAnnotation(RequestLimit.class);

        String requestNumberStr = resolver.resolveStringValue(requestLimit.requestNumber());
        String inTimeMinStr = resolver.resolveStringValue(requestLimit.inTimeMin());
        int requestNumber = Integer.parseInt(requestNumberStr);
        int inTimeMin = Integer.parseInt(inTimeMinStr);

        return RequestLimitModel
                .RequestLimitModelBuilder()
                .setMethod(method)
                .setIp(ip)
                .setRequestNumber(requestNumber)
                .setInTimeMin(inTimeMin)
                .build();
    }

    private String getClientIp() {
        return clientIpService.getIp();
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }
}
