package com.example.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName NioFileChannelCopy
 * @Description:
 * @Author huangjian
 * @Date 2023/6/2
 **/
public class NioFileChannelCopy {
    public static void main(String[] args) throws Exception{

        File file = new File("d:\\file01.txt");
        FileInputStream fis = new FileInputStream(file);
        FileChannel fisChannel = fis.getChannel();


        FileOutputStream fos = new FileOutputStream("d:\\fileCopy.txt");
        FileChannel fosChannel = fos.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while(true){
            byteBuffer.clear();//清空buffer数据
            int read = fisChannel.read(byteBuffer);
            if(read == -1){
                break;
            }

            byteBuffer.flip();
            fosChannel.write(byteBuffer);
        }


        fosChannel.close();
        fos.close();

        fisChannel.close();
        fis.close();
    }
}
