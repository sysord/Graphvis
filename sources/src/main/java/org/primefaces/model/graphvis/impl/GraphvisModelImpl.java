package org.primefaces.model.graphvis.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.model.graphvis.GraphvisEdge;
import org.primefaces.model.graphvis.GraphvisEdge.ARROW_SHAPE;
import org.primefaces.model.graphvis.GraphvisModel;
import org.primefaces.model.graphvis.GraphvisModelElement;
import org.primefaces.model.graphvis.GraphvisModelElement.GRAPHVIS_ELEMENT_TYPE;
import org.primefaces.model.graphvis.GraphvisNode;
import org.primefaces.model.graphvis.GraphvisNode.NODE_SHAPE;

/**
 * Graphvis model default implementation
 * 
 * http://www.sysord.com
 */
public class GraphvisModelImpl implements GraphvisModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	/**
	 * all model elements stored in map with id as key
	 */
	protected Map<String, GraphvisModelElement> hModelElement = new HashMap<String, GraphvisModelElement>();
	
	/**
	 * all model nodes
	 */
	protected List<GraphvisNode> nodes = new ArrayList<GraphvisNode>();

	/**
	 * all model edges
	 */
	protected List<GraphvisEdge> edges = new ArrayList<GraphvisEdge>();
	
	
	protected NODE_SHAPE defaultNodeShape = NODE_SHAPE.ELLIPSE;
	protected ARROW_SHAPE defaultEdgeShape = ARROW_SHAPE.ARROW;
	protected int defaultNodeSize = 20;
	protected String defaultNodeColor = "#0B94B1";
	protected String defaultEdgeColor = "#000000";
	protected String layout = null;

	
	
	/**
	 * Struct for incremental synchronization
	 */
	public class GraphvisModelActionImpl implements GraphvisModelAction{
		private static final long serialVersionUID = 1L;
		
		protected GVMODEL_ACTION_TYPE  actionType;
		protected String  targetElementId;
		
		public GraphvisModelActionImpl(GVMODEL_ACTION_TYPE actionType, String targetElementId) {
			super();
			this.actionType = actionType;
			this.targetElementId = targetElementId;
		}

		public GVMODEL_ACTION_TYPE getActionType() {
			return actionType;
		}

		public String getTargetElementId() {
			return targetElementId;
		}		
	}
	
	//----------------------------------
	// Model synchronization structure
	//----------------------------------
	
	protected List<GraphvisModelAction> modelSynchronizationStructure = new ArrayList<GraphvisModel.GraphvisModelAction>();
	
	protected void registerSynchronisationAction(GVMODEL_ACTION_TYPE actionType, String targetElementId){
		modelSynchronizationStructure.add(new GraphvisModelActionImpl(actionType, targetElementId));
	}
	
	@Override
	public List<GraphvisModelAction> getModelSynchronizationStructure() {
		return modelSynchronizationStructure;
	}

	@Override
	public void setModelSynchronizationStructure(List<GraphvisModelAction> modelSynchronizationStructure) {
		this.modelSynchronizationStructure = modelSynchronizationStructure;
	}

	@Override
	public void clearModelSynchronizationStructure() {
		this.modelSynchronizationStructure = new ArrayList<GraphvisModel.GraphvisModelAction>();;
	}

	
	//----------------
	// Graph elements
	//----------------

	public GraphvisModelElement getGraphvisModelElement(String id){
		return hModelElement.get(id);
	} 

	@SuppressWarnings("unchecked")
	public <T extends GraphvisModelElement> T getGraphvisModelElement(String id, GraphvisModelElement.GRAPHVIS_ELEMENT_TYPE elementType){
		GraphvisModelElement element = getGraphvisModelElement(id);
		if (element != null &&  element.getElementType() == elementType){
			return (T)element;
		}
		return null;
	} 

	public List<GraphvisModelElement> getGraphvisModelElements(){
		return new ArrayList<GraphvisModelElement>(hModelElement.values());
	}

	public boolean existsGraphvisModelElement(String id){
		return hModelElement.containsKey(id);
	}
	
	public boolean existsGraphvisModelElement(String id, GraphvisModelElement.GRAPHVIS_ELEMENT_TYPE elementType){
		GraphvisModelElement element = hModelElement.get(id);
		return (element != null &&  element.getElementType() == elementType);
	}
	
	protected void addNewModelElement(GraphvisModelElement element){
		hModelElement.put(element.getId(), element);
		//modelActions.add(new GraphvisModelActionImpl(GVMODEL_ACTION_TYPE.ADD_NODE, element.getId()));
	}
	
	protected void removeModelElement(GraphvisModelElement element){
		hModelElement.remove(element.getId());
		//modelActions.add(new GraphvisModelActionImpl(GVMODEL_ACTION_TYPE.ADD_NODE, element.getId()));
	}
	
	protected void updateModelElement(GraphvisModelElement element){
		//modelActions.add(new GraphvisModelActionImpl(GVMODEL_ACTION_TYPE.UPDATE_ELEMENT, element.getId()));
	}


	//------------
	// Nodes
	//------------
	
	@Override
	public NODE_SHAPE getDefaultNodeShape() {
		return defaultNodeShape;
	}
	@Override
	public void setDefaultNodeShape(NODE_SHAPE defaultNodeShape) {
		this.defaultNodeShape = defaultNodeShape;
	}
	
	@Override
	public int getDefaultNodeSize() {
		return defaultNodeSize;
	}
	@Override
	public void setDefaultNodeSize(int defaultNodeSize) {
		this.defaultNodeSize = defaultNodeSize;
	}

	@Override
	public String getDefaultNodeColor() {
		return defaultNodeColor;
	}

	@Override
	public void setDefaultNodeColor(String defaultNodeColor) {
		this.defaultNodeColor = defaultNodeColor;
	}

	
	
	@Override
	public List<GraphvisNode> getNodes() {
		return new ArrayList<GraphvisNode>(nodes);
	}
	
	@Override
	public GraphvisNode getNode(String nodeId) {
		GraphvisModelElement modelElement = getGraphvisModelElement(nodeId);
		if(modelElement != null && modelElement.getElementType() == GRAPHVIS_ELEMENT_TYPE.NODE){
			return (GraphvisNode) modelElement;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.primefaces.model.graphvis.GraphvisModel#addNode(java.lang.String, java.lang.String)
	 */
	@Override
	public GraphvisNode addNode(String id, String label) {
		GraphvisNode newNode = new GraphvisNodeImpl(id, label);
		return addNode(newNode);
	} 
	
	/* (non-Javadoc)
	 * @see org.primefaces.model.graphvis.GraphvisModel#addNode(org.primefaces.model.graphvis.GraphvisNode)
	 */
	@Override	
	public GraphvisNode addNode(GraphvisNode node) {
		if(existsGraphvisModelElement(node.getId(), GRAPHVIS_ELEMENT_TYPE.NODE)){
			return (GraphvisNode) getGraphvisModelElement(node.getId());
		}

		if(node.getSize() == -1){
			node.setSize(defaultNodeSize);
		}

		if(node.getShape() == null){
			node.setShape(defaultNodeShape);
		}

		if(node.getColor() == null){
			node.setColor(defaultNodeColor);
		}
		
		addNewModelElement(node);
		registerSynchronisationAction(GVMODEL_ACTION_TYPE.ADD_NODE, node.getId());
		nodes.add(node);
		return node;
	}

	@Override
	public GraphvisNode removeNode(String nodeId) {
		return removeNode((GraphvisNode)getGraphvisModelElement(nodeId, GRAPHVIS_ELEMENT_TYPE.NODE));		
	}

	@Override
	public GraphvisNode removeNode(GraphvisNode node) {
		if(node == null || !existsGraphvisModelElement(node.getId(), GRAPHVIS_ELEMENT_TYPE.NODE)){
			return null;
		}
		GraphvisNode removedNode = processRemoveNode(node);
		registerSynchronisationAction(GVMODEL_ACTION_TYPE.REMOVE_NODE, node.getId());
		return removedNode;
	}

	protected GraphvisNode processRemoveNode(GraphvisNode node){
		//Remove all connected edges		
		List<GraphvisEdge> lConnectedEdges = new ArrayList<GraphvisEdge>();
		lConnectedEdges.addAll(node.getIncomingEdges());
		lConnectedEdges.addAll(node.getOutcomingEdges());		
		
		for(GraphvisEdge edge : lConnectedEdges){
			processRemoveEdge(edge);
		}
		
		removeModelElement(node);
		nodes.remove(node);
		return node;
	}

	public GraphvisNode updateNode(GraphvisNode node) {
		GraphvisNode modelNode = getGraphvisModelElement(node.getId(), GRAPHVIS_ELEMENT_TYPE.NODE);
		if(modelNode != null){
			//synchronizeNode(modelNode, node);
			//updateModelElement(modelNode);
			registerSynchronisationAction(GVMODEL_ACTION_TYPE.UPDATE_NODE, node.getId());
		}
		return modelNode;
	}


//	protected GraphvisNode synchronizeNode(String nodeId, JSONObject nodeObject){
//		orgNode.setLabel(newNode.getLabel());
//		return orgNode;
//	}


	//------------
	// Edges
	//------------

	@Override
	public List<GraphvisEdge> getEdges() {
		return new ArrayList<GraphvisEdge>(edges);
	}
	
	@Override
	public GraphvisEdge getEdge(String nodeId) {
		GraphvisModelElement modelElement = getGraphvisModelElement(nodeId);
		if(modelElement != null && modelElement.getElementType() == GRAPHVIS_ELEMENT_TYPE.EDGE){
			return (GraphvisEdge) modelElement;
		}
		return null;
	}
		
	/* (non-Javadoc)
	 * @see org.primefaces.model.graphvis.GraphvisModel#addEdge(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public GraphvisEdge addEdge(String edgeId, String label, String sourceNodeId, String targetNodeId) {
		GraphvisNode sourceNode = getGraphvisModelElement(sourceNodeId, GRAPHVIS_ELEMENT_TYPE.NODE);		
		GraphvisNode targetNode = getGraphvisModelElement(targetNodeId, GRAPHVIS_ELEMENT_TYPE.NODE);
		if(sourceNode == null || targetNode == null) return null;		
		return addEdge(edgeId, label, sourceNode, targetNode);
	}

	/* (non-Javadoc)
	 * @see org.primefaces.model.graphvis.GraphvisModel#addEdge(java.lang.String, org.primefaces.model.graphvis.GraphvisNode, org.primefaces.model.graphvis.GraphvisNode)
	 */
	@Override
	public GraphvisEdge addEdge(String label, GraphvisNode sourceNode, GraphvisNode targetNode) {
		String edgeId = "EDGE__S" + sourceNode.getId() + "_T" + targetNode.getId();
		return addEdge(edgeId, label, sourceNode, targetNode);
	}

	/* (non-Javadoc)
	 * @see org.primefaces.model.graphvis.GraphvisModel#addEdge(java.lang.String, java.lang.String, org.primefaces.model.graphvis.GraphvisNode, org.primefaces.model.graphvis.GraphvisNode)
	 */
	@Override
	public GraphvisEdge addEdge(String edgeId, String label, GraphvisNode sourceNode, GraphvisNode targetNode) {
		if(existsGraphvisModelElement(edgeId, GRAPHVIS_ELEMENT_TYPE.EDGE)){
			return (GraphvisEdge) getGraphvisModelElement(edgeId);
		}
		GraphvisEdge newEdge = new GraphvisEdgeImpl(edgeId, label, sourceNode, targetNode);
		sourceNode.getOutcomingEdges().add(newEdge);
		targetNode.getIncomingEdges().add(newEdge);
		newEdge.setShape(defaultEdgeShape);
		newEdge.setColor(defaultEdgeColor);
		addNewModelElement(newEdge);
		edges.add(newEdge);
		registerSynchronisationAction(GVMODEL_ACTION_TYPE.ADD_EDGE, edgeId);
		return newEdge;		
	}

	@Override
	public GraphvisEdge removeEdge(String edgeId) {
		if(!existsGraphvisModelElement(edgeId, GRAPHVIS_ELEMENT_TYPE.EDGE)){
			return null;
		}
		GraphvisEdge removedEdge = (GraphvisEdge) getGraphvisModelElement(edgeId);		
		return processRemoveEdge(removedEdge);
	}

	@Override
	public GraphvisEdge removeEdge(GraphvisEdge edge) {
		if(!existsGraphvisModelElement(edge.getId(), GRAPHVIS_ELEMENT_TYPE.EDGE)){
			return null;
		}
		return processRemoveEdge(edge);
	}
	
	
	/**
	 * Remove the edge
	 * 
	 * @param edge
	 * @return
	 */
	protected GraphvisEdge processRemoveEdge(GraphvisEdge edge){
		removeModelElement(edge);
		edges.remove(edge);
		//unreference
		edge.getSourceNode().getOutcomingEdges().remove(edge);
		edge.getTargetNode().getIncomingEdges().remove(edge);		
		registerSynchronisationAction(GVMODEL_ACTION_TYPE.REMOVE_EDGE, edge.getId());
		return edge;		
	}
	
	@Override
	public GraphvisEdge updateEdge(GraphvisEdge edge) {
		GraphvisEdge modelEdge = getGraphvisModelElement(edge.getId(), GRAPHVIS_ELEMENT_TYPE.EDGE);
		if(modelEdge != null){
			synchronizeEdge(modelEdge, edge);
			updateModelElement(modelEdge);
			registerSynchronisationAction(GVMODEL_ACTION_TYPE.UPDATE_EDGE, edge.getId());
		}
		return modelEdge;
	}

	protected GraphvisEdge synchronizeEdge(GraphvisEdge orgEdge, GraphvisEdge newEdge){
		//XXX Voir pour références aux noeuds du newEdge + maintenance structures incoming/outcoming
		// + newEdge comment est il construit ???
		orgEdge.synchronize(newEdge);
		return orgEdge;
	}
	
	
	/*-------------------
	 *   Selection
	 *-------------------*/

	@Override
	public List<GraphvisModelElement> getSelectedItems() {
		List<GraphvisModelElement> lSelectedItems = new ArrayList<GraphvisModelElement>();
		for(GraphvisModelElement element : hModelElement.values()){
			if(element.isSelected()){
				lSelectedItems.add(element);
			}
		}
		return lSelectedItems;
	}

	@Override
	public List<GraphvisNode> getSelectedNodes() {
		List<GraphvisNode> lSelectedItems = new ArrayList<GraphvisNode>();
		for(GraphvisNode node : nodes){
			if(node.isSelected()){
				lSelectedItems.add(node);
			}
		}
		return lSelectedItems;
	}
	
	@Override
	public List<GraphvisEdge> getSelectedEdges() {
		List<GraphvisEdge> lSelectedItems = new ArrayList<GraphvisEdge>();
		for(GraphvisEdge edge : edges){
			if(edge.isSelected()){
				lSelectedItems.add(edge);
			}
		}
		return lSelectedItems;
	}
	
	@Override
	public void selectItem(String itemId) {
		System.out.println("SELECT ITEM:" + itemId);
		selectItem(hModelElement.get(itemId));
	}

	@Override
	public void selectItem(GraphvisModelElement item) {
		if(item != null){
			item.select();			
		}
	}

	@Override
	public void selectItems(List<GraphvisModelElement> items) {
		for(GraphvisModelElement item : items){
			selectItem(item);
		}
	}

	@Override
	public void unselectItem(String itemId) {
		unselectItem(hModelElement.get(itemId));
	}
	
	@Override
	public void unselectItem(GraphvisModelElement item) {
		if(item != null){
			item.unselect();			
		}
	}

	@Override
	public void unselectItems(List<GraphvisModelElement> items) {
		for(GraphvisModelElement item : items){
			unselectItem(item);
		}
	}

	@Override
	public String getLayout() {
		return layout;
	}

	@Override
	public void setLayout(String layout) {
		this.layout = layout;
		registerSynchronisationAction(GVMODEL_ACTION_TYPE.DO_LAYOUT, layout);
	}



}
