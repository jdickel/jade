//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.10.09 um 03:42:30 PM CEST 
//


package com.sos.jade.converter.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r UnauthenticatedProxyType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="UnauthenticatedProxyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}BasicConnection"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnauthenticatedProxyType", propOrder = {
    "basicConnection"
})
public class UnauthenticatedProxyType {

    @XmlElement(name = "BasicConnection", required = true)
    protected BasicConnectionType basicConnection;

    /**
     * Ruft den Wert der basicConnection-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BasicConnectionType }
     *     
     */
    public BasicConnectionType getBasicConnection() {
        return basicConnection;
    }

    /**
     * Legt den Wert der basicConnection-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BasicConnectionType }
     *     
     */
    public void setBasicConnection(BasicConnectionType value) {
        this.basicConnection = value;
    }

}
