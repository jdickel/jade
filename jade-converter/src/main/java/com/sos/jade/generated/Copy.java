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
 *         &lt;element ref="{}CopySource"/>
 *         &lt;element ref="{}CopyTarget"/>
 *         &lt;element ref="{}TransferOptions" minOccurs="0"/>
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
    "copySource",
    "copyTarget",
    "transferOptions"
})
@XmlRootElement(name = "Copy")
public class Copy {

    @XmlElement(name = "CopySource", required = true)
    protected CopySource copySource;
    @XmlElement(name = "CopyTarget", required = true)
    protected CopyTarget copyTarget;
    @XmlElement(name = "TransferOptions")
    protected TransferOptions transferOptions;

    /**
     * Gets the value of the copySource property.
     * 
     * @return
     *     possible object is
     *     {@link CopySource }
     *     
     */
    public CopySource getCopySource() {
        return copySource;
    }

    /**
     * Sets the value of the copySource property.
     * 
     * @param value
     *     allowed object is
     *     {@link CopySource }
     *     
     */
    public void setCopySource(CopySource value) {
        this.copySource = value;
    }

    /**
     * Gets the value of the copyTarget property.
     * 
     * @return
     *     possible object is
     *     {@link CopyTarget }
     *     
     */
    public CopyTarget getCopyTarget() {
        return copyTarget;
    }

    /**
     * Sets the value of the copyTarget property.
     * 
     * @param value
     *     allowed object is
     *     {@link CopyTarget }
     *     
     */
    public void setCopyTarget(CopyTarget value) {
        this.copyTarget = value;
    }

    /**
     * Gets the value of the transferOptions property.
     * 
     * @return
     *     possible object is
     *     {@link TransferOptions }
     *     
     */
    public TransferOptions getTransferOptions() {
        return transferOptions;
    }

    /**
     * Sets the value of the transferOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransferOptions }
     *     
     */
    public void setTransferOptions(TransferOptions value) {
        this.transferOptions = value;
    }

}
