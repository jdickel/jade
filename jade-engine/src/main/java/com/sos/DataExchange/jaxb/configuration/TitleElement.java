//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, v2.2.4
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2012.08.21 at 08:04:29 PM CEST
//

package com.sos.DataExchange.jaxb.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

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
 *       &lt;attribute name="language" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre> */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "content" })
@XmlRootElement(name = "title")
public class TitleElement {

    @XmlValue
    protected String content;
    @XmlAttribute(name = "language")
    protected String language;

    /** Gets the value of the content property.
     * 
     * @return possible object is {@link String } */
    public String getContent() {
        return content;
    }

    /** Sets the value of the content property.
     * 
     * @param value allowed object is {@link String } */
    public void setContent(final String value) {
        content = value;
    }

    /** Gets the value of the language property.
     * 
     * @return possible object is {@link String } */
    public String getLanguage() {
        return language;
    }

    /** Sets the value of the language property.
     * 
     * @param value allowed object is {@link String } */
    public void setLanguage(final String value) {
        language = value;
    }

}
