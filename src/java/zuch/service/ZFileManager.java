/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import zuch.model.Audio;
import zuch.model.ZConstants;
import zuch.util.ZFileSystemUtils;

/**
 *
 * @author florent
 */
@Stateless
public class ZFileManager {
    
    @Inject ZFileSystemUtils fileSystemUtils;
    
    public void saveFile(byte[] input,String fileHash){
        
        String filePathStr = fileSystemUtils.getTracksPathString() + fileHash + ZConstants.APP_AUDIO_EXT;
        Path filePath = Paths.get(filePathStr);
        File outputFile = filePath.toFile();
         
        try(FileChannel fileChannel = new  FileOutputStream(outputFile).getChannel()){
            
            ByteBuffer buffer = ByteBuffer.wrap(input);
            fileChannel.write(buffer);
            
            //create sample file
            saveSample(fileHash);
         
        } catch (IOException ex) {
            Logger.getLogger(ZFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteFile(String fileHash){
        try {
            String filePathStr = fileSystemUtils.getTracksPathString() + fileHash + ZConstants.APP_AUDIO_EXT;
            Path filePath = Paths.get(filePathStr);
            Files.delete(filePath);
            deleteSample(fileHash);
        } catch (IOException ex) {
            Logger.getLogger(ZFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void deleteSample(String fileHash){
        try {
            String filePathStr = fileSystemUtils.getSamplesPathString() + fileHash + ZConstants.APP_AUDIO_EXT;
            Path filePath = Paths.get(filePathStr);
            Files.delete(filePath);
        } catch (IOException ex) {
            Logger.getLogger(ZFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void saveSample(String fileHash){
        
        try {
            String filePathStr = fileSystemUtils.getTracksPathString() + fileHash + ZConstants.APP_AUDIO_EXT;
            Path filePath = Paths.get(filePathStr);
            File sourceFile = filePath.toFile();
            
            String sampleFilePathStr = fileSystemUtils.getSamplesPathString() + fileHash + ZConstants.APP_AUDIO_EXT;
            Path sampleFilePath = Paths.get(sampleFilePathStr);
            File sampleOutputFile = sampleFilePath.toFile();
            
            FileChannel sinkChannel;
            try (FileChannel sourceChannel = new FileInputStream(sourceFile).getChannel()) {
                sinkChannel = new FileOutputStream(sampleOutputFile).getChannel();
                // Copy source file contents to the sink file
                sourceChannel.transferTo(0, sourceChannel.size()/4, sinkChannel);
            }
            sinkChannel.close();
        } catch (FileNotFoundException ex) { 
            Logger.getLogger(ZFileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ZFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    
    public void saveArtWork(byte[] input,String fileHash,String extension){
        
        String filePathStr = fileSystemUtils.getArtWorkPathString() + fileHash +  extension;
        Path filePath = Paths.get(filePathStr);
        if(!Files.exists(filePath)){
            File outputFile = filePath.toFile();
         
            try(FileChannel fileChannel = new  FileOutputStream(outputFile).getChannel()){

                ByteBuffer buffer = ByteBuffer.wrap(input);
                fileChannel.write(buffer);  


            } catch (IOException ex) {
                Logger.getLogger(ZFileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public void saveDefaultArtWork(byte[] input,String fileHash,String extension){
        
        String filePathStr = fileSystemUtils.getArtWorkPathString() + fileHash +  extension;
        Path filePath = Paths.get(filePathStr);
        if(!Files.exists(filePath)){
            File outputFile = filePath.toFile();
         
            try(FileChannel fileChannel = new  FileOutputStream(outputFile).getChannel()){

                ByteBuffer buffer = ByteBuffer.wrap(input);
                fileChannel.write(buffer);


            } catch (IOException ex) {
                Logger.getLogger(ZFileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public void deleteArtWork(String fileHash,String extension){
        try {
            String filePathStr = fileSystemUtils.getArtWorkPathString() + fileHash + "." + extension ;
            Path filePath = Paths.get(filePathStr);
            Files.delete(filePath);
        } catch (IOException ex) {
            Logger.getLogger(ZFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public InputStream getFileInputStream(String fileHash){
        InputStream inStream = null;
        try {
            String filePathStr = fileSystemUtils.getTracksPathString() + fileHash+ ZConstants.APP_AUDIO_EXT;
            Path filePath = Paths.get(filePathStr);
            
            inStream = Files.newInputStream(filePath, StandardOpenOption.READ);
                 
        } catch (IOException ex) {
            Logger.getLogger(ZFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return inStream;
    }
    
    public InputStream getSampleFileInputStream(String fileHash){
        InputStream inStream = null;
        try {
            String filePathStr = fileSystemUtils.getSamplesPathString() + fileHash+ ZConstants.APP_AUDIO_EXT;
            Path filePath = Paths.get(filePathStr);
            
            inStream = Files.newInputStream(filePath);
                 
        } catch (IOException ex) {
            Logger.getLogger(ZFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return inStream;
    }
    
    
    public InputStream getArtWorkFileInputStream(Audio audio){
        InputStream inStream = null;
        String fileHash = audio.getId3().getArtWorkHash();
        try {
            String filePathStr = fileSystemUtils.getArtWorkPathString()
                    + fileHash+ audio.getId3().getArtWorkExt();
            Path filePath = Paths.get(filePathStr);
            
            inStream = Files.newInputStream(filePath, StandardOpenOption.READ);
                 
        } catch (IOException ex) {
            Logger.getLogger(ZFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return inStream;
    }
   
}
