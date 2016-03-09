//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, v2.2.4
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2012.08.21 at 08:04:29 PM CEST
//

package com.sos.DataExchange.jaxb.configuration;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/** <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.sos-berlin.com/schema/jade_configuration_v1.0}validity" minOccurs="0"/>
 *         &lt;element ref="{http://www.sos-berlin.com/schema/jade_configuration_v1.0}documentation" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.w3.org/2001/XInclude}include" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{http://www.sos-berlin.com/schema/jade_configuration_v1.0}profile" minOccurs="0"/>
 *           &lt;element ref="{http://www.sos-berlin.com/schema/jade_configuration_v1.0}profiles" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre> */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "validity", "documentation", "includeOrProfileOrProfiles" })
@XmlRootElement(name = "configuration")
public class ConfigurationElement {

    protected ValidityElement validity;
    protected JADEDocumentation documentation;
    @XmlElements({ @XmlElement(name = "include", namespace = "http://www.w3.org/2001/XInclude", type = Include.class),
            @XmlElement(name = "profile", type = JADEProfile.class), @XmlElement(name = "profiles", type = JADEProfiles.class) })
    protected List<Object> includeOrProfileOrProfiles = new Vector<Object>();

    /** Gets the value of the validity property.
     * 
     * @return possible object is {@link ValidityElement } */
    public ValidityElement getValidity() {
        return validity;
    }

    /** Sets the value of the validity property.
     * 
     * @param value allowed object is {@link ValidityElement } */
    public void setValidity(final ValidityElement value) {
        validity = value;
    }

    /** Gets the value of the documentation property.
     * 
     * @return possible object is {@link JADEDocumentation } */
    public JADEDocumentation getDocumentation() {
        return documentation;
    }

    /** Sets the value of the documentation property.
     * 
     * @param value allowed object is {@link JADEDocumentation } */
    public void setDocumentation(final JADEDocumentation value) {
        documentation = value;
    }

    /** Gets the value of the includeOrProfileOrProfiles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the includeOrProfileOrProfiles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getIncludeOrProfileOrProfiles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Include }
     * {@link JADEProfile } {@link JADEProfiles } */
    public List<Object> getIncludeOrProfileOrProfiles() {
        if (includeOrProfileOrProfiles == null) {
            includeOrProfileOrProfiles = new Vector<Object>();
        }
        return includeOrProfileOrProfiles;
    }

}
