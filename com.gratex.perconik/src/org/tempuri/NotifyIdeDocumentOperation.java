
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.gratex.perconik.iactivitysvc.IdeDocumentOperationDto;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventDto" type="{http://www.gratex.com/PerConIk/IActivitySvc}IdeDocumentOperationDto" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "eventDto"
})
@XmlRootElement(name = "NotifyIdeDocumentOperation")
public class NotifyIdeDocumentOperation {

    protected IdeDocumentOperationDto eventDto;

    /**
     * Gets the value of the eventDto property.
     * 
     * @return
     *     possible object is
     *     {@link IdeDocumentOperationDto }
     *     
     */
    public IdeDocumentOperationDto getEventDto() {
        return eventDto;
    }

    /**
     * Sets the value of the eventDto property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdeDocumentOperationDto }
     *     
     */
    public void setEventDto(IdeDocumentOperationDto value) {
        this.eventDto = value;
    }

}
