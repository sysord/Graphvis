package org.primefaces.model.graphvis;

import java.io.Serializable;

/**
 * 	Graph model element interface
 * 
 * http://www.sysord.com
 */
public interface GraphvisModelElement extends Serializable{

	public enum GRAPHVIS_ELEMENT_TYPE {
		UNKNOW,
		NODE,
		EDGE,
	}
	
	/**
	 * provide element type
	 * @return the element type
	 */
	public GRAPHVIS_ELEMENT_TYPE getElementType();
	
	/**
	 * element id setter
	 * @param id
	 */
	public void setId(String id);
	/**
	 * element id getter
	 * @return element id
	 */
	public String getId();
	
	/**
	 * element label setter
	 * @param label
	 */
	public void setLabel(String label);
	/**
	 * element label getter
	 * @return element label 
	 */
	public String getLabel();

	/**
	 * element color setter
	 * @param color defined as hexadecimal string (example: "ff0000") 
	 */
	public void setColor(String color);

	/**
	 * element color getter
	 * @return element color
	 */
	public String getColor();

	/**
	 * set element as selected
	 */
	public void select();
	
	/**
	 * set element as unselected
	 */
	public void unselect();
	
	/**
	 * selected element query
	 * @return true if the element is selected, false otherwise
	 */
	public boolean isSelected();
	

	/**
	 * Synchronize the element with an other element
	 * synchronization update all element properties from 
	 * the other element properties
	 * @param otherElement
	 */
	public void synchronize(GraphvisModelElement otherElement);

	
}
