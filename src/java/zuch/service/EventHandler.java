/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import zuch.model.LogEvent;
import zuch.qualifier.LogSessionCreated;
import zuch.qualifier.LogSessionDestroyed;


/**
 *
 * @author florent
 */
@Singleton
@Startup
public class EventHandler {
    
    static final Logger log = Logger.getLogger("zuch.service.AudioManager");
    
    @Inject LogEventManagerLocal logEventManager;
    
    
    public void onCreate(@Observes @LogSessionCreated LogEvent logEvent){
        log.info("EVENT HANDLER SESSION BEAN CREATED...");
        logEventManager.registerEvent(logEvent);
    }
    
  
    public void onDestroy(@Observes @LogSessionDestroyed LogEvent logEvent){
        log.info("EVENT HANDLER SESSION BEAN WILL BE DESTROY...");
        logEventManager.registerEvent(logEvent);
    }
}
