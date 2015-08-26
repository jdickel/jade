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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CredentialStoreType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CredentialStoreType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}CSFile"/>
 *         &lt;element ref="{}CSAuthentication"/>
 *         &lt;element ref="{}CSEntryPath"/>
 *         &lt;element ref="{}CSExportAttachment" minOccurs="0"/>
 *         &lt;element ref="{}CSStoreType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CredentialStoreType", propOrder = {
    "csFile",
    "csAuthentication",
    "csEntryPath",
    "csExportAttachment",
    "csStoreType"
})
@XmlSeeAlso({
    CredentialStoreFragment.class
})
public class CredentialStoreType {

    @XmlElement(name = "CSFile", required = true)
    protected String csFile;
    @XmlElement(name = "CSAuthentication", required = true)
    protected CSAuthentication csAuthentication;
    @XmlElement(name = "CSEntryPath", required = true)
    protected String csEntryPath;
    @XmlElement(name = "CSExportAttachment")
    protected CSExportAttachment csExportAttachment;
    @XmlElement(name = "CSStoreType", defaultValue = "KeePassX")
    protected String csStoreType;

    /**
     * Gets the value of the csFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSFile() {
        return csFile;
    }

    /**
     * Sets the value of the csFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSFile(String value) {
        this.csFile = value;
    }

    /**
     * Gets the value of the csAuthentication property.
     * 
     * @return
     *     possible object is
     *     {@link CSAuthentication }
     *     
     */
    public CSAuthentication getCSAuthentication() {
        return csAuthentication;
    }

    /**
     * Sets the value of the csAuthentication property.
     * 
     * @param value
     *     allowed object is
     *     {@link CSAuthentication }
     *     
     */
    public void setCSAuthentication(CSAuthentication value) {
        this.csAuthentication = value;
    }

    /**
     * Gets the value of the csEntryPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSEntryPath() {
        return csEntryPath;
    }

    /**
     * Sets the value of the csEntryPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSEntryPath(String value) {
        this.csEntryPath = value;
    }

    /**
     * Gets the value of the csExportAttachment property.
     * 
     * @return
     *     possible object is
     *     {@link CSExportAttachment }
     *     
     */
    public CSExportAttachment getCSExportAttachment() {
        return csExportAttachment;
    }

    /**
     * Sets the value of the csExportAttachment property.
     * 
     * @param value
     *     allowed object is
     *     {@link CSExportAttachment }
     *     
     */
    public void setCSExportAttachment(CSExportAttachment value) {
        this.csExportAttachment = value;
    }

    /**
     * Gets the value of the csStoreType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSStoreType() {
        return csStoreType;
    }

    /**
     * Sets the value of the csStoreType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSStoreType(String value) {
        this.csStoreType = value;
    }

}
