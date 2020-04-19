package com.qianmi.open.api.qmcs.endpoint;

import com.qianmi.open.api.qmcs.channel.ChannelException;

import java.util.Map;

/**
 * 客户端身份标识
 */
public class ClientIdentity implements Identity {

    private String appKey;
    private String groupName;

    public ClientIdentity(String appKey, String groupName) {
        this.appKey = appKey;
        this.groupName = groupName;
    }

    @Override
    public Identity parse(Object data) throws ChannelException {
        return null;
    }

    @Override
    public void render(Object to) {
        ((Map<String, String>) to).put("app_key", this.appKey);
        ((Map<String, String>) to).put("group_name", this.groupName);
    }

    @Override
    public boolean equals(Identity id) {
        if (!id.getClass().equals(ClientIdentity.class)) {
            return false;
        }
        ClientIdentity that = (ClientIdentity) id;
        if (appKey != null ? !appKey.equals(that.appKey) : that.appKey != null) return false;
        return !(groupName != null ? !groupName.equals(that.groupName) : that.groupName != null);
    }

    @Override
    public int hashCode() {
        int result = appKey != null ? appKey.hashCode() : 0;
        result = 31 * result + (groupName != null ? groupName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return this.appKey + "~" + this.groupName;
    }
}
