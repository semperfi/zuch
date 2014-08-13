/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.util;

import java.util.logging.Logger;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author florent
 */
@WebListener
public class ZuchSessionListener implements HttpSessionListener{
    
     static final Logger log = Logger.getLogger("zuch.service.ZuchSessionListener");

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.warning("A SESSION HAS BEEN CRETAED...");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.warning("A SESSION HAS BEEN DESTROYED...");
    }
    
}
