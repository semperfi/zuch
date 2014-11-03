/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author florent
 */
@Entity
@XmlRootElement
@Table( uniqueConstraints = @UniqueConstraint( columnNames = {"ZUSER","ZROLE"}))
public class ZGroup implements Serializable {
   
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Version
    private Integer version;
    
    
    @ManyToOne
    @JoinColumn( name = "ZUSER")
    private ZUser zUser;
    
    @Column(name = "ZROLE")
    @Enumerated(EnumType.STRING)
    private ZGroupRole role;
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZUser getzUser() {
        return zUser;
    }

    public void setzUser(ZUser zUser) {
        this.zUser = zUser;
    }

    public ZGroupRole getRole() {
        return role;
    }

    public void setRole(ZGroupRole role) {
        this.role = role;
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
        if (!(object instanceof ZGroup)) {
            return false;
        }
        ZGroup other = (ZGroup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "zuch.model.ZGroup[ id=" + id + " ]";
    }
    
}
