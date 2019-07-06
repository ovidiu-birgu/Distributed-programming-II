//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.01.20 at 05:59:54 PM CET 
//


package it.polito.dp2.RNS.sol3.service.jaxb.rnssystem;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element name="gate" type="{}Gate" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="roadSegment" type="{}RoadSegment" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="parkingArea" type="{}ParkingArea" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="vehicle" type="{}Vehicle" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="road" type="{}Road" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="service" type="{}Service" maxOccurs="unbounded" minOccurs="0"/>
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
    "gate",
    "roadSegment",
    "parkingArea",
    "vehicle",
    "road",
    "service"
})
@XmlRootElement(name = "rns")
public class Rns {

    protected List<Gate> gate;
    protected List<RoadSegment> roadSegment;
    protected List<ParkingArea> parkingArea;
    protected List<Vehicle> vehicle;
    protected List<Road> road;
    protected List<Service> service;

    /**
     * Gets the value of the gate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Gate }
     * 
     * 
     */
    public List<Gate> getGate() {
        if (gate == null) {
            gate = new ArrayList<Gate>();
        }
        return this.gate;
    }

    /**
     * Gets the value of the roadSegment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roadSegment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoadSegment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RoadSegment }
     * 
     * 
     */
    public List<RoadSegment> getRoadSegment() {
        if (roadSegment == null) {
            roadSegment = new ArrayList<RoadSegment>();
        }
        return this.roadSegment;
    }

    /**
     * Gets the value of the parkingArea property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parkingArea property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParkingArea().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParkingArea }
     * 
     * 
     */
    public List<ParkingArea> getParkingArea() {
        if (parkingArea == null) {
            parkingArea = new ArrayList<ParkingArea>();
        }
        return this.parkingArea;
    }

    /**
     * Gets the value of the vehicle property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vehicle property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVehicle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Vehicle }
     * 
     * 
     */
    public List<Vehicle> getVehicle() {
        if (vehicle == null) {
            vehicle = new ArrayList<Vehicle>();
        }
        return this.vehicle;
    }

    /**
     * Gets the value of the road property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the road property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoad().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Road }
     * 
     * 
     */
    public List<Road> getRoad() {
        if (road == null) {
            road = new ArrayList<Road>();
        }
        return this.road;
    }

    /**
     * Gets the value of the service property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the service property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getService().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Service }
     * 
     * 
     */
    public List<Service> getService() {
        if (service == null) {
            service = new ArrayList<Service>();
        }
        return this.service;
    }

}
