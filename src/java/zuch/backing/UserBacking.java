/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import zuch.exception.UserAlreadyExists;
import zuch.model.ZUser;
import zuch.qualifier.PerformanceMonitor;
import zuch.service.EncryptionServiceLocal;
import zuch.service.ZUserManager;
import zuch.service.ZUserManagerLocal;

/**
 *
 * @author florent
 */
@Named("userBacking")
@SessionScoped
//@PerformanceMonitor
public class UserBacking extends BaseBacking implements Serializable{
    
    static final Logger log = Logger.getLogger("zuch.service.UserBacking");
    
    @Inject
    private ZUserManagerLocal userManager;
    
    @Inject
    private EncryptionServiceLocal encSvc;
    
    @Named
    @Produces
    @RequestScoped
    private ZUser newUser = new ZUser();

    //private String infoMessage;

    
   public String registerUser(){
       
       String registerErrorMsg = "";
       
       if(! newUser.getPassword().equals(newUser.getPassword2())){
                    
           //getContext().addMessage(null, new FacesMessage("Password must be identical"));
        
           String msg = getBundleMessage("user.password.confirm.error");
           getContext().addMessage("subscribeForm:confirmPassword", 
                   new FacesMessage(FacesMessage.SEVERITY_ERROR,msg, null));
           
           return null;
       }
       
       try{
           
            registerErrorMsg = getBundleMessage("user.register.error");
            
            
            String passwd = encSvc.hash(newUser.getPassword());
            newUser.setPassword(passwd);
            ZUser registeredUser =  userManager.registerUser(newUser);
            
            //log user
            HttpServletRequest request = 
                    (HttpServletRequest) getContext().getExternalContext().getRequest();
            
            request.login(registeredUser.getId(),newUser.getPassword2()); //non ciphered passwd is still in newUser
              
            newUser = new ZUser(); //reset zUser object
                
            return "/protected/pages/jukebox";
            
       }catch(UserAlreadyExists ex){
           getContext().addMessage(null,
                   new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
         //  Logger.getLogger(UserBacking.class.getName()).log(Level.SEVERE, null, ex);
           
       }catch(Exception ex){
          // Logger.getLogger(UserBacking.class.getName()).log(Level.SEVERE, null, ex);
           //getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurs while registering user"));
           getContext().addMessage(null,
                   new FacesMessage(FacesMessage.SEVERITY_ERROR, registerErrorMsg, null));
       
       }
      return null;
   }
   
     public String logout(){
         FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/faces/login.xhtml?faces-redirect=true";
        
        
   }
   

    public ZUser getNewUser() {
        return newUser;
    }

    public void setNewUser(ZUser newUser) {
        this.newUser = newUser;
    }
   
   
    
}
