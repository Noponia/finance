package com.bakaneko.finance.config;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@Builder
@Data
public class YamlConfig {

    private List<TaxData> taxData;

    @Builder
    @Data
    public static class Income {
        private Integer lower;
        private Integer upper;
    }

    @Builder
    @Data
    public static class Amount {
        private Integer base;
        private Double rate;
        private Integer starting;
    }

    @Builder
    @Data
    public static class Bracket {
        private Income income;
        private Amount amount;
    }

    @Builder
    @Data
    public static class TaxData {
        private String year;
        private List<Bracket> brackets;
        private Bracket baller;
    }
}
