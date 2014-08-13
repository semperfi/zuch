/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.backing;

import java.util.Arrays;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import zuch.command.Cmd;
import zuch.command.CmdParser;

/**
 *
 * @author florent
 */
@Named(value = "terminal")
@ApplicationScoped
public class Terminal {

    /**
     * Creates a new instance of Terminal
     */
    
    @Inject CmdParser cmdParser;
    
    public Terminal() {
    }
    
    public String handleCommand(String command, String[] params) {
        
        Cmd cmd = new Cmd(command, Arrays.asList(params));
        return cmdParser.parse(command, cmd);
    }
    
}
