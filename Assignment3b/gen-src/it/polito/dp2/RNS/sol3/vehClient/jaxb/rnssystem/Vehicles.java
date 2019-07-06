
package it.polito.dp2.RNS.sol3.vehClient.jaxb.rnssystem;

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
 *         &lt;element name="webvehicle" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="self" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                   &lt;element name="vehicle" type="{}Vehicle"/>
 *                   &lt;element ref="{}vehiclePath"/>
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
    "webvehicle"
})
@XmlRootElement(name = "vehicles")
public class Vehicles {

    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger totalPages;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger page;
    @XmlSchemaType(name = "anyURI")
    protected String next;
    @XmlElement(nillable = true)
    protected List<Vehicles.Webvehicle> webvehicle;

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
     * Gets the value of the webvehicle property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the webvehicle property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWebvehicle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Vehicles.Webvehicle }
     * 
     * 
     */
    public List<Vehicles.Webvehicle> getWebvehicle() {
        if (webvehicle == null) {
            webvehicle = new ArrayList<Vehicles.Webvehicle>();
        }
        return this.webvehicle;
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
     *         &lt;element name="vehicle" type="{}Vehicle"/>
     *         &lt;element ref="{}vehiclePath"/>
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
        "vehicle",
        "vehiclePath"
    })
    public static class Webvehicle {

        @XmlElement(required = true)
        @XmlSchemaType(name = "anyURI")
        protected String self;
        @XmlElement(required = true)
        protected Vehicle vehicle;
        @XmlElement(required = true)
        protected VehiclePath vehiclePath;

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
         * Gets the value of the vehicle property.
         * 
         * @return
         *     possible object is
         *     {@link Vehicle }
         *     
         */
        public Vehicle getVehicle() {
            return vehicle;
        }

        /**
         * Sets the value of the vehicle property.
         * 
         * @param value
         *     allowed object is
         *     {@link Vehicle }
         *     
         */
        public void setVehicle(Vehicle value) {
            this.vehicle = value;
        }

        /**
         * Gets the value of the vehiclePath property.
         * 
         * @return
         *     possible object is
         *     {@link VehiclePath }
         *     
         */
        public VehiclePath getVehiclePath() {
            return vehiclePath;
        }

        /**
         * Sets the value of the vehiclePath property.
         * 
         * @param value
         *     allowed object is
         *     {@link VehiclePath }
         *     
         */
        public void setVehiclePath(VehiclePath value) {
            this.vehiclePath = value;
        }

    }

}
