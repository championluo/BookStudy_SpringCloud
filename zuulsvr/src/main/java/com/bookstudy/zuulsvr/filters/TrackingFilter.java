package com.bookstudy.zuulsvr.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrackingFilter extends ZuulFilter {

    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER = true;
    private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

    @Autowired
    FilterUtils filterUtils;

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

        RequestContext currentContext = RequestContext.getCurrentContext();
        String requestURI = currentContext.getRequest().getRequestURI();
        logger.debug("Processing incoming request for {}",requestURI);
        return null;
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}
