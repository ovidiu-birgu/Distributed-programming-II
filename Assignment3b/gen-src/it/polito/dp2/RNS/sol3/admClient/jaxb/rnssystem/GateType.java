
package it.polito.dp2.RNS.sol3.admClient.jaxb.rnssystem;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GateType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GateType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="IN"/>
 *     &lt;enumeration value="OUT"/>
 *     &lt;enumeration value="INOUT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "GateType")
@XmlEnum
public enum GateType {

    IN,
    OUT,
    INOUT;

    public String value() {
        return name();
    }

    public static GateType fromValue(String v) {
        return valueOf(v);
    }

}
