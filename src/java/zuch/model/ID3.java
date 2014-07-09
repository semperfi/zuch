/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.model;

import java.io.Serializable;
import javax.persistence.Entity;

import javax.persistence.Id;

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
    
    /*
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    */
    
    
    private String track;
    private String artist;
    private String title;
    private String album;
    private String audioYear;
    private String genre;
    private String comment;
    
    @Id
    private String footPrint;
    
    @Version
    private Integer version;
    

    
    
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
        if ((this.footPrint == null && other.footPrint != null) 
                || (this.footPrint != null && !this.footPrint.equals(other.footPrint))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "zuch.model.ID3[ id=" + footPrint + " ]";
    }
    
}
