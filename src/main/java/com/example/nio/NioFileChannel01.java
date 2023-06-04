package com.example.nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName NioFileChannel01
 * @Description:
 * @Author huangjian
 * @Date 2023/6/2
 **/
public class NioFileChannel01 {
    public static void main(String[] args) {
        String msg = "hello nio。";
        FileOutputStream fos = null;
        FileChannel channel = null;
        try {
            fos = new FileOutputStream("d:\\file01.txt");
            channel = fos.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put(msg.getBytes());

            byteBuffer.flip();
            channel.write(byteBuffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != channel){
                    channel.close();
                }
                if(fos != null){
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
