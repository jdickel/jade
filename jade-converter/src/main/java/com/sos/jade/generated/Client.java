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
 *         &lt;element ref="{}SupplyingClient" minOccurs="0"/>
 *         &lt;element ref="{}ReceivingClient" minOccurs="0"/>
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
    "supplyingClient",
    "receivingClient"
})
@XmlRootElement(name = "Client")
public class Client {

    @XmlElement(name = "SupplyingClient")
    protected String supplyingClient;
    @XmlElement(name = "ReceivingClient")
    protected String receivingClient;

    /**
     * Gets the value of the supplyingClient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplyingClient() {
        return supplyingClient;
    }

    /**
     * Sets the value of the supplyingClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplyingClient(String value) {
        this.supplyingClient = value;
    }

    /**
     * Gets the value of the receivingClient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceivingClient() {
        return receivingClient;
    }

    /**
     * Sets the value of the receivingClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceivingClient(String value) {
        this.receivingClient = value;
    }

}
