package com.itheima.websocket.controller;

import com.itheima.websocket.server.WebSocketServer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * @Class: WebSocketController
 * @Package com.itheima.websocket.controller
 * @Description: 核心控制器
 * @Company: http://www.itheima.com/
 */
@RestController
public class WebSocketController {
    @RequestMapping("im")
    public ModelAndView page() {
        return new ModelAndView("ws");
    }

    /*
     * @Description: 消息推送
     * @Method: pushToWeb
     * @Param: [message, toUserId]
     * @Update:
     * @since: 1.0.0
     * @Return: org.springframework.http.ResponseEntity<java.lang.String>
     *
     */
    @RequestMapping("/push/{toUserId}")
    public ResponseEntity<String> pushToWeb(String message, @PathVariable String toUserId) throws Exception {

        boolean flag = WebSocketServer.sendInfo(message, toUserId);
        System.out.println("添加新代码");

        System.out.println("新增代码aaaaaa");
        return flag == true ? ResponseEntity.ok("消息推送成功...") : ResponseEntity.ok("消息推送失败，用户不在线...");

    }


}
