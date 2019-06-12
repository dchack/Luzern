package com.github.hopedc.luzern.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @author hopedc
 * @date 2017-03-09 15:29
 */
public class luzernConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "luzern", name = "enable", matchIfMissing = true)
    public luzernController luzernController() {
        return new luzernController();
    }
}
