package org.primefaces.model.graphvis;

import java.util.List;

/**
 * 
 * Graph node interface  
 *
 * http://www.sysord.com
 */
public interface GraphvisNode extends GraphvisModelElement{
	
	public final GRAPHVIS_ELEMENT_TYPE ELEMENT_TYPE = GRAPHVIS_ELEMENT_TYPE.NODE;

	/**
	 * available node shapes 
	 */
	public static enum NODE_SHAPE{
		ELLIPSE,
		RECTANGLE,
		TRIANGLE,
		DIAMOND,
		HEXAGON,
		OCTAGON,
		PARALLELOGRAM,		
		ROUNDRECTANGLE,
	}
	
	/**
	 * provide all incoming edges
	 * @return list of all edges incoming to the node
	 */
	public List<GraphvisEdge> getIncomingEdges();
	
	/**
	 * provide all outcoming edges
	 * @return list of all edges outcoming from the node
	 */
	public List<GraphvisEdge> getOutcomingEdges();
	
	/**
	 * x node location on the graph representation getter
	 * @return x node location
	 */
	public float getX();
	/**
	 * x node location setter
	 * @param x
	 */
	public void setX(float x);
	
	/**
	 * y node location on the graphic representation getter
	 * @return y node location
	 */
	public float getY();
	/**
	 * y node location setter
	 * @param y
	 */
	public void setY(float y);
	
	/**
	 * node shape on the graphic representation getter
	 * @return node shape 
	 */
	public NODE_SHAPE getShape();
	/**
	 * node shape setter
	 * @param shape
	 */
	public void setShape(NODE_SHAPE shape);

	/**
	 * node size on the graphic representation getter
	 * @return
	 */
	public int getSize();
	/**
	 * node size setter
	 * @param size
	 */
	public void setSize(int size);
	
	

}
