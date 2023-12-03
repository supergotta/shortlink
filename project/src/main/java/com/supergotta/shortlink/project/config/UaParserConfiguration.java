package com.supergotta.shortlink.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua_parser.Parser;

import java.io.IOException;

/**
 * 请求头解析器配置
 */
@Configuration
public class UaParserConfiguration {

    @Bean
    public Parser parser() throws IOException {
        return new Parser();
    }
}
