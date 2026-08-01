package org.erywim.chapter3.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author Erywim 2024/7/30
 */

@ToString(callSuper = true)
@Data
public class RpcResponseMessage extends Message {
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}