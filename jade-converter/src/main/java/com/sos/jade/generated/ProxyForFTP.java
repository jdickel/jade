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
 *       &lt;choice>
 *         &lt;element ref="{}HTTPProxy"/>
 *         &lt;element ref="{}SOCKS4Proxy"/>
 *         &lt;element ref="{}SOCKS5Proxy"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "httpProxy",
    "socks4Proxy",
    "socks5Proxy"
})
@XmlRootElement(name = "ProxyForFTP")
public class ProxyForFTP {

    @XmlElement(name = "HTTPProxy")
    protected AuthenticatedProxyType httpProxy;
    @XmlElement(name = "SOCKS4Proxy")
    protected UnauthenticatedProxyType socks4Proxy;
    @XmlElement(name = "SOCKS5Proxy")
    protected AuthenticatedProxyType socks5Proxy;

    /**
     * Gets the value of the httpProxy property.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticatedProxyType }
     *     
     */
    public AuthenticatedProxyType getHTTPProxy() {
        return httpProxy;
    }

    /**
     * Sets the value of the httpProxy property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticatedProxyType }
     *     
     */
    public void setHTTPProxy(AuthenticatedProxyType value) {
        this.httpProxy = value;
    }

    /**
     * Gets the value of the socks4Proxy property.
     * 
     * @return
     *     possible object is
     *     {@link UnauthenticatedProxyType }
     *     
     */
    public UnauthenticatedProxyType getSOCKS4Proxy() {
        return socks4Proxy;
    }

    /**
     * Sets the value of the socks4Proxy property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnauthenticatedProxyType }
     *     
     */
    public void setSOCKS4Proxy(UnauthenticatedProxyType value) {
        this.socks4Proxy = value;
    }

    /**
     * Gets the value of the socks5Proxy property.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticatedProxyType }
     *     
     */
    public AuthenticatedProxyType getSOCKS5Proxy() {
        return socks5Proxy;
    }

    /**
     * Sets the value of the socks5Proxy property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticatedProxyType }
     *     
     */
    public void setSOCKS5Proxy(AuthenticatedProxyType value) {
        this.socks5Proxy = value;
    }

}
