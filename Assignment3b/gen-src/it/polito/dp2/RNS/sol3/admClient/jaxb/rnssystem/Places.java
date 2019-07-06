
package it.polito.dp2.RNS.sol3.admClient.jaxb.rnssystem;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="totalPages" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="page" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="next" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="webplace" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="self" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                   &lt;element name="gate" type="{}Gate" minOccurs="0"/>
 *                   &lt;element name="roadSegment" type="{}RoadSegment" minOccurs="0"/>
 *                   &lt;element name="parkingArea" type="{}ParkingArea" minOccurs="0"/>
 *                   &lt;element name="vehicles" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "totalPages",
    "page",
    "next",
    "webplace"
})
@XmlRootElement(name = "places")
public class Places {

    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger totalPages;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger page;
    @XmlSchemaType(name = "anyURI")
    protected String next;
    @XmlElement(nillable = true)
    protected List<Places.Webplace> webplace;

    /**
     * Gets the value of the totalPages property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalPages() {
        return totalPages;
    }

    /**
     * Sets the value of the totalPages property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalPages(BigInteger value) {
        this.totalPages = value;
    }

    /**
     * Gets the value of the page property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPage(BigInteger value) {
        this.page = value;
    }

    /**
     * Gets the value of the next property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNext() {
        return next;
    }

    /**
     * Sets the value of the next property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNext(String value) {
        this.next = value;
    }

    /**
     * Gets the value of the webplace property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the webplace property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWebplace().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Places.Webplace }
     * 
     * 
     */
    public List<Places.Webplace> getWebplace() {
        if (webplace == null) {
            webplace = new ArrayList<Places.Webplace>();
        }
        return this.webplace;
    }


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
    public static class Webplace {

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

}
