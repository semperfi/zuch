/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.command;


import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import zuch.qualifier.CmdCount;
import zuch.qualifier.CmdHelp;

/**
 *
 * @author florent
 */
@Stateless
public class CmdParser {

    @Inject @CmdCount CmdExecutor countCmd;
    @Inject @CmdHelp CmdExecutor helpCmd;
    
    HashMap<String,CmdExecutor> commands;
    
    @PostConstruct
    public void init(){
        commands = new HashMap<>() ;
        commands.put("count",countCmd);
    }
   
    
    
    
    public String parse(String cmdString, Cmd cmd ){
        String result;
        
        if(commands.containsKey(cmdString)){
            result = commands.get(cmdString).execute(cmd);
            //result = countCmd.execute(cmd);
        }else{
            result = new StringBuilder().append("command ")
                    .append("'")
                    .append(cmd.getCommand())
                    .append("'")
                    .append(" not found.").toString();
        }
        
        return result;
    }
    
    
}
