package org.primefaces.model.graphvis;

import java.io.Serializable;
import java.util.List;

import org.primefaces.model.graphvis.GraphvisNode.NODE_SHAPE;

/**
 * Graph model
 * 
 * http://www.sysord.com  
 */
public interface GraphvisModel extends Serializable{

	/**
	 * Action type. 
	 * action type is used for synchronization between client and server, 
	 * it define the type of action to be replicated on the client or server sode
	 */
	public enum GVMODEL_ACTION_TYPE{
		ADD_NODE,
		REMOVE_NODE,
		UPDATE_NODE,
		ADD_EDGE,
		REMOVE_EDGE,		
		UPDATE_EDGE,		
		DO_LAYOUT,		
	}

	/**
	 * GraphvisModelAction
	 * it define action applied on the graph model. Model action are exchanged
	 * between client and server for synchronize graphs model on the two sides.
	 */
	public interface GraphvisModelAction extends Serializable{
		public GVMODEL_ACTION_TYPE getActionType();
		public String getTargetElementId();
	}

	/**
	 * provide list of GraphvisModelAction, logging all action applied on the graph model
	 * 
	 * @return list of all GraphvisModelAction applied on the graph model
	 */
	public List<GraphvisModelAction> getModelSynchronizationStructure();
	
	/**
	 * modelSynchronizationStructure setter
	 * 
	 * @param modelSynchronizationStructure
	 */
	public void setModelSynchronizationStructure(List<GraphvisModelAction> modelSynchronizationStructure);
	
	/**
	 * clear all logged GraphvisModelAction
	 */
	public void clearModelSynchronizationStructure();

	/**
	 * provide the default model node shape.
	 * When no shape is defined for a new node
	 * the new node will use the default node shape.
	 * 
	 * @return default node shape
	 */
	public NODE_SHAPE getDefaultNodeShape();
	
	/**
	 * default node shape setter
	 * @param defaultNodeShape
	 */
	public void setDefaultNodeShape(NODE_SHAPE defaultNodeShape);

	/**
	 * provide the default model node size.
	 * When no size is defined for a new node
	 * the new node will use the default node size.
	 * @return
	 */
	public int getDefaultNodeSize();
	
	/**
	 * default node size setter
	 * @param defaultNodeSize
	 */
	public void setDefaultNodeSize(int defaultNodeSize);
	
	/**
	 * Provide the default model node color.
	 * When no color is defined for a new node
	 * the new node will use the default node color.
	 * @return
	 */
	public String getDefaultNodeColor();
	
	/**
	 * default node color setter
	 * 
	 * @param defaultNodeColor
	 */
	public void setDefaultNodeColor(String defaultNodeColor);
	
	/**
	 * Layout define the drawing strategy for the node
	 * 
	 * on the client side. 
	 * @return the layout name
	 */
	public String getLayout();
	
	/**
	 * layou setter
	 * 
	 * @param layout name of the layout to be applied
	 */
	public void setLayout(String layout);
	
	/**
	 * provide all model nodes.
	 * 
	 * @return list of all nodes 
	 */
	public List<GraphvisNode> getNodes();
	
	/**
	 * provide all model edges.
	 * 
	 * @return list of all edges
	 */
	public List<GraphvisEdge> getEdges();

	/**
	 * provide all selected model elements.
	 * 
	 * @return list of all graph model element tagged as selected
	 */
	public List<GraphvisModelElement> getSelectedItems();
	
	/**
	 * provide all selected model nodes.
	 * 
	 * @return list of all graph model nodes tagged as selected
	 */
	public List<GraphvisNode> getSelectedNodes();
	
	/**
	 * provide all selected model edges.
	 * 
	 * @return list of all graph model edges tagged as selected
	 */
	public List<GraphvisEdge> getSelectedEdges();
	
	/**
	 * Select a model element. the model element will be tagged as selected
	 * 
	 * @param modelElement
	 */
	public void selectItem(GraphvisModelElement modelElement);
	
	/**
	 * Select a list of model elements. the model elements will be tagged as selected
	 * 
	 * @param modelElements
	 */
	public void selectItems(List<GraphvisModelElement> modelElements);
	
