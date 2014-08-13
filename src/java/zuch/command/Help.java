/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.command;

import javax.ejb.Stateless;
import zuch.qualifier.CmdHelp;

/**
 *
 * @author florent
 */
@Stateless
@CmdHelp
public class Help implements CmdExecutor{

 
    @Override
    public String execute(Cmd cmd) {
         return new StringBuilder().append("'")
                .append(cmd.getCommand())
                .append("'")
                .append(" has been executed!").toString();
    }
    
}
