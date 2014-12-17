/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;




/**
 *
 * @author florent
 */

@SessionScoped
public class PlayToken implements Serializable{

    private String token;
    private String rangeToken;

    public PlayToken() {
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
