package com.erywim.service.inteceptor;

import com.erywim.service.IpCounterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

public class IpCountInterceptor implements HandlerInterceptor {
    @Autowired
    private IpCounterService ipCounterService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ipCounterService.record();
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
