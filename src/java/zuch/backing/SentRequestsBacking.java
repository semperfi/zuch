/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.inject.Inject;
import zuch.model.AudioRequest;
import zuch.model.AudioRequestStatus;
import zuch.service.AudioManagerLocal;
import zuch.service.AudioRequestManagerLocal;

/**
 *
 * @author florent
 */
@Named(value = "sentRequestsBacking")
@RequestScoped
//@PerformanceMonitor
public class SentRequestsBacking extends BaseBacking implements Serializable{
    
    @Inject AudioRequestManagerLocal audioRequestManager;
    @Inject AudioManagerLocal audioManager;

    /**
     * Creates a new instance of SentRequestsBacking
     */
    
    public SentRequestsBacking() {
    }
    
  
    @PostConstruct
    public void init(){
        sentReqCount  = retrieveSentPendingRequests().size();
        
    }
    
   
    private long sentReqCount = 0;
   

    public List<AudioRequest> retrieveSentPendingRequests(){
        return audioRequestManager.viewSentRequests(getCurrentUser(),
                AudioRequestStatus.PENDING);
        
    }

    

    public long getSentReqCount() {
        return sentReqCount;
    }

    public void setSentReqCount(long sentReqCount) {
        this.sentReqCount = sentReqCount;
    }
     
     
    
}
