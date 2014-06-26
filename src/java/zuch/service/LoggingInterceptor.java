/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.util.logging.Logger;
import javax.interceptor.AroundConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 *
 * @author florent
 */
public class LoggingInterceptor {
    
    static final Logger log = Logger.getLogger("zuch.service.LoggingInterceptor");
    
    @AroundConstruct
    private void init(InvocationContext ic) throws Exception {
        log.fine("Entering constructor");
    try {
    ic.proceed();
    } finally {
        log.fine("Exiting constructor");
    }
    }
    
    @AroundInvoke
    private Object logMethod(InvocationContext ic) throws Exception {
                
        String enterMessage = "["+ic.getTarget().toString() 
                +"] ENTER "+ic.getMethod().getName();
        log.info(enterMessage);
        
    try {
        return ic.proceed();
    } finally {
        String exitMessage = "["+ic.getTarget().toString() 
                +"] EXIT "+ic.getMethod().getName();
        log.info(exitMessage);
    }
    }
    
}
