/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.aop;

import java.io.Serializable;
import java.time.Instant;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import zuch.qualifier.PerformanceMonitor;


/**
 *
 * @author florent
 */

@Interceptor
@PerformanceMonitor
public class PerformanceInterceptor implements Serializable{
    
    static final Logger log = Logger.getLogger("zuch.aop.PerformanceInterceptor");
    
    @AroundInvoke 
    public Object monitor(InvocationContext ctx) throws Exception {
        //long start = new Date().getTime();
        String enterMessage = "-["+ctx.getTarget().toString() 
                +"] ENTER "+ctx.getMethod().getName();
        log.info(enterMessage);
        
        Instant start = Instant.now();
        try {
            return ctx.proceed(); 
        } finally {
            //long elapsed = new Date().getTime() - start;
            long elapsed = Instant.now().getNano() - start.getNano();
            String clazz = ctx.getTarget().getClass().getName();
            String method = ctx.getMethod().getName();
            String msg = "  [" +clazz +"."+ method + "]"+": ELAPSED TIME: %d ms";
            log.info( String.format(msg, elapsed / 1000000));
            
            String exitMessage = "-["+ctx.getTarget().toString() 
                +"] EXIT "+ctx.getMethod().getName();
            log.info(exitMessage);
        }
    }
}
