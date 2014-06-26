/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;


import java.util.List;
import zuch.exception.AudioLendingAlreadyExists;
import zuch.model.AudioLending;

/**
 *
 * @author florent
 */

public interface AudioLendingManagerLocal {

    public AudioLending registerLending(AudioLending lending) throws AudioLendingAlreadyExists;
    public List<AudioLending> getAllLendings();
    public AudioLending updateLending(AudioLending lending);
    public void endLending(AudioLending lending);
    public List<AudioLending> viewsLending();
}
