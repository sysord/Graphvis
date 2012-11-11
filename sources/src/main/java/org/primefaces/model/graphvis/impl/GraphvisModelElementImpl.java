package org.primefaces.model.graphvis.impl;

import org.primefaces.model.graphvis.GraphvisModelElement;


/**
 * Abstract Graphvis model element
 * 
 * http://www.sysord.com
 */
public abstract class GraphvisModelElementImpl implements GraphvisModelElement {

	private static final long serialVersionUID = 1L;
	
	protected String id;
	protected String label;
	
	protected boolean isSelected;
	
	protected String  color = null;

	
	public GraphvisModelElementImpl(String id, String label) {
		super();
		this.id = id;
		this.label = label;
	}

	public abstract GRAPHVIS_ELEMENT_TYPE getElementType();

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void select() {
		isSelected = true;
	}

	@Override
	public void unselect() {
		isSelected = false;
	}

	@Override
	public boolean isSelected() {
		return isSelected;
	}

	@Override
	public String getColor() {
		return color;
	}

	@Override
	public void setColor(String color) {
		this.color = color;
	}
	
	@Override
	public void synchronize(GraphvisModelElement otherElement) {
		this.label = otherElement.getLabel();
		this.isSelected = otherElement.isSelected();
		this.color = otherElement.getColor();
	}

}
