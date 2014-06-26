/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.AsyncContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
 *
 * @author florent
 */
public class AsyncWriteStreamListener implements WriteListener{
    
    InputStream in;
    ServletOutputStream out;
    AsyncContext ctx;
    
    public AsyncWriteStreamListener(InputStream in,
            ServletOutputStream out,AsyncContext ctx){
        
        this.in = in;
        this.out = out;
        this.ctx = ctx;
    }

    @Override
    public void onWritePossible() throws IOException {
        
        final int BYTES = 2 * 1024;
        int length = 0;
         
        byte[] bbuf = new byte[BYTES];
        
        while (((length = in.read(bbuf)) != -1)){
                  if(out!= null){
                       out.write(bbuf,0,length);
                  }  
                   
                }
    }

    @Override
    public void onError(Throwable t) {
        System.out.printf("thread [%s] Error occurred=%s\n", 
        Thread.currentThread().getName(), t.getMessage());
        ctx.complete();
    }
    
}
