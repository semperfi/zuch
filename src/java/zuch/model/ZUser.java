/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author florent
 */
@Entity
public class ZUser implements Serializable {
    private static final long serialVersionUID = 1L;
    
    
    @Id
    private String id;
    
    private String firstName;
    private String lastName;
    
    private String password;
    
    @Transient
    private String password2;
    
    @Version
    private Integer version;
        
    @Column(name = "CREATED")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date created = new Date();
    
    @OneToMany(mappedBy = "zUser", cascade = CascadeType.ALL)
    private List<ZGroup> groupList;
    
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Audio> jukeBox;
    
    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL)
    private List<AudioRequest> audioRequests;
    
    @OneToMany(mappedBy = "ratingAuthor")
    private List<Rating> ratings;
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlTransient
    public List<ZGroup> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<ZGroup> groupList) {
        this.groupList = groupList;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    @XmlTransient
    public List<Audio> getJukeBox() {
        return jukeBox;
    }

    public void setJukeBox(List<Audio> jukeBox) {
        this.jukeBox = jukeBox;
    }

    @XmlTransient
    public List<AudioRequest> getAudioRequests() {
        return audioRequests;
    }

    public void setAudioRequests(List<AudioRequest> audioRequests) {
        this.audioRequests = audioRequests;
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
        if (!(object instanceof ZUser)) {
            return false;
        }
        ZUser other = (ZUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "zuch.model.ZUser[ id=" + id + " ]";
    }
    
}
