package com.bookstudy.zuulsvr.filters;

import com.bookstudy.zuulsvr.config.ServiceConfig;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class TrackingFilter extends ZuulFilter {

    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER = true;
    private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

    @Autowired
    FilterUtils filterUtils;

    @Autowired
    ServiceConfig serviceConfig;

    /**
     * 告诉Zuul当前过滤器是前置还是后置或者路由
     * @return
     */
    @Override
    public String filterType() {
        return filterUtils.PRE_FILTER_TYPE;
    }

    /**
     * 返回当前过滤器的排序值
     * @return
     */
    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    /**
     * 返回当前过滤器是否要执行
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    /**
     * 判断
     * @return
     */
    private boolean isCorrelationIdPresent(){
        if (filterUtils.getCorrelationId() !=null){
            return true;
        }

        return false;
    }

    /**
     * 每次过滤器执行的代码
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {

        if (isCorrelationIdPresent()) {
            logger.debug("tmx-correlation-id found in tracking filter: {}. ",filterUtils.getCorrelationId());
        } else {
            filterUtils.setCorrelationId(generateCorrelationId());

            System.out.println("tmx-correlation-id generate in tracking filter: "+filterUtils.getCorrelationId());
            logger.debug("tmx-correlation-id generate in tracking filter: {}. ",filterUtils.getCorrelationId());
        }

        System.out.println("The organization id from the token is : " + getOrganizationId());
        filterUtils.setOrgId(getOrganizationId());

        RequestContext currentContext = RequestContext.getCurrentContext();
        String requestURI = currentContext.getRequest().getRequestURI();
        System.out.println("Processing incoming request for "+requestURI);
        logger.debug("Processing incoming request for {}",requestURI);
        return null;
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

    private String getOrganizationId(){
        String result = "";

        if (filterUtils.getAuthToken()!=null) {
            String authToken = filterUtils.getAuthToken()
                    .replace("Bearer ","");

            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(
                                serviceConfig.getJwtKey().getBytes("UTF-8")
                        ).parseClaimsJws(authToken).getBody();

                result = (String) claims.get("organizationId");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
