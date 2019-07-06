
package it.polito.dp2.RNS.sol3.vehClient.jaxb.rnssystem;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VehicleState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="VehicleState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PARKED"/>
 *     &lt;enumeration value="IN_TRANSIT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "VehicleState")
@XmlEnum
public enum VehicleState {

    PARKED,
    IN_TRANSIT;

    public String value() {
        return name();
    }

    public static VehicleState fromValue(String v) {
        return valueOf(v);
    }

}
