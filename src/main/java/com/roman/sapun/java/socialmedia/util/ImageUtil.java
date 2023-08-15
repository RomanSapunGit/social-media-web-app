package com.roman.sapun.java.socialmedia.util;

import java.io.IOException;
import java.util.zip.DataFormatException;

public interface ImageUtil {
    /**
     * Compresses the given image data using the Deflate algorithm.
     *
     * @param data The image data to compress.
     * @return The compressed image data.
     * @throws IOException If an I/O error occurs during compression.
     */
    byte[] compressImage(byte[] data) throws IOException;
    /**
     * Decompresses the given compressed image data using the Deflate algorithm.
     *
     * @param data The compressed image data to decompress.
     * @return The decompressed image data.
     */
    byte[] decompressImage(byte[] data);
}
