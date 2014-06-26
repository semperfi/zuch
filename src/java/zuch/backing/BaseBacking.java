/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;

import java.util.Map;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author florent
 */
public class BaseBacking {
    
    protected FacesContext getContext(){
        return FacesContext.getCurrentInstance();
    }
    
    
    protected String getBundleMessage(String resourceId){
        
      
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle bundle = 
                facesContext.getApplication().getResourceBundle(facesContext, "bundle");
      
        
        String resource = bundle.getString(resourceId);
        
        return resource;
    }
    
    protected Map getRequestMap(){
        return getContext().getExternalContext().getRequestMap();
    }
    
    protected HttpServletRequest getRequest(){
    
        return  (HttpServletRequest)getContext().getExternalContext().getRequest();
    }
    
    protected HttpSession getSession() {
        return (HttpSession) getContext().getExternalContext().getSession(false);
    }
    
    protected String getCurrentUser(){
    
        return getContext().getExternalContext().getUserPrincipal().getName();
    }
    
}
