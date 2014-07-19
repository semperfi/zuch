/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import zuch.exception.UserAlreadyExists;
import zuch.exception.UserNotFound;
import zuch.model.ZGroup;
import zuch.model.ZGroupRole;
import zuch.model.ZUser;

/**
 *
 * @author florent
 */
@Stateless
public class ZUserManager implements ZUserManagerLocal{
    
    @PersistenceContext(unitName = "ZuchPU")
    private EntityManager em;

    
    
    @Override
    public ZUser getZuchUser(String userID) throws UserNotFound {
        
        Query query = em.createQuery("SELECT zuchUser FROM ZUser zuchUser WHERE "
                            + "zuchUser.id = :userID");
        query.setParameter("userID", userID);
        
        ZUser zUser = (ZUser)query.getSingleResult();
        
        if(zUser == null){
            throw  new UserNotFound();
        }
        
        return zUser;
    }
    
   
    
    

    @Override
    public List<ZUser> retrieveUsers() {
       // List<ZUser> zUsers = new ArrayList<>();
        
        Query query = em.createQuery("SELECT zuchUser FROM ZUser zuchUser");
        
        List<ZUser> zUsers = query.getResultList();
        
        if(zUsers == null){
            return new ArrayList<>();
        }
        
        return zUsers;
    }
    

    @Override
    public ZUser registerUser(ZUser user) throws UserAlreadyExists {
        
        Query query = em.createQuery("select zuchUser from ZUser zuchUser where "
                            + "zuchUser.id = :userID");
        query.setParameter("userID", user.getId());
        
        try{
            query.getSingleResult();
            throw new UserAlreadyExists();
        }catch(NoResultException  ex){
            Logger.getLogger(ZUserManager.class.getName()).log(Level.FINER, "No user found");
        }
        
        List<ZGroup> userGroups = new ArrayList<>();
        ZGroup zGroup = new ZGroup();
        zGroup.setzUser(user);
        zGroup.setRole(ZGroupRole.ZUCH_USER);
        
        userGroups.add(zGroup);
        
        user.setGroupList(userGroups);
        
        /*
        try{
        
        }catch(Exception ex){
        
            Logger.getLogger(AudioManager.class.getName()).log(Level.WARNING, "CANNOT PERSIST AUDIO...");
        }
        */
        
        em.persist(user);
        em.flush();
        
        return user;
        
        
    }

    
    @Override
    public void removeUser(String userID) throws UserNotFound {
        
        ZUser zUser = em.find(ZUser.class, userID);
        
        if(zUser == null){
            throw new UserNotFound();
        }
        
        em.remove(zUser);
        em.flush();
    }

    
    
}
