/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.command;

import java.util.Arrays;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import zuch.qualifier.CmdCount;
import zuch.service.AudioManager;
import zuch.service.AudioManagerLocal;
import zuch.service.ZUserManager;
import zuch.service.ZUserManagerLocal;

/**
 *
 * @author florent
 */
@Stateless
@CmdCount
public class Count implements CmdExecutor{
    
    @Inject AudioManagerLocal audioManager;
    @Inject ZUserManagerLocal userManager;
    
    
    private final List<String> options = Arrays.asList("-u","-a");
    final String errorMsg = new StringBuilder().append("'")
                    .append("count")
                    .append("'")
                    .append(": usage 'count (-u or -a') ").toString();

    @Override
    public String execute(Cmd cmd) {
        
        String result = errorMsg;
        
        
        if(!cmd.getParams().isEmpty()){
            String param0 = cmd.getParams().get(0);
            if(options.contains(param0)){
               
                switch(param0){
                    case "-u":
                        long userCount = userManager.getZuchUserCount();
                        result = " sytem contains "+userCount+" users.";
                        break;
                    case "-a":
                        long audioCount = audioManager.getAllAudiosCount();
                        result = " sytem contains "+audioCount+" audios files.";
                        break;

                }

            }
        }
        
        
        return result;
    }
    
}
