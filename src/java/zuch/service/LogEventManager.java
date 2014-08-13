/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import zuch.exception.LogEventNotFound;
import zuch.model.LogEvent;

/**
 *
 * @author florent
 */
@Stateless
public class LogEventManager implements LogEventManagerLocal{
    
    static final Logger log = Logger.getLogger("zuch.service.LogEventManager");
    
    @PersistenceContext(unitName = "ZuchPU")
    private EntityManager em;

    @Override
    public List<LogEvent> getAllEvent() {
        Query query = em.createQuery("SELECT cEvent FROM LogEvent cEvent");
      
        query.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
        
        List<LogEvent> logList = (List<LogEvent>)query.getResultList();
        
        if(logList == null){
            return new ArrayList<>();
        }
        
        return logList;
    }

    @Override
    public LogEvent getEvent(long id) throws LogEventNotFound {
         
        LogEvent event = em.find(LogEvent.class, id);
        if(event == null){
            throw new LogEventNotFound();
        }
         
        return event;
    }

    @Override
    public List<LogEvent> getEventsOfUser(String userID) {
        Query query = em.createQuery("SELECT cEvent FROM LogEvent cEvent WHERE "
                 + "cEvent.zuser = :userID ");
        
        query.setParameter("userID", userID);
        
        
        List<LogEvent> logList = (List<LogEvent>)query.getResultList();
        
        if(logList == null){
            return new ArrayList<>();
        }
        
        return logList;
    }

    @Override
    public List<LogEvent> getLastEventsofUser(String userID, int eventNbr) {  
        Query query = em.createQuery("SELECT cEvent FROM LogEvent cEvent WHERE "
                 + "cEvent.zuser = :userID ");
        
        query.setParameter("userID", userID);
        query.setMaxResults(eventNbr);
        
        List<LogEvent> logList = (List<LogEvent>)query.getResultList();
        
        if(logList == null){
            return new ArrayList<>();
        }
        
        return logList;
    }

    @Override
    public LogEvent registerEvent(LogEvent event) {
       em.persist(event);
       em.flush();
       
       return event;
       
    }

    @Override
    public void removeEvent(long id) throws LogEventNotFound{
        LogEvent event = em.find(LogEvent.class, id);
        
        if(event == null){
            throw new LogEventNotFound();
        }
        
        em.remove(event);
        em.flush();
    }

    
    
}
