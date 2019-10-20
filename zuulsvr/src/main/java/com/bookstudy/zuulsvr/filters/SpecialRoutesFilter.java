package com.bookstudy.zuulsvr.filters;

import com.bookstudy.zuulsvr.model.AbTestingRoute;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class SpecialRoutesFilter extends ZuulFilter {

    private static final int FILTER_ORDER =  1;
    private static final boolean SHOULD_FILTER =true;

    @Autowired
    FilterUtils filterUtils;

    @Autowired
    RestTemplate restTemplate;

    //这个bean是spring cloud提供的用于将原始请求的信息复制到zuul服务代理请求中去
    private ProxyRequestHelper proxyRequestHelper = new ProxyRequestHelper(new ZuulProperties());

    @Override
    public String filterType() {
        return filterUtils.ROUTE_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext currentContext = RequestContext.getCurrentContext();

        //调用特殊路由服务确定当前服务是否有其他节点
        //通过service_id来查找,应为zuul是结合了Eureka服务的,所以在路径进入的时候就可以拿到当前url中的serviceId信息并放到requestContext中
        AbTestingRoute abTestingRoute = getAbRoutingInfo(filterUtils.getServiceId());

//        if (abTestingRoute!=null && userSpecialRoute(abTestingRoute)){
        if (abTestingRoute!=null && false){
            //如果使用新的路由,则需要重新构建路由,newEndPoint+uri
            String newRouteString = buildRouteString(
                    currentContext.getRequest().getRequestURI(),
                    abTestingRoute.getEndpoint(),
                    currentContext.get("serviceId").toString()
            );

            //跳转到新的路由上去
            forwardToSpecialRoute(newRouteString);
        }

        return null;
    }

    /**
     * 将原始请求的信息(request获取head,body等)添加到新的请求中
     * @param newRouteString
     */
    private void forwardToSpecialRoute(String newRouteString) {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        //复制原始请求信息
        MultiValueMap<String, String> zuulRequestHeaders = this.proxyRequestHelper.buildZuulRequestHeaders(request);
        MultiValueMap<String, String> zuulRequestQueryParams = this.proxyRequestHelper.buildZuulRequestQueryParams(request);

        //获取请求类型(post/get/...)
        String sMethod = getVerb(request);

        //获取请求体的输入流
        InputStream inputStream = getRequestBody(request);

        //意义不明
        if (request.getContentLength()<0) {
            ctx.setChunkedRequestBody();
        }
        this.proxyRequestHelper.addIgnoredHeaders();

        //使用HttpClient调用新的路由
        CloseableHttpClient httpClient = null;
        HttpResponse response = null;

        httpClient = HttpClients.createDefault();
        try {
            response = forward(httpClient,sMethod,newRouteString,request,zuulRequestHeaders,zuulRequestQueryParams,inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            }
            catch(IOException ex){}
        }
    }

    private HttpResponse forward(HttpClient httpClient, String verb, String uri, HttpServletRequest request,
                                 MultiValueMap<String, String> headers, MultiValueMap<String, String> params,
                                 InputStream requestEntity) throws IOException {

        //先构建一个全新的Http
        Map<String, Object> info = this.proxyRequestHelper.debug(verb, uri, headers, params,
                requestEntity);
        URL host = new URL(uri);
        HttpHost httpHost = getHttpHost(host);

        HttpRequest httpRequest;
        int contentLength = request.getContentLength();
        InputStreamEntity entity = new InputStreamEntity(requestEntity, contentLength,
                request.getContentType() != null
                        ? ContentType.create(request.getContentType()) : null);
        switch (verb.toUpperCase()) {
            case "POST":
                HttpPost httpPost = new HttpPost(uri);
                httpRequest = httpPost;
                httpPost.setEntity(entity);
                break;
            case "PUT":
                HttpPut httpPut = new HttpPut(uri);
                httpRequest = httpPut;
                httpPut.setEntity(entity);
                break;
            case "PATCH":
                HttpPatch httpPatch = new HttpPatch(uri );
                httpRequest = httpPatch;
                httpPatch.setEntity(entity);
                break;
            default:
                httpRequest = new BasicHttpRequest(verb, uri);
        }

        try {
            httpRequest.setHeaders(convertHeaders(headers));
            HttpResponse zuulResponse = forwardRequest(httpClient, httpHost, httpRequest);
            return zuulResponse;
        } finally {

        }
    }

    /**
     *
     * @param httpClient
     * @param httpHost
     * @param httpRequest
     * @return
     */
    private HttpResponse forwardRequest(HttpClient httpClient, HttpHost httpHost, HttpRequest httpRequest) throws IOException {
        return httpClient.execute(httpHost, httpRequest);
    }

    /**
     *
     * @param headers
     * @return
     */
    private Header[] convertHeaders(MultiValueMap<String, String> headers) {
        List<Header> list = new ArrayList<>();
        for (String name : headers.keySet()) {
            for (String value : headers.get(name)) {
                list.add(new BasicHeader(name, value));
            }
        }
        return list.toArray(new BasicHeader[0]);
    }

    private HttpHost getHttpHost(URL host) {
        HttpHost httpHost = new HttpHost(host.getHost(), host.getPort(), host.getProtocol());
        return httpHost;
    }

    /**
     * 获取请求体 (输入流InputStream 返回)
     * @param request
     * @return
     */
    private InputStream getRequestBody(HttpServletRequest request) {
        InputStream requestEntity = null;
        try {
            requestEntity = request.getInputStream();
        } catch (IOException e) {
            // no RequestBody is ok
        }
        return requestEntity;
    }

    /**
     * 获取请求的类型(method: get / post/ put / )
     * @param request
     * @return
     */
    private String getVerb(HttpServletRequest request) {
        String method = request.getMethod();
        return method.toUpperCase();
    }

    /**
     *
     * @param oldUrl    原始请求
     * @param newEndPoint    从specialrouteservice获取的新的newEndPoint
     * @param serviceId 服务ID
     * @return
     */
    private String buildRouteString(String oldUrl, String newEndPoint, String serviceId) {
        int index = oldUrl.indexOf(serviceId);
        String uri = oldUrl.substring(index + serviceId.length()+1);
        System.out.println("Target route : "+String.format("%s/%s",newEndPoint,uri));

        return String.format("%s/%s",newEndPoint,uri);
    }

    /**
     * 根据返回的服务路径权重,和随机生成的数比较,判断是否需要使用新的路由
     * @param abTestingRoute
     * @return
     */
    private boolean userSpecialRoute(AbTestingRoute abTestingRoute) {
        Random random = new Random();

        if (abTestingRoute.getActive().equals("N")){
            return false;
        }

        int value = random.nextInt((10-1)+1)+1;

        if (abTestingRoute.getWeight()<value) {
            System.out.println("user new url");
            return true;
        }
        System.out.println("user old url");
        return false;
    }

    /**
     * 本方法主要用来调用SpecialRoute服务来查询当前Serviceid下是否有多个服务
     * 使用RestTemplate 来远程调用
     * @param serviceName
     * @return
     */
    private AbTestingRoute getAbRoutingInfo(String serviceName) {

        ResponseEntity<AbTestingRoute> restExchange = null;

        try {
            restExchange = restTemplate.exchange("http://specialroutesservice/v1/route/abtesting/{serviceName}",
                   HttpMethod.GET, null, AbTestingRoute.class,
                   serviceName);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode()== HttpStatus.NOT_FOUND) {
                return null;
            }
            throw e;
//            e.printStackTrace();
        }

        return restExchange.getBody();
    }
}
