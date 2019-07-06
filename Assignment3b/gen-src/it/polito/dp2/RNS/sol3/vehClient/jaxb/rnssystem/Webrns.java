
package it.polito.dp2.RNS.sol3.vehClient.jaxb.rnssystem;

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
 *         &lt;element name="places" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="gates" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="roadSegments" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="parkingAreas" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="connections" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="vehicles" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
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
    "places",
    "gates",
    "roadSegments",
    "parkingAreas",
    "connections",
    "vehicles"
})
@XmlRootElement(name = "webrns")
public class Webrns {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String self;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String places;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String gates;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String roadSegments;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String parkingAreas;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String connections;
    @XmlElement(required = true)
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
     * Gets the value of the places property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlaces() {
        return places;
    }

    /**
     * Sets the value of the places property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlaces(String value) {
        this.places = value;
    }

    /**
     * Gets the value of the gates property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGates() {
        return gates;
    }

    /**
     * Sets the value of the gates property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGates(String value) {
        this.gates = value;
    }

    /**
     * Gets the value of the roadSegments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoadSegments() {
        return roadSegments;
    }

    /**
     * Sets the value of the roadSegments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoadSegments(String value) {
        this.roadSegments = value;
    }

    /**
     * Gets the value of the parkingAreas property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParkingAreas() {
        return parkingAreas;
    }

    /**
     * Sets the value of the parkingAreas property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParkingAreas(String value) {
        this.parkingAreas = value;
    }

    /**
     * Gets the value of the connections property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnections() {
        return connections;
    }

    /**
     * Sets the value of the connections property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnections(String value) {
        this.connections = value;
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
