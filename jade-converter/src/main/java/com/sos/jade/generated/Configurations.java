//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.08.25 at 03:34:05 PM MESZ 
//


package com.sos.jade.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element ref="{}Fragments"/>
 *         &lt;element ref="{}Profiles"/>
 *         &lt;element ref="{}General" minOccurs="0"/>
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
    "fragments",
    "profiles",
    "general"
})
@XmlRootElement(name = "Configurations")
public class Configurations {

    @XmlElement(name = "Fragments", required = true)
    protected Fragments fragments;
    @XmlElement(name = "Profiles", required = true)
    protected Profiles profiles;
    @XmlElement(name = "General")
    protected General general;

    /**
     * Gets the value of the fragments property.
     * 
     * @return
     *     possible object is
     *     {@link Fragments }
     *     
     */
    public Fragments getFragments() {
        return fragments;
    }

    /**
     * Sets the value of the fragments property.
     * 
     * @param value
     *     allowed object is
     *     {@link Fragments }
     *     
     */
    public void setFragments(Fragments value) {
        this.fragments = value;
    }

    /**
     * Gets the value of the profiles property.
     * 
     * @return
     *     possible object is
     *     {@link Profiles }
     *     
     */
    public Profiles getProfiles() {
        return profiles;
    }

    /**
     * Sets the value of the profiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link Profiles }
     *     
     */
    public void setProfiles(Profiles value) {
        this.profiles = value;
    }

    /**
     * Gets the value of the general property.
     * 
     * @return
     *     possible object is
     *     {@link General }
     *     
     */
    public General getGeneral() {
        return general;
    }

    /**
     * Sets the value of the general property.
     * 
     * @param value
     *     allowed object is
     *     {@link General }
     *     
     */
    public void setGeneral(General value) {
        this.general = value;
    }

}
