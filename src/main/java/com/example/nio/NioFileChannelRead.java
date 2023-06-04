package com.example.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName NioFileChannelRead
 * @Description:
 * @Author huangjian
 * @Date 2023/6/2
 **/
public class NioFileChannelRead {
    public static void main(String[] args) {
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            File file = new File("d:\\file01.txt");
            fis = new FileInputStream(file);

            channel = fis.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());
            int read = channel.read(byteBuffer);
            System.out.println(read);

            System.out.println(new String(byteBuffer.array()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(null != channel){
                    channel.close();
                }
                if(fis != null){
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
