/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.util.List;
import javax.ejb.Local;
import zuch.exception.AudioRequestAlreadyExists;
import zuch.exception.AudioRequestNotFound;
import zuch.model.AudioRequest;
import zuch.model.AudioRequestStatus;

/**
 *
 * @author florent
 */
@Local
public interface AudioRequestManagerLocal {
    
    public AudioRequest sendAudioRequest(AudioRequest audioRequest,
            int lendLifeTime) throws AudioRequestAlreadyExists;
    public AudioRequest updateAudioRequest(AudioRequest audioRequest);
    public void approveAudioRequest(AudioRequest audioRequest) throws AudioRequestNotFound;
    public void rejectAudioRequest(AudioRequest audioRequest) throws AudioRequestNotFound;
    public List<AudioRequest> viewReceivedRequests(String zuserName, AudioRequestStatus status);
    public long viewReceivedRequestsCount(String zuserName, AudioRequestStatus status);
    public long viewSentRequestsCount(String zuserName, AudioRequestStatus status);
    public List<AudioRequest> viewSentRequests(String zuserName, AudioRequestStatus status);
    
}
