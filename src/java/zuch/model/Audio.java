/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author florent
 */
@Entity
@XmlRootElement
public class Audio implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Version
    private Integer version;
    
    private String uploadedName;
    private String comment;
    
    @Column(name = "AVG_RATING")
    private int avgRating;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private AudioStatus status;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private final Date created = new Date();
    
    @ManyToOne
    @NotNull
    private ZUser owner;
    
    @NotNull
    @Column(unique = true)
    private String footPrint;
   
    
    
    @OneToMany(mappedBy = "requestedAudio",  cascade = CascadeType.ALL)
    private List<AudioRequest> audioRequests;
    
    
    @OneToMany(mappedBy = "audio", cascade = CascadeType.ALL)
    private List<AudioLending> audioLendings;
    
    @OneToMany(mappedBy = "audio", cascade = CascadeType.ALL)
    private List<Rating> ratings;
    
    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private ID3 id3;
    
   // @OneToOne(cascade = CascadeType.ALL)
   // private AudioContent content;

    public Audio() {
        this.audioLendings = new ArrayList<>();
        this.audioRequests = new ArrayList<>();
    }

    public ZUser getOwner() {
        return owner;
    }

    public void setOwner(ZUser owner) {
        this.owner = owner;
    }

    public String getUploadedName() {
        return uploadedName;
    }

    public void setUploadedName(String uploadedName) {
        this.uploadedName = uploadedName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreated() {
        return created;
    }

    public int getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(int avgRating) {
        this.avgRating = avgRating;
    }

   

    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlTransient
    public List<AudioRequest> getAudioRequests() {
        return audioRequests;
    }

    public void setAudioRequests(List<AudioRequest> audioRequests) {
        this.audioRequests = audioRequests;
    }

    

    public ID3 getId3() {
        return id3;
    }

    public void setId3(ID3 id3) {
        this.id3 = id3;
    }

    public AudioStatus getStatus() {
        return status;
    }

    public void setStatus(AudioStatus status) {
        this.status = status;
    }

    @XmlTransient
    public List<AudioLending> getAudioLendings() {
        return audioLendings;
    }

    public void setAudioLendings(List<AudioLending> audioLendings) {
        this.audioLendings = audioLendings;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getFootPrint() {
        return footPrint;
    }

    public void setFootPrint(String footPrint) {
        this.footPrint = footPrint;
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
        if (!(object instanceof Audio)) {
            return false;
        }
        Audio other = (Audio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "zuch.model.Audio[ id=" + id + " ]";
    }
    
}
