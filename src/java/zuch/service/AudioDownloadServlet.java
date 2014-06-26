/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.service;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

/**
 *
 * @author florent
 */
@WebServlet(name = "AudioDownloadServlet", urlPatterns = {"/download"})
public class AudioDownloadServlet extends HttpServlet {
    
    @Inject AudioManagerLocal audioManager;

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
        
            System.out.printf("METHOD DOWNLOAD SERVLET processRequest() CALLED %s" + 
                " ON THREAD [%s]\n", getClass().getSimpleName(), 
            Thread.currentThread().getName());

        
            final int BYTES = 4 * 1024;
            int length = 0;
        
            String id = request.getParameter("id");
            String token = request.getParameter("token");
            
            Audio audio;
            try {
                audio = audioManager.getAudio(Long.valueOf(id));
                String audioFileName = "";
                if(audio != null){
                    inputStream = new ByteArrayInputStream(audio.getContent().getContent());
                    audioFileName = audio.getId3().getTitle();
                    
                    String attachement = "attachment; filename=\"" 
                        + audio.getId3().getTitle()
                        + ".mp3\"";
                    int mp3length = audio.getContent().getContent().length;

                    response.setContentType("audio/mpeg");
                    response.setHeader( "Content-Disposition", attachement );
                    response.setContentLength(mp3length);

                     Logger.getLogger(AudioPlayerServlet.class.getName()).info("BEGINING STREAMING...");
                     byte[] bbuf = new byte[BYTES];
                     while (((length = inputStream.read(bbuf)) != -1)){
                     if(outputStream != null){
                               outputStream.write(bbuf,0,length);

                            }  
                        }


                }
     
            } catch (AudioNotFound ex) {
                Logger.getLogger(AudioDownloadServlet.class.getName()).log(Level.SEVERE, null, ex);
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
