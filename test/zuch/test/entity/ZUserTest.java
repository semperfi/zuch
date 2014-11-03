/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.test.entity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.validation.ConstraintViolationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import zuch.model.ZUser;

/**
 *
 * @author florent
 */
public class ZUserTest {
    
    private static final EntityManagerFactory emf = 
            Persistence.createEntityManagerFactory("ZuchTestPU");
    
    private EntityManager em;
    private EntityTransaction tx;
    
    @Before
    public void initEntityManager() throws Exception{
        em = emf.createEntityManager();
        tx = em.getTransaction();
    }
    
    @After
    public void closeEntityManager() throws Exception{
        if(em != null) em.close();
    }
    
    @Test
    public void shouldCreateUser() throws Exception{
        ZUser zuser = new ZUser();
        zuser.setId("Isco");
        zuser.setPassword("hgTyy86LKO");
        zuser.setFirstName("Francisco");
        zuser.setLastName("Alrajon");
        
        tx.begin();
        em.persist(zuser);
        tx.commit();
    
    }
    
    /*
    @Test(expected = ConstraintViolationException.class)
    public void shouldRaiseConstraintViolationCauseAlreadyExistUser() {

    }
    */
    
}
