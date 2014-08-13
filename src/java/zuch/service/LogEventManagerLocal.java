/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.util.List;
import zuch.exception.LogEventNotFound;
import zuch.model.LogEvent;

/**
 *
 * @author florent
 */
public interface LogEventManagerLocal {
    
    public List<LogEvent> getAllEvent();
    public LogEvent getEvent(long id) throws LogEventNotFound;
    public List<LogEvent> getEventsOfUser(String userId);
    public List<LogEvent> getLastEventsofUser(String userId,int eventNbr);
    public LogEvent registerEvent(LogEvent event);
    public void removeEvent(long id) throws LogEventNotFound;
    
    
}
