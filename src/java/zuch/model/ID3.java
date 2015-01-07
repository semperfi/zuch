/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author florent
 */
@Entity
//@Table(uniqueConstraints=@UniqueConstraint(columnNames={"FOOTPRINT"}))
@XmlRootElement
public class ID3 implements Serializable {
    private static final long serialVersionUID = 1L;
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Version
    private Integer version;
        
    @Column(name = "CREATED")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date created = new Date();

     
    private String track;
    private String artist;
    private String title;
    private String album;
    private String audioYear;
    private String genre;
    private String comment;
     
     
    private boolean artWork;
    
    private String artWorkHash;
    
    private String artWorkMimeType;
    
    private String artWorkExt;
    
   
    public ID3() {
        artWork = false;
    }
    

    
    
    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAudioYear() {
        return audioYear;
    }

    public void setAudioYear(String audioYear) {
        this.audioYear = audioYear;
    }

    public boolean hasArtWork() {
        return artWork;
    }

    public void setArtWork(boolean artWork) {
        this.artWork = artWork;
    }

    public String getArtWorkHash() {
        return artWorkHash;
    }

    public void setArtWorkHash(String artWorkHash) {
        this.artWorkHash = artWorkHash;
    }

    public String getArtWorkMimeType() {
        return artWorkMimeType;
    }

    public void setArtWorkMimeType(String artWorkMimeType) {
        this.artWorkMimeType = artWorkMimeType;
    }

    

    

    

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

   

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtWorkExt() {
        return artWorkExt;
    }

    public void setArtWorkExt(String artWorkExt) {
        this.artWorkExt = artWorkExt;
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
        if (!(object instanceof ID3)) {
            return false;
        }
        ID3 other = (ID3) object;
        if ((this.id == null && other.id != null) 
                || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "zuch.model.ID3[ id=" + id + " ]";
    }
    
}
