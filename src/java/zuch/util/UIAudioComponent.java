/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.util;

import java.io.IOException;
import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

/**
 *
 * @author florent
 */
@FacesComponent("UIAudioComponent")
public class UIAudioComponent extends UINamingContainer {
    
    private static final String ELEMENT_ID = "media-player";
    private static final String ATTRIBUTE_AUTOPLAY = "autoplay";
    private static final String ATTRIBUTE_LOOP = "loop";
    private static final String ATTRIBUTE_MUTED = "muted";
    private static final String ATTRIBUTE_CONTROLS = "controls";
    private static final String ATTRIBUTE_POSTER = "poster";
    private static final String ATTRIBUTE_WIDTH = "width";
    private static final String ATTRIBUTE_HEIGHT = "height";
    
    public String getElementId() {
      return ELEMENT_ID;
   }
    
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        super.encodeBegin(context);
        UIComponent element = findMediaElement();
        addAttributeIfTrue(element, ATTRIBUTE_AUTOPLAY);
        addAttributeIfTrue(element, ATTRIBUTE_LOOP);
        addAttributeIfTrue(element, ATTRIBUTE_MUTED);
        addAttributeIfTrue(element, ATTRIBUTE_CONTROLS);
        addAttributeIfNotNull(element, ATTRIBUTE_POSTER);
        addAttributeIfNotNull(element, ATTRIBUTE_WIDTH);
        addAttributeIfNotNull(element, ATTRIBUTE_HEIGHT);
    }
    
    private void addAttributeIfNotNull(UIComponent component, String attributeName) {
        Object attributeValue = getAttribute(attributeName);
        if (attributeValue != null) {
            component.getPassThroughAttributes().put(attributeName, attributeValue);
        }
    }
    
    private void addAttributeIfTrue(UIComponent component, String attributeName) {
        if (isAttributeTrue(attributeName)) {
        component.getPassThroughAttributes().put(attributeName, attributeName);
        }
      }
    
     
   
    private UIComponent findMediaElement() throws IOException {
        UIComponent element = findComponent(getElementId());
        if (element == null) {
        throw new IOException("Media element with ID "
        + getElementId() + " could not be found");
        }
        return element;
}
    
   
    
 private Object getAttribute(String name) {
    ValueExpression ve = getValueExpression(name);
    if (ve != null) {
    // Attribute is a value expression
    return ve.getValue(getFacesContext().getELContext());
    } else if (getAttributes().containsKey(name)) {
    // Attribute is a fixed value
    return getAttributes().get(name);
    } else {
    // Attribute doesn't exist
    return null;
    }
}   
 
 
 private boolean isAttributeTrue(String attributeName) {
    boolean isBoolean = getAttribute(attributeName) instanceof java.lang.Boolean;
    boolean isTrue = ((boolean) getAttribute(attributeName)) == Boolean.TRUE;
    return isBoolean && isTrue;
}
    

    
}
