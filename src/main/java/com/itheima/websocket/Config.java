package com.itheima.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
* @Class: Config
* @Package com.itheima.websocket
* @Description:开启websocket端点
* @Company: http://www.itheima.com/
*/
@Configuration
public class Config {
    @Bean
    public ServerEndpointExporter  serverEndpointExporter(){
        return  new ServerEndpointExporter();
    }
}
