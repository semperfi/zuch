/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.event;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import zuch.model.Audio;
import zuch.qualifier.AudioAdded;
import zuch.qualifier.AudioDeleted;
import zuch.qualifier.AudioIndexed;
import zuch.qualifier.AudioPlaying;
import zuch.qualifier.AudioRemovedFromIndex;
import zuch.qualifier.ContentReceived;

/**
 *
 * @author florent
 */
@Stateless
public class EventService {

    @Inject 
    @AudioAdded 
    private Event<Audio> AudioAddedEvent;
    
    @Inject 
    @AudioDeleted
    private Event<Audio> AudioDeletedEvent;
    
    @Inject 
    @AudioIndexed
    private Event<String> AudioIndexedEvent;
    
    @Inject 
    @AudioRemovedFromIndex
    private Event<String> AudioRemovedFromIndexEvent;
    
    @Inject 
    @AudioPlaying
    private Event<Audio> AudioPlayingEvent;
    
    @Inject
    @ContentReceived
    private Event<AudioAddedPayload> ContentReceivedEvent;
    
    

    public Event<Audio> getAudioAddedEvent() {
        return AudioAddedEvent;
    }

      
    public Event<AudioAddedPayload> getContentReceivedEvent() {
        return ContentReceivedEvent;
    }

    

    public Event<Audio> getAudioDeletedEvent() {
        return AudioDeletedEvent;
    }

    public Event<String> getAudioIndexedEvent() {
        return AudioIndexedEvent;
    }

    public Event<String> getAudioRemovedFromIndexEvent() {
        return AudioRemovedFromIndexEvent;
    }

    public Event<Audio> getAudioPlayingEvent() {
        return AudioPlayingEvent;
    }

    
   
}
