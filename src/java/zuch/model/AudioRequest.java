/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author florent
 */
@Entity
public class AudioRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Version
    private Integer version;
    
    @ManyToOne
    private Audio requestedAudio;
    
    @ManyToOne
    private ZUser requester;
    
    private long requestTime;
    
    private long responseTime;
    
    private int lendLifeTime;  //number of days
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private AudioRequestStatus status;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Audio getRequestedAudio() {
        return requestedAudio;
    }

    public void setRequestedAudio(Audio requestedAudio) {
        this.requestedAudio = requestedAudio;
    }

    public ZUser getRequester() {
        return requester;
    }

    public void setRequester(ZUser requester) {
        this.requester = requester;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public int getLendLifeTime() {
        return lendLifeTime;
    }

    public void setLendLifeTime(int lendLifeTime) {
        this.lendLifeTime = lendLifeTime;
    }

   
    
    public AudioRequestStatus getStatus() {
        return status;
    }

    public void setStatus(AudioRequestStatus status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

   
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AudioRequest)) {
            return false;
        }
        AudioRequest other = (AudioRequest) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "zuch.model.AudioRequest[ id=" + id + " ]";
    }
    
}
