//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.08 at 10:11:39 AM CET 
//


package eu.europa.ec.fisheries.asset.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBHashCodeStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;


/**
 * <p>Java class for NumberOfAssetsGroupByFlagState complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NumberOfAssetsGroupByFlagState"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="flagState" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="numberOfAssets" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NumberOfAssetsGroupByFlagState", propOrder = {
    "flagState",
    "numberOfAssets"
})
public class NumberOfAssetsGroupByFlagState implements Equals, HashCode
{

    @XmlElement(required = true)
    protected String flagState;
    protected int numberOfAssets;

    /**
     * Default no-arg constructor
     * 
     */
    public NumberOfAssetsGroupByFlagState() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public NumberOfAssetsGroupByFlagState(final String flagState, final int numberOfAssets) {
        this.flagState = flagState;
        this.numberOfAssets = numberOfAssets;
    }

    /**
     * Gets the value of the flagState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlagState() {
        return flagState;
    }

    /**
     * Sets the value of the flagState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlagState(String value) {
        this.flagState = value;
    }

    /**
     * Gets the value of the numberOfAssets property.
     * 
     */
    public int getNumberOfAssets() {
        return numberOfAssets;
    }

    /**
     * Sets the value of the numberOfAssets property.
     * 
     */
    public void setNumberOfAssets(int value) {
        this.numberOfAssets = value;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof NumberOfAssetsGroupByFlagState)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final NumberOfAssetsGroupByFlagState that = ((NumberOfAssetsGroupByFlagState) object);
        {
            String lhsFlagState;
            lhsFlagState = this.getFlagState();
            String rhsFlagState;
            rhsFlagState = that.getFlagState();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "flagState", lhsFlagState), LocatorUtils.property(thatLocator, "flagState", rhsFlagState), lhsFlagState, rhsFlagState)) {
                return false;
            }
        }
        {
            int lhsNumberOfAssets;
            lhsNumberOfAssets = this.getNumberOfAssets();
            int rhsNumberOfAssets;
            rhsNumberOfAssets = that.getNumberOfAssets();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "numberOfAssets", lhsNumberOfAssets), LocatorUtils.property(thatLocator, "numberOfAssets", rhsNumberOfAssets), lhsNumberOfAssets, rhsNumberOfAssets)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        final EqualsStrategy strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        int currentHashCode = 1;
        {
            String theFlagState;
            theFlagState = this.getFlagState();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "flagState", theFlagState), currentHashCode, theFlagState);
        }
        {
            int theNumberOfAssets;
            theNumberOfAssets = this.getNumberOfAssets();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "numberOfAssets", theNumberOfAssets), currentHashCode, theNumberOfAssets);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}
