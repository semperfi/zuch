/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.embeddable.EJBContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author florent
 */
public class EncryptionServiceTest {
    
   // private static final String JNDI_NAME_ENCR = "java:global/Zuch/EncryptionService";
    private static EJBContainer ejbContainer;
    EncryptionServiceLocal encryptionSvcFacade;
    
    public EncryptionServiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
         Map<String, Object> properties = new HashMap<>();
         properties.put("org.glassfish.ejb.embedded.glassfish.configuration.file", "test-resources/domain.xml");
         ejbContainer = javax.ejb.embeddable.EJBContainer.createEJBContainer(properties);
    }
    
    @AfterClass
    public static void tearDownClass() {
      if(ejbContainer != null){
          ejbContainer.close();
      }
        
    }
    
    @Before
    public void setUp() throws Exception {
        encryptionSvcFacade = 
               (EncryptionServiceLocal) ejbContainer.getContext().lookup("java:global/classes/EncryptionService");
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testHash() throws Exception {
        System.out.println("hash");
        
        String actResult = encryptionSvcFacade.hash("Hello world!");
        String expResult = "c0535e4be2b79ffd93291305436bf889314e4a3faec05ecffcbb7df31ad9e51a";
        assertEquals("ERROR IN HASH STRING...", expResult, actResult);
        
    }
    
    @Test
    public void testCompare() throws Exception {
        boolean expResult = encryptionSvcFacade.compare("hello", "hello");
        assertTrue("COMPARE IS NOT TRUE...", expResult);
    }
    
    @Test
    public void testStringToByteArray() throws Exception {
        byte[] expResult = "hello".getBytes("UTF-8");
        byte[] actResult = encryptionSvcFacade.stringToByteArray("hello");
        assertArrayEquals("STRING TO BYTE ERROR...", expResult, actResult);
    }
    
    @Test
    public void testByteArrayToHexString() throws Exception {
        String expResult = "68656c6c6f"; //hello hex string
        String actResult = encryptionSvcFacade.byteArrayToHexString("hello".getBytes("UTF-8"));
        assertEquals("BYTE ARRAY TO HEX STRING ERROR... ", expResult, actResult);
    
    }
    
}
