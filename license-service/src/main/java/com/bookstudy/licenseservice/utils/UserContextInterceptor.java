package com.bookstudy.licenseservice.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class UserContextInterceptor implements ClientHttpRequestInterceptor {

    /**
     * 此方法在RestTemplate发生实际的Http服务调用之前被调用
     * @param request
     * @param body
     * @param execution
     * @return
     * @throws IOException
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        HttpHeaders headers = request.getHeaders();
        String correlationId = UserContextHolder.getUserContext().getCorrelationId();
        String authToken = UserContextHolder.getUserContext().getAuthToken();

        headers.add(UserContext.CORRELATION_ID,correlationId);
        headers.add(UserContext.AUTH_TOKEN,authToken);
        return execution.execute(request,body);
    }
}
