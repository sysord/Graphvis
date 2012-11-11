package org.primefaces.model.graphvis.impl;

import org.primefaces.model.graphvis.GraphvisEdge;
import org.primefaces.model.graphvis.GraphvisModelElement;
import org.primefaces.model.graphvis.GraphvisNode;

/**
 * Graphvis edge default implementation
 * 
 * http://www.sysord.com
 */
public class GraphvisEdgeImpl extends GraphvisModelElementImpl implements GraphvisEdge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected GraphvisNode sourceNode;
	protected GraphvisNode targetNode;
	protected Boolean directed = false;
	protected Integer width = 1;
	protected ARROW_SHAPE shape = ARROW_SHAPE.ARROW;
	

	@Override
	public GRAPHVIS_ELEMENT_TYPE getElementType() {
		return GraphvisEdge.ELEMENT_TYPE;
	}
	
	public GraphvisEdgeImpl(String id, String label, GraphvisNode sourceNode,
			GraphvisNode targetNode) {
		super(id, label);
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
	}

	public GraphvisNode getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(GraphvisNode sourceNode) {
		this.sourceNode = sourceNode;
	}

	public GraphvisNode getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(GraphvisNode targetNode) {
		this.targetNode = targetNode;
	}

	@Override
	public void synchronize(GraphvisModelElement otherElement) {		
		super.synchronize(otherElement);
		GraphvisEdge otherEdge = (GraphvisEdge) otherElement;
		this.sourceNode = otherEdge.getSourceNode();
		this.targetNode = otherEdge.getTargetNode();
		this.directed = otherEdge.getDirected();
		this.width = otherEdge.getWidth();
		this.shape = otherEdge.getShape();				
	}

	@Override
	public Boolean getDirected() {
		return directed;
	}

	@Override
	public void setDirected(Boolean directed) {
		this.directed = directed;
	}

	@Override
	public Integer getWidth() {
		return width;
	}

	@Override
	public void setWidth(Integer width) {
		this.width = width;
	}
	
	@Override
	public ARROW_SHAPE getShape() {
		return shape;
	}

	@Override
	public void setShape(ARROW_SHAPE shape) {
		this.shape = shape;
	}


}
