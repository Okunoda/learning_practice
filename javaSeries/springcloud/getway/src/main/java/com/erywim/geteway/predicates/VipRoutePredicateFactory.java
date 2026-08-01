package com.erywim.geteway.predicates;

import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Component
public class VipRoutePredicateFactory extends AbstractRoutePredicateFactory<VipRoutePredicateFactory.Config> {

    public List<String> shortcutFieldOrder() {
        return Arrays.asList("param", "value"); //这里的提供给短写法的。依据短写法写入的顺序依次映射到指定的字段上。就像 ==> - Vip=user,erywim
    }

    public VipRoutePredicateFactory(){
        super(Config.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(final VipRoutePredicateFactory.Config config){
        return new Predicate<ServerWebExchange>() {
            @Override
            public boolean test(ServerWebExchange serverWebExchange) {
                ServerHttpRequest request = serverWebExchange.getRequest();
                String first = request.getQueryParams().getFirst(config.getParam());
                return Strings.isNotBlank(first) && first.equals(config.getValue());
            }
        };
    }

    public static class Config {
        private String param;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }

        private String value;
    }
}
