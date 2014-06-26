/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.model;

import javax.ejb.Singleton;
import javax.ejb.Startup;




/**
 *
 * @author florent
 */
@Singleton
@Startup
public class PlayTokens {

    private String token;
    private String rangeToken;

    public PlayTokens() {
        token = "";
        rangeToken = "";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRangeToken() {
        return rangeToken;
    }

    public void setRangeToken(String rangeToken) {
        this.rangeToken = rangeToken;
    }
    
    
}
