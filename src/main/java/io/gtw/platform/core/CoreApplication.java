package io.gtw.platform.core;

import io.gtw.platform.core.cache.GlobalPredicateCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoreApplication {

    public static void main(String[] args) {
        GlobalPredicateCache.initCache();
        SpringApplication.run(CoreApplication.class, args);
    }

}

