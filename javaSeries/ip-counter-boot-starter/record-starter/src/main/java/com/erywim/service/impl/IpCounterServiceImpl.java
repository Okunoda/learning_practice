package com.erywim.service.impl;

import com.erywim.service.IpCounterService;
import com.erywim.service.config.IpCounterProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IpCounterServiceImpl implements IpCounterService {

    @Autowired
    private HttpServletRequest request;
    private final Map<String ,Integer> ipMap = new ConcurrentHashMap<>();

    @Autowired
    private IpCounterProperties ipCounterProperties;

    @Override
    public void record() {
        //记录ip
        String ip = request.getRemoteAddr();
        //记录访问次数
        Integer ipCount = ipMap.getOrDefault(ip, 0);
        //更新
        ipMap.put(ip, ipCount + 1);
        //输出
        ipMap.forEach((k, v) -> {
            //打印规范的详细日志
            if(IpCounterProperties.DISPLAY_MODE.DETAIL.getMode().equals(ipCounterProperties.getDisplayMode())){
                System.out.println("--------------------------------");
                System.out.println("\t\t\tip访问监控");
                System.out.println("ip是：" + k + "访问了：" + v + "次");
                System.out.println("--------------------------------");
            }else{
                System.out.println("ip是：" + k + "访问了：" + v + "次");
            }

        });
    }
}
