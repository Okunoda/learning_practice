package com.erywim.product;

import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

@SpringBootTest
public class DiscoveryTest {

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    NacosServiceDiscovery nacosServiceDiscovery;

    @Test
    void testNacos() {
        try {
            for (String service : nacosServiceDiscovery.getServices()) {
                System.out.println(service);
                for (ServiceInstance instance : nacosServiceDiscovery.getInstances(service)) {
                    System.out.println(instance.getHost() + ":" + instance.getPort());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        for (String service : discoveryClient.getServices()) {
            System.out.println(service);
            //获取ip + port
            for (ServiceInstance instance : discoveryClient.getInstances(service)) {
                System.out.println(instance.getHost() + ":" + instance.getPort());
            }
        }
    }
}
