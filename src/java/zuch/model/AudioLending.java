/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.Version;

/**
 *
 * @author florent
 */
@Entity
public class AudioLending implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Version
    private Integer version;
    
    @Column(name = "CREATED")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date created = new Date();

    
    @ManyToOne
    private Audio audio;
    
    @OneToOne
    private ZUser lender;
    @OneToOne
    private ZUser tempOwner;
    
    @Enumerated(EnumType.STRING)
    private AudioLendStatus status;
    
    private long lendbegining;
    private long lendEnding;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public long getLendbegining() {
        return lendbegining;
    }

    public void setLendbegining(long lendbegining) {
        this.lendbegining = lendbegining;
    }

    public long getLendEnding() {
        return lendEnding;
    }

    public void setLendEnding(long lendEnding) {
        this.lendEnding = lendEnding;
    }

    public AudioLendStatus getStatus() {
        return status;
    }

    public void setStatus(AudioLendStatus status) {
        this.status = status;
    }

    

    public ZUser getLender() {
        return lender;
    }

    public void setLender(ZUser lender) {
        this.lender = lender;
    }

    public ZUser getTempOwner() {
        return tempOwner;
    }

    public void setTempOwner(ZUser tempOwner) {
        this.tempOwner = tempOwner;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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
        if (!(object instanceof AudioLending)) {
            return false;
        }
        AudioLending other = (AudioLending) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "zuch.model.AudioLend[ id=" + id + " ]";
    }
    
}
