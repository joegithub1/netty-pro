package com.example.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @ClassName NioFileChannelFrom
 * @Description:
 * @Author huangjian
 * @Date 2023/6/2
 **/
public class NioFileChannelFrom {
    public static void main(String[] args) throws IOException {

        FileInputStream fis = new FileInputStream("d:\\poto.jpg");
        FileChannel fisChannel = fis.getChannel();

        FileOutputStream fos = new FileOutputStream("d:\\newImg.jpg");
        FileChannel fosChannel = fos.getChannel();

        fosChannel.transferFrom(fisChannel,0,fisChannel.size());

        fosChannel.close();
        fisChannel.close();
        fos.close();
        fis.close();
    }
}
