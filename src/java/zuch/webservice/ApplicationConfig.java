/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.webservice;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;

/**
 *
 * @author florent
 */
@ApplicationPath("rs")
public class ApplicationConfig extends Application{
    
    private final Set<Class<?>> classes;
    
    public ApplicationConfig(){
        HashSet<Class<?>> c = new HashSet<>();
        c.add(MoxyJsonFeature.class);
        c.add(AudioRestService.class);
        classes = Collections.unmodifiableSet(c);
        
        
    }
    
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
    
}
