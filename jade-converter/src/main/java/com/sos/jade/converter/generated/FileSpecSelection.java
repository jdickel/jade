//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.08.28 um 03:50:36 PM CEST 
//


package com.sos.jade.converter.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}FileSpec"/>
 *         &lt;element ref="{}Directory" minOccurs="0"/>
 *         &lt;element ref="{}Recursive" minOccurs="0"/>
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
    "fileSpec",
    "directory",
    "recursive"
})
@XmlRootElement(name = "FileSpecSelection")
public class FileSpecSelection {

    @XmlElement(name = "FileSpec", required = true)
    protected String fileSpec;
    @XmlElement(name = "Directory")
    protected String directory;
    @XmlElement(name = "Recursive", defaultValue = "false")
    protected Boolean recursive;

    /**
     * Ruft den Wert der fileSpec-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileSpec() {
        return fileSpec;
    }

    /**
     * Legt den Wert der fileSpec-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileSpec(String value) {
        this.fileSpec = value;
    }

    /**
     * Ruft den Wert der directory-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Legt den Wert der directory-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirectory(String value) {
        this.directory = value;
    }

    /**
     * Ruft den Wert der recursive-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRecursive() {
        return recursive;
    }

    /**
     * Legt den Wert der recursive-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRecursive(Boolean value) {
        this.recursive = value;
    }

}
