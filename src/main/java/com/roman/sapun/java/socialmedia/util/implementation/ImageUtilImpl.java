package com.roman.sapun.java.socialmedia.util.implementation;

import com.roman.sapun.java.socialmedia.util.ImageUtil;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.*;

@Component
public class ImageUtilImpl implements ImageUtil {
    @Override
    public byte[] compressImage(byte[] data) throws IOException {
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream(data.length);
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        DeflaterOutputStream defOutputStream = new DeflaterOutputStream(byteArrayOut, deflater);
        try {
            defOutputStream.write(data);
        } finally {
            defOutputStream.close();
            byteArrayOut.close();
        }
        return byteArrayOut.toByteArray();
    }

    @Override
    public byte[] decompressImage(byte[] data) {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        InflaterInputStream inflaterStream = new InflaterInputStream(byteStream);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inflaterStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            inflaterStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new byte[0];
        }
    }
}
