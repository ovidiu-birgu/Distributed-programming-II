
package it.polito.dp2.RNS.sol3.admClient.jaxb.rnssystem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="self" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="gate" type="{}Gate" minOccurs="0"/>
 *         &lt;element name="roadSegment" type="{}RoadSegment" minOccurs="0"/>
 *         &lt;element name="parkingArea" type="{}ParkingArea" minOccurs="0"/>
 *         &lt;element name="vehicles" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
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
    "self",
    "gate",
    "roadSegment",
    "parkingArea",
    "vehicles"
})
@XmlRootElement(name = "webplace")
public class Webplace {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String self;
    protected Gate gate;
    protected RoadSegment roadSegment;
    protected ParkingArea parkingArea;
    @XmlSchemaType(name = "anyURI")
    protected String vehicles;

    /**
     * Gets the value of the self property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSelf() {
        return self;
    }

    /**
     * Sets the value of the self property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSelf(String value) {
        this.self = value;
    }

    /**
     * Gets the value of the gate property.
     * 
     * @return
     *     possible object is
     *     {@link Gate }
     *     
     */
    public Gate getGate() {
        return gate;
    }

    /**
     * Sets the value of the gate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gate }
     *     
     */
    public void setGate(Gate value) {
        this.gate = value;
    }

    /**
     * Gets the value of the roadSegment property.
     * 
     * @return
     *     possible object is
     *     {@link RoadSegment }
     *     
     */
    public RoadSegment getRoadSegment() {
        return roadSegment;
    }

    /**
     * Sets the value of the roadSegment property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoadSegment }
     *     
     */
    public void setRoadSegment(RoadSegment value) {
        this.roadSegment = value;
    }

    /**
     * Gets the value of the parkingArea property.
     * 
     * @return
     *     possible object is
     *     {@link ParkingArea }
     *     
     */
    public ParkingArea getParkingArea() {
        return parkingArea;
    }

    /**
     * Sets the value of the parkingArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParkingArea }
     *     
     */
    public void setParkingArea(ParkingArea value) {
        this.parkingArea = value;
    }

    /**
     * Gets the value of the vehicles property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicles() {
        return vehicles;
    }

    /**
     * Sets the value of the vehicles property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicles(String value) {
        this.vehicles = value;
    }

}
