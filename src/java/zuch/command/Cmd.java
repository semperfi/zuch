/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.command;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author florent
 */
public class Cmd {
    
    private String command;
    private List<String> parameters;

    public Cmd(String cmd,List<String> params ) {
        this.parameters = params;
        this.command = cmd;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getParams() {
        return parameters;
    }

    public void setParams(List<String> params) {
        this.parameters = params;
    }
    
    
    
}
