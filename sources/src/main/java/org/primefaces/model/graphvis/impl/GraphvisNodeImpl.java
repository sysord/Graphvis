package org.primefaces.model.graphvis.impl;

import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.graphvis.GraphvisEdge;
import org.primefaces.model.graphvis.GraphvisModelElement;
import org.primefaces.model.graphvis.GraphvisNode;

/**
 * Graphvis node default implementation
 * 
 * http://www.sysord.com
 */
public class GraphvisNodeImpl extends GraphvisModelElementImpl implements GraphvisNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected List<GraphvisEdge> incomingEdges = new ArrayList<GraphvisEdge>();
	protected List<GraphvisEdge> outcomingEdges = new ArrayList<GraphvisEdge>();
	

	protected float x;
	protected float y;
	
	protected NODE_SHAPE shape = null;
	protected int size = -1;
	

	public GraphvisNodeImpl(String id, String label) {
		super(id, label);
	}

	public GraphvisNodeImpl(String id, String label, NODE_SHAPE shape) {
		this(id, label);
		this.shape = shape;
	}

	public GraphvisNodeImpl(String id, String label, NODE_SHAPE shape, int x, int y) {
		this(id, label, shape);
		this.x = x;
		this.y = y;
	}


	
	@Override
	public GRAPHVIS_ELEMENT_TYPE getElementType() {
		return GraphvisNode.ELEMENT_TYPE;
	}

	public NODE_SHAPE getShape() {
		return shape;
	}

	public void setShape(NODE_SHAPE shape) {
		this.shape = shape;
	}

	
	@Override
	public float getX() {
		return x;
	}
	@Override
	public void setX(float x) {
		this.x = x;
	}

	
	@Override
	public float getY() {
		return y;
	}
	@Override
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public int getSize() {
		return size;
	}
	@Override
	public void setSize(int size) {
		this.size = size;
	}
	
	
	@Override
	public List<GraphvisEdge> getIncomingEdges() {
		return this.incomingEdges;
	}

	@Override
	public List<GraphvisEdge> getOutcomingEdges() {
		return this.outcomingEdges;

	}

	@Override
	public void synchronize(GraphvisModelElement otherElement) {
		super.synchronize(otherElement);
		GraphvisNode otherNode = (GraphvisNode) otherElement;
		this.x = otherNode.getX();
		this.y = otherNode.getY();
		this.shape = otherNode.getShape();
		this.size = otherNode.getSize();
	}

	@Override
	public String toString() {
		return "GraphvisNodeImpl [id=" + id  +", x=" + x + ", y=" + y + "]";
	}



	
	
}
