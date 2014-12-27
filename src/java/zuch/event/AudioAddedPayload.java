/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.event;



/**
 *
 * @author florent
 */
public class AudioAddedPayload {
    
    private final String fileHash;
    private final byte[] content;
    
    public AudioAddedPayload(byte[] content, String fileHash){
        this.content = content;
        this.fileHash = fileHash;
    }

    public String getFileHash() {
        return fileHash;
    }

   
    public byte[] getContent() {
        return content;
    }

   
}
