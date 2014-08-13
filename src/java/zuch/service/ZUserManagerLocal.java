/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.util.List;
import javax.ejb.Local;
import zuch.exception.UserAlreadyExists;
import zuch.exception.UserNotFound;
import zuch.model.ZUser;

/**
 *
 * @author florent
 */
@Local
public interface ZUserManagerLocal {
    
    public ZUser getZuchUser(String userID) throws UserNotFound;
    //public ZUser getAudioOwner(long audioID) throws UserNotFound;
    public long getZuchUserCount();
    public List<ZUser> retrieveUsers();
    public ZUser registerUser(ZUser user) throws UserAlreadyExists;
    public void removeUser(String userID) throws UserNotFound;
    
}
