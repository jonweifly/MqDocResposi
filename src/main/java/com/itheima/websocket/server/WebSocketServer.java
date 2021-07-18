package com.itheima.websocket.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Class: WebSocketServer
 * @Package com.itheima.websocket.server
 * @Description: 连接、消息管理
 * @Company: http://www.itheima.com/
 */
@ServerEndpoint("/ws/{userId}")
@Component
public class WebSocketServer {
    //日志
    static Log log = LogFactory.getLog(WebSocketServer.class);
    //在线数量
    private static final AtomicInteger onlineCount = new AtomicInteger(0);
    //处理客户端连接socket
    private static Map<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    //会话信息
    private Session session;
    //用户信息
    private String userId = "";

    /*
     * @Description:  打开WebSokcet连接
     * @Method: onOPen
     * @Param: [userId, session]
     * @Update:
     * @since: 1.0.0
     * @Return: void
     *
     */
    @OnOpen
    public void onOPen(@PathParam("userId") String userId, Session session) {
        //处理session和用户信息
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
        } else {
            webSocketMap.put(userId, this);
            //增加在线人数
            addOnlineCount();
        }
        try {
            //处理连接成功消息的发送
            sendMessage("Server>>>>远程WebSoket连接成功");
            log.info("用户" + userId + "成功连接，当前的在线人数为" + getOnlineCount());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*
     * @Description:  关闭连接
     * @Method: onClose
     * @Param: []
     * @Update:
     * @since: 1.0.0
     * @Return: void
     *
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            subOnlineCount();

        }
        log.info("用户退出....");
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount.decrementAndGet();
    }

    /*
     * @Description:消息中转
     * @Method: onMessage
     * @Param: [message, session]
     * @Update:
     * @since: 1.0.0
     * @Return: void
     *
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        if (StringUtils.isNotEmpty(message)) {
            try {
                //解析消息
                JSONObject jsonObject = JSON.parseObject(message);
                String toUserId = jsonObject.getString("toUserId");
                String msg = jsonObject.getString("msg");
                if (StringUtils.isNotEmpty(toUserId) && webSocketMap.containsKey(toUserId)) {
                    webSocketMap.get(toUserId).sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * @Description: 服务端向客户端发送数据
     * @Method: sendMessage
     * @Param: [s]
     * @Update:
     * @since: 1.0.0
     * @Return: void
     *
     */
    public void sendMessage(String s) throws IOException {
        this.session.getBasicRemote().sendText(s);
    }

    /*
     * @Description: 获取在线人数的数量
     * @Method: getOnlineCount
     * @Param: []
     * @Update:
     * @since: 1.0.0
     * @Return: java.util.concurrent.atomic.AtomicInteger
     *
     */
    public static synchronized AtomicInteger getOnlineCount() {
        return onlineCount;
    }

    /*
     * @Description: 增加在线人数
     * @Method: addOnlineCount
     * @Param: []
     * @Update:
     * @since: 1.0.0
     * @Return: void
     *
     */
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount.incrementAndGet();
    }

    /*
     * @Description: 服务器消息推送
     * @Method: sendInfo
     * @Param: [message, userId]
     * @Update:
     * @since: 1.0.0
     * @Return: void
     *
     */
    public static boolean sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        boolean  flag=true;
        if (StringUtils.isNotEmpty(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);

        } else {
            log.error("用户" + userId + "不在线");
            flag=false;
        }
        return  flag;
    }

}
