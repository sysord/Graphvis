package org.primefaces.model.graphvis;

/**
 * 
 * Graph edge  
 *
 * http://www.sysord.com
 */
public interface GraphvisEdge extends GraphvisModelElement{

	public static final GRAPHVIS_ELEMENT_TYPE ELEMENT_TYPE = GRAPHVIS_ELEMENT_TYPE.EDGE;

	/**
	 * available arrow shapes 
	 */
	public enum ARROW_SHAPE{
		NONE,
		DELTA,
		ARROW,
		DIAMOND,
		CIRCLE,
		OCTAGON,
		PARALLELOGRAM,
		
		TEE,
		TRIANGLE,
		SQUARE,
	}

	/**
	 * origin node getter.
	 * @return origin node
	 */
	public GraphvisNode getSourceNode();
	
	/**
	 * origin node setter
	 * @param sourceNode
	 */
	public void setSourceNode(GraphvisNode sourceNode);

	/**
	 * destination node getter 
	 * @return destination node
	 */
	public GraphvisNode getTargetNode();

	/**
	 * target node setter 
	 * @param targetNode
	 */
	public void setTargetNode(GraphvisNode targetNode);

	/**
	 * 
	 * @return true if the edge is directed, false otherwise
	 */
	public Boolean getDirected();
	/**
	 * directed attribute setter
	 * @param directed
	 */
	public void setDirected(Boolean directed);

	/**
	 * edge width on the graph representation  getter
	 * @return edge with
	 */
	public Integer getWidth();
	/**
	 * edge width on the graph representation setter
	 * @param width
	 */
	public void setWidth(Integer width);

	/**
	 * edge arrowshape getter
	 * @return edge with
	 */
	public ARROW_SHAPE getShape();
	/**
	 * edge arrowshape setter
	 * @param shape
	 */
	public void setShape(ARROW_SHAPE shape);
}
