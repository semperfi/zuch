/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import zuch.exception.AudioNotFound;
import zuch.model.Audio;
import zuch.model.PlayTokens;
import zuch.model.ZConstants;

/**
 *
 * @author florent
 */
@WebServlet(name = "AudioSamplePlayerServlet", urlPatterns = {"/zuchsampleplayer"})
public class AudioSamplePlayerServlet extends HttpServlet {

   
    
    @Inject AudioManagerLocal audioManager;
    @Inject PlayTokens playTokens;
    @Inject ZFileManager fileManager;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
           ServletOutputStream outputStream = null ;
           InputStream inputStream = null;
        
            System.out.printf("METHOD processRequest() CALLED %s" + 
                " ON THREAD [%s]\n", getClass().getSimpleName(), 
            Thread.currentThread().getName());

        
           // final int BYTES = 8 * 1024;
            int length = 0;
        
            String id = request.getParameter("id");
            String token = request.getParameter("tk");
            
            if(playTokens.getToken().equals(token) ){
               
             try {
               
                //remove token before streaming to forbid downloading by right clicking
                playTokens.setToken("");
                
               response.reset();
               outputStream = response.getOutputStream();
                
               String msg = "TOKEN EXIST: " ;
               Logger.getLogger(AudioPlayerServlet.class.getName()).info(msg);
           
             
               Audio audio = audioManager.getAudio(Long.valueOf(id));
               
               String audioFileName = "";
               if(audio != null){
                  // inputStream = new ByteArrayInputStream(audio.getContent().getContentSample());
                   inputStream = fileManager.getSampleFileInputStream(audio.getId3().getFootPrint());
                   audioFileName = audio.getId3().getTitle();
                
               }
          
                if(inputStream == null){
                    return;
                }
                
                
               String attachement = "attachment; filename=\"zuch.mp3\"";
               int mp3length = inputStream.available();
                
                response.setContentType("audio/mpeg");
                response.setHeader( "Content-Disposition", attachement );
                response.setContentLength(mp3length);
                
               // InputStream in = context.getResourceAsStream("/" + originalFile);
                
                Logger.getLogger(AudioPlayerServlet.class.getName()).info("BEGINING STREAMING...");

                byte[] bbuf = new byte[ZConstants.STREAM_BUFFERSIZE];
                while (((length = inputStream.read(bbuf)) != -1)){
                  if(outputStream != null){
                       outputStream.write(bbuf,0,length);
                  }  
                   
                }
                Logger.getLogger(AudioPlayerServlet.class.getName()).info("STREAMING END...");
              
             
                
             }catch(IOException ex){
                 Logger.getLogger(AudioPlayerServlet.class.getName()).info("CONNECTION CLOSED");
                 // Logger.getLogger(Play.class.getName()).severe(ex.getMessage());
                // ex.printStackTrace();
             } catch (AudioNotFound ex) {
                   Logger.getLogger(AudioPlayerServlet.class.getName()).log(Level.SEVERE, null, "Audio not found");
             }finally{
             
                 
                 if(inputStream != null){
                        try{

                            inputStream.close();
                        }catch(IOException ex){
                            //Logger.getLogger(Play.class.getName()).severe(ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                 
             }
                
           }else{
               String msg = "TOKEN HAS BEEN DELETED: " ;
               Logger.getLogger(AudioPlayerServlet.class.getName()).info(msg);
           
           }
    
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
