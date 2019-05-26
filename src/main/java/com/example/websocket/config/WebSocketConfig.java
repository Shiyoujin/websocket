package com.example.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author white matter
 */
@Configuration
public class WebSocketConfig {
    /**
     * @Description: 检测服务类实现 是由Spring官方提供的标准实现，
     * 用于扫描ServerEndpointConfig配置类和@ServerEndpoint注解实例。
     * @return
     */
    @Bean
    public org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
