/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;


/**
 *
 * @author florent
 */

@Interceptor
@PerformanceMonitor
public class PerformanceInterceptor {
    
    @AroundInvoke 
    public Object monitor(InvocationContext ctx) throws Exception {
        long start = new Date().getTime();
        try {
            return ctx.proceed(); 
        } finally {
            long elapsed = new Date().getTime() - start;
            String method = ctx.getMethod().getName();
            String msg =  method + ": ELAPSED TIME: {1}";
            Logger.getLogger(PerformanceInterceptor.class.getName()).log(Level.INFO, 
                    msg, elapsed);
        }
    }
}
