package com.erywim.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "ip.counter")
@Configuration
public class IpCounterProperties {
    private String displayMode = DISPLAY_MODE.SIMPLE.getMode();

    public enum DISPLAY_MODE {
        SIMPLE("simple"), DETAIL("detail");

        private String mode;

        public String getMode() {
            return mode;
        }

        DISPLAY_MODE(String mode) {
            this.mode = mode;
        }
    }

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }
}
