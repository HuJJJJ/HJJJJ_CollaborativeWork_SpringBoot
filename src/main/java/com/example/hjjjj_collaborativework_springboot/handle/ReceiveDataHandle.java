package com.example.hjjjj_collaborativework_springboot.handle;

import com.example.hjjjj_collaborativework_springboot.Util.Utils;

public class ReceiveDataHandle {

    /// <summary>
    /// 缓存
    /// </summary>
    public byte[] Buffer;

    /// <summary>
    /// 下次要接收的长度
    /// </summary>
    public int Size;

    /// <summary>
    /// 偏移量
    /// </summary>
    public int OffSet;

    /// <summary>
    /// 缓存长度
    /// </summary>
    public int BufferLength;

    public ReceiveDataHandle(int buffLength) {
        Buffer = new byte[buffLength];
        BufferLength = buffLength;
        Size = Buffer.length;
    }

    public ReceiveDataHandle() {
        int buffLength = 1024;
        Buffer = new byte[buffLength];
        BufferLength = buffLength;
        Size = Buffer.length;
    }

    public void splitData(byte[] data, int dataSize) {
        //数据包头未接收完成,需要继续接收
        if (dataSize < 8) {
            OffSet = dataSize;
            var temp = new byte[dataSize * 2];
            System.arraycopy(data, 0, temp, 0, data.length);
            Buffer = temp;
            Size = BufferLength;
            return;
        }
        var jsonLengthTemp = new byte[4];
        var payloadLengthTemp = new byte[4];
        byte[] jsonString;
        byte[] payload;
        System.arraycopy(data, 0, jsonLengthTemp, 0, 4);
        System.arraycopy(data, 4, payloadLengthTemp, 0, 4);
        var jsonLength = Utils.bytesToInt(jsonLengthTemp, 0);
        var payloadLength = Utils.bytesToInt(payloadLengthTemp, 0);


    }

}
