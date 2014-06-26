/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.primefaces.model.UploadedFile;
import zuch.backing.AudioAddBacking;

/**
 *
 * @author florent
 */
@FacesValidator("zuch.util.AudioFileValidator")
public class AudioFileValidator  implements Validator{

    @Override
    public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
       Logger.getLogger(AudioAddBacking.class.getName()).info("CALLING VALIDATING AUDIO...");
      
        UploadedFile uploadedFile = (UploadedFile)value;
            
       List<FacesMessage> msgs = new ArrayList<>();
      
      if(uploadedFile != null){
        if (uploadedFile.getSize() > (30*1048576) ) {
            msgs.add(new FacesMessage("file size must not exceed 30 MB"));
            Logger.getLogger(AudioAddBacking.class.getName()).info("file size must not exceed 30 MB");
        
        }
        if ( ! ( "audio/mpeg".equals(uploadedFile.getContentType()) ||
                "audio/mp3".equals(uploadedFile.getContentType()) ) ) {
             msgs.add(new FacesMessage("File format must be mp3"));
             Logger.getLogger(AudioAddBacking.class.getName()).info("File format must be mp3");
        }
        if (! msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
      }
    }
    
        
    
}
