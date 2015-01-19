package zuch.service;



import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import zuch.backing.JukeBoxBacking;
import zuch.event.EventService;
import zuch.exception.AudioNotFound;
import zuch.model.Audio;
import zuch.model.ZConstants;
import zuch.util.AudioUtils;

/**
 *
 * @author florent
 */
@WebServlet(name = "AudioPlayerServlet", urlPatterns = {"/zuchplayer"})
public class AudioPlayerServlet extends HttpServlet {
    
    static final Logger log = Logger.getLogger(AudioPlayerServlet.class.getName());
   
    @Inject AudioManagerLocal audioManager;
    @Inject JukeBoxBacking jukeBacking;
    @Inject PlayToken playToken;
    @Inject AudioUtils audioUtils;
    @Inject ZFileManager fileManager;
    @Inject EventService eventService;
    
   // @Inject
   // Event<Audio> startIndexingEvent;

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
         
        String range = "";
        range = request.getHeader("Range");
        log.info(String.format("HEADER RANGE %s",range));
        
        
        
        if( (range == null) || (range.isEmpty()) ){
            processNormalRequest(request, response);
        }else{
            processRangeRequest(request, response, range);
        }
        
    }
    
    
     private void processNormalRequest(HttpServletRequest request, HttpServletResponse response){
    
           log.info("PROCESSING NORMAL REQUEST...");
           
           log.warning(String.format("METHOD processNormalRequest() CALLED %s" + 
                " ON THREAD [%s]\n", getClass().getSimpleName(), 
                Thread.currentThread().getName()));
           
           InputStream inputStream = null;
           
           int length = 0;
        
            String id = request.getParameter("id");
            String token = request.getParameter("tk");
            
            if( playToken.getToken().equals(token) ){
                
                   //set to empty to denie downloading
                   playToken.setToken("");
                   response.reset();
                
                   try(ServletOutputStream outputStream = response.getOutputStream();) {
                                    
                       log.info("TOKEN EXIST: ");
                       
                       Audio audio = null;
                       
                       if(!id.equals("sample")){
                           audio = audioManager.getAudio(Long.valueOf(id));
                           inputStream = fileManager.getFileInputStream(audio.getFootPrint());
                           //fire selection audio event
                         //  startIndexingEvent.fire(audio);
                       }else{
                       
                           //audio = audioUtils.getDefaultAudioSample();
                           inputStream = audioUtils.getDefaultAudioSample();
                       }

                       String audioFileName = "";
                       if(inputStream != null){
                            if(audio != null){
                               /* fire audioPlayingEvent for play trace history
                               * this event is received by zuch.audit.PlayingHistory class 
                               */
                               eventService.getAudioPlayingEvent().fire(audio);
                               audioFileName = audio.getId3().getTitle();
                           }else{
                               audioFileName = "Zuch sample";
                           }
                                                   
                            String attachement = "attachement; filename=\"zuch.mp3\"";
                            String inline = "inline; filename=\"zuch.mp3\"";
                            int fileLength = inputStream.available();

                            response.setContentType("audio/mpeg");
                            response.setHeader( "Content-Disposition", inline );
                            response.setHeader("Accept-Ranges", "bytes");
                            response.setHeader("Connection", "keep-alive");
                            response.setContentLength(fileLength);

                            // InputStream in = context.getResourceAsStream("/" + originalFile);

                            Logger.getLogger(AudioPlayerServlet.class.getName()).info("BEGINING STREAMING...");

                            byte[] bbuf = new byte[ZConstants.STREAM_BUFFERSIZE];
                         
                            while (((length = inputStream.read(bbuf)) != -1)){
                               outputStream.write(bbuf,0,length);
                               outputStream.flush();

                             }

                            Logger.getLogger(AudioPlayerServlet.class.getName()).info("STREAMING END...");

                       }

                    
                     }catch(IOException ex){
                         log.warning(String.format("---CONNECTION CLOSED---: %s", ex.getMessage()));
                     }catch (AudioNotFound ex) {
                           log.severe(ex.getMessage());
                     }finally{
                 
                        if(inputStream != null){
                            try{
                                    inputStream.close();
                            }catch(IOException ex){

                                       log.severe(ex.getMessage());
                                   }
                            }

                    }
              
           }else{
               String msg = "TOKEN HAS BEEN DELETED: " ;
               Logger.getLogger(AudioPlayerServlet.class.getName()).info(msg);
           
           }
    
    }
    
    
   
    private void processRangeRequest(HttpServletRequest request, HttpServletResponse response,
            String rangeHeader){
        
           log.info(String.format("PROCESSING RANGE REQUEST..."));
           
           
           log.warning(String.format("METHOD processRangeRequest() CALLED %s" + 
                " ON THREAD [%s]\n", getClass().getSimpleName(), 
            Thread.currentThread().getName()));

           
           InputStream inputStream = null;
            
             String sValue[] = rangeHeader.split("-");
             String start = "";
             String end = "";
        
             if(sValue.length == 1){
                
                String tVal = sValue[0].split("-")[0].split("=")[1];
                start = tVal;
                end = "-1";
            
            }else if(sValue.length == 2){
                
                String tVal = sValue[0].split("-")[0].split("=")[1];
                start = tVal;
                end = sValue[1];
            }
    
           
            
            int reqStart = Integer.parseInt(start);
            int reqEnd = Integer.parseInt(end);
            
            log.info(String.format("REQUEST START: %d",reqStart));
            log.info(String.format("REQUEST END: %d",reqEnd));
        
           
            String id = request.getParameter("id");
            String token = request.getParameter("tk");
           // String rangeToken = request.getParameter("rtk");//may be used later
            
                   //prevent file downloading
                   //do not check token range request is not a new request 
                   playToken.setRangeToken("");
                   playToken.setToken("");
                   response.reset();
                
                   try(ServletOutputStream outputStream = response.getOutputStream();) {
               
                     
                       String msg = "TOKEN EXIST: " ;
                       Logger.getLogger(AudioPlayerServlet.class.getName()).info(msg);
                        
                       Audio audio = null;
                       
                       if(!id.equals("sample")){
                           audio = audioManager.getAudio(Long.valueOf(id));
                           inputStream = fileManager.getFileInputStream(audio.getFootPrint());
                        }else{
                           inputStream = audioUtils.getDefaultAudioSample();
                       }

                       String audioFileName = "";
                       if(inputStream != null){
                            if(audio != null){
                               /* fire audioPlayingEvent for play trace history
                               * this event is received by zuch.audit.PlayingHistory class 
                               */
                               eventService.getAudioPlayingEvent().fire(audio);
                               audioFileName = audio.getId3().getTitle();
                           }else{
                               audioFileName = "Zuch sample";
                           }
                           
                           
                            String attachement = "attachement; filename=\"zuch.mp3\"";
                            String inline = "inline; filename=\"zuch.mp3\"";
                            int fileLength = inputStream.available();
                            
                            
                            int totalSize = fileLength;
                            int rangeStart = 0;
                            int rangeEnd = 0;
                            
                            int contentLen = 0;
                            
                            
                            if( (reqStart == 0) && (reqEnd == -1) ){
                               rangeStart = reqStart;
                               rangeEnd = 0 + 1;   //+ 1 dont know why but if not will not working on IE
                               contentLen = totalSize - reqStart  ;
                            }else if( (reqStart == 0) && ( reqEnd > 0) ){
                               rangeStart = reqStart;
                               rangeEnd = reqEnd;
                               contentLen = reqEnd - reqStart  ;
                            }else if ((reqStart > 0) && (reqEnd == -1)){
                                
                               rangeStart = reqStart;
                               rangeEnd = reqStart + 1 ;
                               contentLen = totalSize - reqStart  ;
                            
                            }else if( (reqStart > 0) && (reqEnd > 0) ){
                                rangeStart = reqStart;
                                rangeEnd = reqEnd;
                                contentLen = reqEnd - reqStart;
                            }
                            
                            log.info(String.format("RANGE START: %d", rangeStart));
                            log.info(String.format("RANGE END: %d", rangeEnd));
                          
                            
                            String contentRange =  "bytes "+
                                     rangeStart + "-" + rangeEnd + "/" + totalSize ;
                            String lastModified;
                            String ETag;
                            if(audio != null){
                                lastModified = audio.getCreated().toString();
                                ETag = audio.getFootPrint();
                            }else{
                                lastModified = new Date().toString();
                                ETag = UUID.randomUUID().toString();
                            }
                            
                            
                            
                             response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                             response.setContentType("audio/mpeg");
                             response.setHeader( "Content-Disposition", inline );
                             response.setHeader("Accept-Ranges", "bytes");
                             response.setHeader("Content-Range",contentRange);
                             response.setHeader("Connection", "keep-alive");
                             response.setContentLength(contentLen);
                             response.setHeader("ETag", ETag);
                             response.setDateHeader("Date", new Date().getTime());
                             response.setHeader("Last-Modified",lastModified);
                            

                            // InputStream in = context.getResourceAsStream("/" + originalFile);

                            log.info("BEGINING STREAMING...");
                            
                            //final int BUFFERSIZE = 8 * 1024 * 1024;
                            int length;

                            byte[] bbuf = new byte[ZConstants.STREAM_BUFFERSIZE];
                            
                            inputStream.skip(reqStart);
                            while (((length = inputStream.read(bbuf)) != -1)){
                               outputStream.write(bbuf,0,length);
                               outputStream.flush();

                             }

                            log.info("STREAMING END...");

                       }

                    
                     }catch(IOException ex){
                         log.warning(String.format("---RANGE RQ CONNECTION CLOSED---"));
                     } catch (AudioNotFound ex) {
                           log.warning("Audio not found");
                     }finally{
                 
                         if(inputStream != null){
                               try{

                                   inputStream.close();
                               }catch(IOException ex){
                                   log.severe(ex.getMessage());
                               }
                           }
          
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
