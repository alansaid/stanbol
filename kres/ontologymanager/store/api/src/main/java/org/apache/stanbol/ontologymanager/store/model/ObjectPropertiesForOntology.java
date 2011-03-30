//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.4-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.05.13 at 09:50:16 AM EEST 
//

package org.apache.stanbol.ontologymanager.store.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{model.rest.persistence.iks.srdc.com.tr}OntologyMetaInformation"/>
 *         &lt;element ref="{model.rest.persistence.iks.srdc.com.tr}PropertyMetaInformation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"ontologyMetaInformation", "propertyMetaInformation"})
@XmlRootElement(name = "ObjectPropertiesForOntology")
public class ObjectPropertiesForOntology {

    @XmlElement(name = "OntologyMetaInformation", required = true)
    protected OntologyMetaInformation ontologyMetaInformation;
    @XmlElement(name = "PropertyMetaInformation")
    protected List<PropertyMetaInformation> propertyMetaInformation;

    /**
     * Gets the value of the ontologyMetaInformation property.
     * 
     * @return possible object is {@link OntologyMetaInformation }
     * 
     */
    public OntologyMetaInformation getOntologyMetaInformation() {
        return ontologyMetaInformation;
    }

    /**
     * Sets the value of the ontologyMetaInformation property.
     * 
     * @param value
     *            allowed object is {@link OntologyMetaInformation }
     * 
     */
    public void setOntologyMetaInformation(OntologyMetaInformation value) {
        this.ontologyMetaInformation = value;
    }

    /**
     * Gets the value of the propertyMetaInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification
     * you make to the returned list will be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the propertyMetaInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getPropertyMetaInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link PropertyMetaInformation }
     * 
     * 
     */
    public List<PropertyMetaInformation> getPropertyMetaInformation() {
        if (propertyMetaInformation == null) {
            propertyMetaInformation = new ArrayList<PropertyMetaInformation>();
        }
        return this.propertyMetaInformation;
    }

}
