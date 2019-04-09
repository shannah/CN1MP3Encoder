/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.media.javase;

import com.codename1.io.Util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.MPEGMode;
import net.sourceforge.lame.mp3.Lame;

/**
 *
 * @author shannah
 */
public class MP3Encoder extends com.codename1.impl.javase.FileEncoder {
    static {
        com.codename1.impl.javase.FileEncoder.register(new MP3Encoder());
    }

    public String getSourceMimetype() {
        return "audio/wav";
    }

    public String getTargetMimetype() {
        return "audio/mp3";
    }

    public byte[] encodePcmToMp3(AudioFormat inputFormat, byte[] pcm) {
        LameEncoder encoder = new LameEncoder(inputFormat, 256, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, false);

        ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
        byte[] buffer = new byte[encoder.getPCMBufferSize()];

        int bytesToTransfer = Math.min(buffer.length, pcm.length);
        int bytesWritten;
        int currentPcmPosition = 0;
        while (0 < (bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer))) {
            currentPcmPosition += bytesToTransfer;
            bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);

            mp3.write(buffer, 0, bytesWritten);
        }

        encoder.close();
        return mp3.toByteArray();
    }

    public void encode(File sourceFile, File destFile, Object arg) throws IOException {
        AudioFormat inputFormat = (AudioFormat)arg;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AudioInputStream fis = null;
        FileOutputStream fos = null;
        try {
            try {
                fis = AudioSystem.getAudioInputStream(sourceFile);
            } catch (Exception ex) {
                throw new IOException(ex);
            }
            //fis = new FileInputStream(sourceFile);
            Util.copy(fis, baos);
            fis.close();
            fis = null;
            fos = new FileOutputStream(destFile);
            ByteArrayInputStream bais = new ByteArrayInputStream(encodePcmToMp3(inputFormat, baos.toByteArray()));
            Util.copy(bais, fos);
            fos.close();
            
            
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Throwable t){}
            try {
                if (fos != null) {
                    fos.close();
                } 
            }catch (Throwable t){}
            
        }
                
    }
}