	/**
	 * Select a model element using its id. the model element will be tagged as selected
	 * 
	 * @param modelElementId
	 */
	public void selectItem(String modelElementId);
	
	/**
	 * Unselect a model element. the model element will be tagged as not selected
	 * 
	 * @param modelElement
	 */
	public void unselectItem(GraphvisModelElement modelElement);
	
	/**
	 * Unselect a model element using its id. the model element will be tagged as uselected
	 * 
	 * @param modelElementId
	 */
	public void unselectItem(String modelElementId);
	
	/**
	 * Unselect a list of model elements. the model elements will be tagged as selected
	 * 
	 * @param modelElements
	 */
	public void unselectItems(List<GraphvisModelElement> modelElements);
	
	
	/**
	 * provide a model element by its id
	 * 
	 * @param id the model element id
	 * @return the model element or null if not exists model element with the id
	 */
	public GraphvisModelElement getGraphvisModelElement(String id);
	
	/**
	 * provide a list containing all grpah model elements.
	 * 
	 * @return list of all model elements
	 */
	public List<GraphvisModelElement> getGraphvisModelElements();
	
	/**
	 * request if a model element with the id exists
	 * 
	 * @param id model element id
	 * @return true if a model element with the id exists, false otherwise 
	 */
	public boolean existsGraphvisModelElement(String id);
	
	/**
	 * node element getter by id
	 * 
	 * @param nodeId
	 * @return node element or false
	 */
	public GraphvisNode getNode(String nodeId);
	
	/**
	 * add a new node 
	 * 
	 * @param node node to add
	 * @return the added node
	 */
	public GraphvisNode addNode(GraphvisNode node);
	
	/**
	 * create a new node and add it to the model.
	 * color, size are shape are initialized using default value.
	 * 
	 * @param id node id
	 * @param label node label
	 * @return the added node
	 */
	public GraphvisNode addNode(String id, String label);

	/**
	 * remove node by id. remove the node with the id if exists.
	 * 
	 * @param nodeId node id
	 * @return the removed node or null if none node was removed
	 */
	public GraphvisNode removeNode(String nodeId);

	/**
	 * remove node. remove the node if exists.
	 * @param node node to remove
	 * @return the removed node or null if none node was removed
	 */
	public GraphvisNode removeNode(GraphvisNode node);
	
	/**
	 * if a node with same id exists in the model, it is synchronized 
	 * with the node properties (properties copy).
	 * 
	 * @param node node
	 * @return the synchronized node
	 */
	public GraphvisNode updateNode(GraphvisNode node);
	
	/**
	 * edge element getter by id
	 * 
	 * @param edgeId
	 * @return
	 */
	public GraphvisEdge getEdge(String edgeId);
	
	/**
	 * Create and add a new edge element. the edge id is generated
	 * 
	 * @param label edge label
	 * @param sourceNode origin node
	 * @param targetNode destination node
	 * @return the new edge
	 */
	public GraphvisEdge addEdge(String label, GraphvisNode sourceNode, GraphvisNode targetNode);
	
	/**
	 * Create and add a new edge element
	 * 
	 * @param id edge id
	 * @param label edge label
	 * @param sourceNode origin node
	 * @param targetNode destination node
	 * @return the new edge
	 */
	public GraphvisEdge addEdge(String id, String label, GraphvisNode sourceNode, GraphvisNode targetNode);
	
	/**
	 * Create and add a new edge element
	 * 
	 * @param id edge id
	 * @param label edge label
	 * @param sourceNode origin node id
	 * @param targetNode destination node id
	 * @return the new edge
	 */
	public GraphvisEdge addEdge(String id, String label, String sourceNodeId, String targetNodeId);
	
	/**
	 * Remove edge by id if exists
	 * 
	 * @param edgeId edge id
	 * @return the removed edge or null
	 */
	public GraphvisEdge removeEdge(String edgeId);
	
	/**
	 * Remove edge if exists in the model
	 * 
	 * @param edge
	 * @return the removed edge or null
	 */
	public GraphvisEdge removeEdge(GraphvisEdge edge);
	
	/**
	 * if an edge with same id exists in the model, it is synchronized 
	 * with the edge properties (properties copy).
	 * @param edge
	 * @return
	 */
	public GraphvisEdge updateEdge(GraphvisEdge edge);
			
	
}
