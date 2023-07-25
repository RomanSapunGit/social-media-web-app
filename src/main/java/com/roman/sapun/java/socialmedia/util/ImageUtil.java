package com.roman.sapun.java.socialmedia.util;

import java.io.IOException;
import java.util.zip.DataFormatException;

public interface ImageUtil {
    byte[] compressImage(byte[] data) throws IOException;

    byte[] decompressImage(byte[] data);
}
