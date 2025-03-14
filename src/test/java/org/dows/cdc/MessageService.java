package org.dows.cdc;


public interface MessageService {

    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMessage(Message message);

}