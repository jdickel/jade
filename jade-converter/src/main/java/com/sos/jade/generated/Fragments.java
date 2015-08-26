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
 *         &lt;element ref="{}ProtocolFragments"/>
 *         &lt;element ref="{}AlternativeFragments" minOccurs="0"/>
 *         &lt;element ref="{}NotificationFragments" minOccurs="0"/>
 *         &lt;element ref="{}CredentialStoreFragments" minOccurs="0"/>
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
    "protocolFragments",
    "alternativeFragments",
    "notificationFragments",
    "credentialStoreFragments"
})
@XmlRootElement(name = "Fragments")
public class Fragments {

    @XmlElement(name = "ProtocolFragments", required = true)
    protected ProtocolFragments protocolFragments;
    @XmlElement(name = "AlternativeFragments")
    protected AlternativeFragments alternativeFragments;
    @XmlElement(name = "NotificationFragments")
    protected NotificationFragments notificationFragments;
    @XmlElement(name = "CredentialStoreFragments")
    protected CredentialStoreFragments credentialStoreFragments;

    /**
     * Gets the value of the protocolFragments property.
     * 
     * @return
     *     possible object is
     *     {@link ProtocolFragments }
     *     
     */
    public ProtocolFragments getProtocolFragments() {
        return protocolFragments;
    }

    /**
     * Sets the value of the protocolFragments property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProtocolFragments }
     *     
     */
    public void setProtocolFragments(ProtocolFragments value) {
        this.protocolFragments = value;
    }

    /**
     * Gets the value of the alternativeFragments property.
     * 
     * @return
     *     possible object is
     *     {@link AlternativeFragments }
     *     
     */
    public AlternativeFragments getAlternativeFragments() {
        return alternativeFragments;
    }

    /**
     * Sets the value of the alternativeFragments property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlternativeFragments }
     *     
     */
    public void setAlternativeFragments(AlternativeFragments value) {
        this.alternativeFragments = value;
    }

    /**
     * Gets the value of the notificationFragments property.
     * 
     * @return
     *     possible object is
     *     {@link NotificationFragments }
     *     
     */
    public NotificationFragments getNotificationFragments() {
        return notificationFragments;
    }

    /**
     * Sets the value of the notificationFragments property.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationFragments }
     *     
     */
    public void setNotificationFragments(NotificationFragments value) {
        this.notificationFragments = value;
    }

    /**
     * Gets the value of the credentialStoreFragments property.
     * 
     * @return
     *     possible object is
     *     {@link CredentialStoreFragments }
     *     
     */
    public CredentialStoreFragments getCredentialStoreFragments() {
        return credentialStoreFragments;
    }

    /**
     * Sets the value of the credentialStoreFragments property.
     * 
     * @param value
     *     allowed object is
     *     {@link CredentialStoreFragments }
     *     
     */
    public void setCredentialStoreFragments(CredentialStoreFragments value) {
        this.credentialStoreFragments = value;
    }

}
