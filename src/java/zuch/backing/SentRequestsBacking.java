/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import zuch.model.AudioRequest;
import zuch.model.AudioRequestStatus;
import zuch.qualifier.PerformanceMonitor;
import zuch.service.AudioManagerLocal;
import zuch.service.AudioRequestManagerLocal;

/**
 *
 * @author florent
 */
@Named(value = "sentRequestsBacking")
@ViewScoped
//@PerformanceMonitor
public class SentRequestsBacking extends BaseBacking implements Serializable{
    
    @Inject AudioRequestManagerLocal audioRequestManager;
    @Inject AudioManagerLocal audioManager;

    /**
     * Creates a new instance of SentRequestsBacking
     */
    public SentRequestsBacking() {
    }
    
     public List<AudioRequest> retrieveSentPendingRequests(){
        return audioRequestManager.viewSentRequests(getCurrentUser(),
                AudioRequestStatus.PENDING);
    }
    
}
