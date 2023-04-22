package com.example.hjjjj_collaborativework_springboot.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hjjjj_collaborativework_springboot.Util.Utils;

public class PackHandle {

    public static byte[] Pack(String jsonString, byte[] payload) {
        byte[] jsonBytes = jsonString.getBytes();
        byte[] jsonBytesLength = Utils.intToBytes(jsonBytes.length);
        byte[] payloadLength = Utils.intToBytes(payload.length);
        byte[] bytes = new byte[jsonBytes.length + payloadLength.length + jsonBytesLength.length + payload.length];
        System.arraycopy(jsonBytesLength, 0, bytes, 0, jsonBytesLength.length);
        System.arraycopy(payloadLength, 0, bytes, jsonBytesLength.length, payloadLength.length);
        System.arraycopy(payload, 0, bytes, jsonBytesLength.length + payloadLength.length, payload.length);
        System.arraycopy(jsonBytes, 0, bytes, jsonBytesLength.length + payloadLength.length + payload.length, jsonBytes.length);
        return bytes;
    }

    public static MessageRequest UnPack(byte[] data) {
        var result = new MessageRequest();
        //json字符串长度，4位
        var length = new byte[4];
        System.arraycopy(data, 0, length, 0, length.length);
        var jsonStringLength = Utils.bytesToInt(length, 0);
        //payload长度
        length = new byte[4];
        System.arraycopy(data, length.length, length, 0, length.length);
        var payloadLength = Utils.bytesToInt(length, 0);
        //获取jsonString
        var jsonString = new byte[jsonStringLength];

        if((data.length-payloadLength-2*length.length)<jsonStringLength){
            System.arraycopy(data, 2 * length.length, jsonString, 0, jsonStringLength-1);
        }else {
            System.arraycopy(data, 2 * length.length, jsonString, 0, jsonStringLength);
        }


        //获取payload
        var payload = new byte[payloadLength];
        if (payloadLength != 0) {
            System.arraycopy(data, 2 * length.length + jsonStringLength, payload, 0, payloadLength);
        }
        var tempString = JSON.parse(new String(jsonString)).toString();
        JSONObject jsonObject = JSONObject.parseObject(tempString, JSONObject.class);
        result.setJsonObject(jsonObject);
        result.setPayload(payload);
        result.setPayloadLength(payloadLength);
        result.setJsonStringLength(jsonStringLength);
        return result;
    }

    public static class MessageRequest {
        private int JsonStringLength;
        private int PayloadLength;
        private JSONObject JsonObject;
        private byte[] Payload;

        public int getJsonStringLength() {
            return JsonStringLength;
        }

        public void setJsonStringLength(int jsonStringLength) {
            JsonStringLength = jsonStringLength;
        }

        public int getPayloadLength() {
            return PayloadLength;
        }

        public void setPayloadLength(int payloadLength) {
            PayloadLength = payloadLength;
        }

        public JSONObject getJsonObject() {
            return JsonObject;
        }

        public void setJsonObject(JSONObject jsonObject) {
            this.JsonObject = jsonObject;
        }

        public byte[] getPayload() {
            return Payload;
        }

        public void setPayload(byte[] payload) {
            Payload = payload;
        }


    }
}
