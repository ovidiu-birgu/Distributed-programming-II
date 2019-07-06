
package it.polito.dp2.RNS.sol3.admClient.jaxb.rnssystem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Gate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Gate">
 *   &lt;complexContent>
 *     &lt;extension base="{}Place">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{}GateType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Gate")
public class Gate
    extends Place
{

    @XmlAttribute(name = "type", required = true)
    protected GateType type;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link GateType }
     *     
     */
    public GateType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link GateType }
     *     
     */
    public void setType(GateType value) {
        this.type = value;
    }

}
