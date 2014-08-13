/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.model;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
     
    private String track;
    private String artist;
    private String title;
    private String album;
    private String audioYear;
    private String genre;
    private String comment;
     
    private String footPrint;
    
    private boolean artWork;
    
    @OneToOne(cascade = CascadeType.ALL)
    private AlbumImage image;
    
    @Version
    private Integer version;

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

    public String getFootPrint() {
        return footPrint;
    }

    public void setFootPrint(String footPrint) {
        this.footPrint = footPrint;
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

    public AlbumImage getImage() {
        return image;
    }

    public void setImage(AlbumImage image) {
        this.image = image;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (footPrint != null ? footPrint.hashCode() : 0);
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
