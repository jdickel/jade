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
 *         &lt;element ref="{}CheckSteadyStateInterval" minOccurs="0"/>
 *         &lt;element ref="{}CheckSteadyStateCount" minOccurs="0"/>
 *         &lt;element ref="{}CheckSteadyStateErrorState" minOccurs="0"/>
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
    "checkSteadyStateInterval",
    "checkSteadyStateCount",
    "checkSteadyStateErrorState"
})
@XmlRootElement(name = "CheckSteadyState")
public class CheckSteadyState {

    @XmlElement(name = "CheckSteadyStateInterval", defaultValue = "1")
    protected Integer checkSteadyStateInterval;
    @XmlElement(name = "CheckSteadyStateCount", defaultValue = "30")
    protected Integer checkSteadyStateCount;
    @XmlElement(name = "CheckSteadyStateErrorState")
    protected String checkSteadyStateErrorState;

    /**
     * Gets the value of the checkSteadyStateInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCheckSteadyStateInterval() {
        return checkSteadyStateInterval;
    }

    /**
     * Sets the value of the checkSteadyStateInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCheckSteadyStateInterval(Integer value) {
        this.checkSteadyStateInterval = value;
    }

    /**
     * Gets the value of the checkSteadyStateCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCheckSteadyStateCount() {
        return checkSteadyStateCount;
    }

    /**
     * Sets the value of the checkSteadyStateCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCheckSteadyStateCount(Integer value) {
        this.checkSteadyStateCount = value;
    }

    /**
     * Gets the value of the checkSteadyStateErrorState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckSteadyStateErrorState() {
        return checkSteadyStateErrorState;
    }

    /**
     * Sets the value of the checkSteadyStateErrorState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckSteadyStateErrorState(String value) {
        this.checkSteadyStateErrorState = value;
    }

}
