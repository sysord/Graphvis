
/**
 * CytoscapeWeb Adapter Classes
 * 
 * http://www.sysord.com
 */
PrimeFaces.widget.CswGraphAdapter = function(divId, graphModel, visualStyle, onVisReadyListener, afterLayoutListener, cytoscapeWebPath, flashInstallerPath) {
	

	this.graphModel = graphModel;

	if(onVisReadyListener){
		this.onVisReadyListener = onVisReadyListener;
	}
	
	if(afterLayoutListener){
		this.afterLayoutListener = afterLayoutListener;
	}

    //-----------------------
    // create visualisation
    //-----------------------
    var options = {
        swfPath: cytoscapeWebPath,
        flashInstallerPath: flashInstallerPath
    };  
        
    this.vis = new org.cytoscapeweb.Visualization(divId, options);
    
	//create CytoscapeGraphModel empty model
	this.cswModel = {
		data: 	{
			nodes: [],
			edges: [],									
		} 
	};

    //TODO: extends schema
	this.cswModel.dataSchema = { nodes: [ 
								{ name: "label", type: "string" },
								{ name: "x", type: "number", defValue: 0 },
								{ name: "y", type: "number", defValue: 0 },
								{ name: "shape", type: "string" },
								{ name: "color", type: "string" },
								{ name: "size", type: "number", defValue: 10 }
							],  
				  edges: [ 	{ name: "label", type: "string" },
							{ name: "color", type: "string" },
							{ name: "width", type: "number", defValue: 1 },
							{ name: "shape", type: "string" },

				  		] 
    			};	


	if(visualStyle){
		this.visualStyle = visualStyle;
	}else{
		this.visualStyle = {
				nodes: {
					//shapes
					shape: { passthroughMapper: { attrName: "shape" } },
					//sizes
					size: {passthroughMapper: { attrName: "size" } },
					//colors
					color: {passthroughMapper: { attrName: "color" } },
				},

				edges: {
					//colors
					color: {passthroughMapper: { attrName: "color" } },
					targetArrowShape: {passthroughMapper: { attrName: "shape" } },
					width: {passthroughMapper: { attrName: "width" } },
				},

			};
	}
  
};

PrimeFaces.widget.CswGraphAdapter.prototype = { 

	init: function(){
	    
	    //--------------------------------------------------------
	    // draw model    
	    //--------------------------------------------------------

		//draw an empty graph 
	    this.vis.draw({ network:this.cswModel, visualStyle:this.visualStyle });    	

	    //attach after layout listener
	    if(this.afterLayoutListener){
	    	this.addAfterLayoutListener(this.afterLayoutListener);
	    }
	    
	    //attach onReady listener
	    var _self = this;
	    
	    this.vis.ready(function(){
	    	_self._convertGraphModel(_self.graphModel);
	    	_self.vis.edgeLabelsVisible(true);
	    	if(_self.onVisReadyListener){
	    		_self.onVisReadyListener();
	    	}
	    });        		
	},

		
	/**
	 * Create CytoscapeWebModel from generic model
	 * Generic model Structure:
	 * genericModel {
	 * 					properties:{ layout: value,
	 * 								 ...
	 * 								 propN: value,
	 * 								},
	 * 					nodes:[
	 * 							{id:nodeId, label:node Label, x:, y:, size: node size, shape: node Shape, color: node color (rgb), datas:{...}},
	 * 							...
	 * 					]
	 * 					edges:[
	 * 							{id:edgeId, label:edge Label, sourceNodeId:, targetNodeId:, directed:(true|false),  width: edge width, color: edge color (rgb), datas:{...}},
	 * 							...
	 * 					]
	 * 				}
	 */
	_convertGraphModel: function(genericModel){
		
		//Add nodes 
		for(var i = 0; i < genericModel.nodes.length; i++){
			this.addNode(genericModel.nodes[i].id, genericModel.nodes[i].label, genericModel.nodes[i].x, genericModel.nodes[i].y,  
					genericModel.nodes[i].size, genericModel.nodes[i].shape, genericModel.nodes[i].color, genericModel.nodes[i].datas, false);
		}
		
		//Add edges
		for(var i = 0; i < genericModel.edges.length; i++){
			this.addEdge(genericModel.edges[i].id, genericModel.edges[i].label, genericModel.edges[i].sourceNodeId, genericModel.edges[i].targetNodeId, 
						genericModel.edges[i].directed, genericModel.edges[i].width, genericModel.edges[i].color, genericModel.edges[i].datas, false);
		}
		
		if(genericModel.properties.layout){
			//layout
			this.applyLayout(genericModel.properties.layout);
		}else{
			//simple redraw
			gv_refresh(this.vis);			
		}
	},	

	/**
	 * do layout
	 */
	applyLayout: function(layout) {
		if(!layout){
			layout ='ForceDirected';
		}
		this.vis.layout(layout);
	},

	redraw: function(){
		gv_refresh(this.vis);
	},
	
	/**
	 * get node by id
	 */
	getNode: function(nodeId) {
		var cswNode =  gv_getElementById(this.vis, nodeId);
		var cswNodeAdapter = new PrimeFaces.widget.CswNodeAdapter(this, cswNode);
		return cswNodeAdapter;
	},

	/**
	 * get all nodes
	 */
	getNodes: function() {
		var nodes= this.vis.nodes();
		return this._adapt_CswItems(nodes);
	},

	/**
	 * Add a new node
	 * return the new node
	 */
	addNode: function(nodeId, label, x, y, size, shape, color, datas, redraw) {
		var newCswNode = gv_addNode(this.vis, nodeId, label, x, y, size, shape, color, datas);
		var pfNode = new PrimeFaces.widget.CswNodeAdapter(this, newCswNode);
		if(redraw == true){
			gv_refresh(this.vis);
		}
		return pfNode;
	},
				
	/**
	 * Remove node by id
	 * return the removed node
	 */
	removeNode: function(nodeId, redraw) {
		var removedNode = gv_removeNode(this.vis, nodeId);
		var pfRemovedNode = new PrimeFaces.widget.CswNodeAdapter(this, removedNode);	
		if(redraw == true){
			gv_refresh(this.vis);
		}
		return pfRemovedNode;	
	},


	/**
	 * get edge by id
	 */
	getEdge: function(nodeId) {
		var cswEdge =  gv_getElementById(this.vis, nodeId);
		var cswEdgeAdapter = new PrimeFaces.widget.CswEdgeAdapter(this, cswEdge);
		return cswEdgeAdapter;
	},

	/**
	 * get all edges
	 */
	getEdges: function() {
		var edges = this.vis.edges();
		return this._adapt_CswItems(edges);
	},

	/**
	 * Add edge
	 * return the new Edge
	 */
	addEdge: function(edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas, redraw) {
		if(!edgeId){
			edgeId = sourceNodeId + "_" + targetNodeId;
		}
		directed = (directed == true);
		var newCswEdge = gv_addEdge(this.vis, edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas);
		var cswEdgeAdapter = new PrimeFaces.widget.CswEdgeAdapter(this, newCswEdge);
		if(redraw == true){
			gv_refresh(this.vis);
		}
		return cswEdgeAdapter;
	},

		
	/**
	 * Remove edge
	 * return the removed Edge
	 */
	removeEdge: function(edgeId, redraw) {
		var removedEdge = gv_removeEdge(this.vis, edgeId);
		var pfRemovedEdge = new PrimeFaces.widget.CswEdgeAdapter(this, removedEdge);
		if(redraw == true){
			gv_refresh(this.vis);
		}
		return pfRemovedEdge;	
	},

						
	/**
	 * Selection 
	 */
	
	/**
	 * Return an array containing selected elements 
	 */
	getSelectedElements: function() {
		return this._adapt_CswItems(gv_SelectedItems(this.vis));
	},

	/**
	 * Select elements by id 
	 */
	selectElements: function(arrElementsId) {
		return gv_SelectItems(this.vis, arrElementsId);
	},

	/**
	 * Unselect elements by id 
	 */
	deselectElements: function(arrElementsId) {
		return gv_DeselectedItems(this.vis, arrElementsId);
	},

	
	/**
	 * Return an array containing selected Nodes
	 */
	getSelectedNodes: function() {
		return this._adapt_CswItems(gv_SelectedNodes(this.vis));
	},

	/**
	 * Return an array containing selected Edges
	 */
	getSelectedEdges: function() {
		return this._adapt_CswItems(gv_SelectedEdges(this.vis));
	},

	

	/**
	 * Binds the client side event listeners binders
	 */
	addItemClickListener: function(listener){
		var _self = this;
		gv_AddClickItemListener(this.vis, 
			function(cswEvent){
				listener(_self._adapt_CswItem(cswEvent.target));
			}
		);
	},

	addNodeClickListener: function(listener){
		gv_AddOnNodeClickListener(this.vis, 
			function(cswEvent){
				listener(new PrimeFaces.widget.CswNodeAdapter(this, cswEvent.target));
			}
		);
	},

	addEdgeClickListener: function(listener){
		gv_AddOnEdgeClickListener(this.vis, 
			function(cswEvent){
				listener(new PrimeFaces.widget.CswEdgeAdapter(this, cswEvent.target));
			}
		);
	},

	addItemDblClickListener: function(listener){
		var _self = this;
		gv_AddDblclickItemListener(this.vis, 
			function(cswEvent){
				listener(_self._adapt_CswItem(cswEvent.target));
			}
		);
	},

	addNodeDblClickListener: function(listener){
		gv_AddOnNodeDblclickListener(this.vis, 
			function(cswEvent){
				listener(new PrimeFaces.widget.CswNodeAdapter(this, cswEvent.target));
			}
		);
	},

	addEdgeDblClickListener: function(listener){
		gv_AddOnEdgeDblclickListener(this.vis, 
			function(cswEvent){
				listener(new PrimeFaces.widget.CswEdgeAdapter(this, cswEvent.target));
			}
		);
	},

	
	addItemSelectionListener: function(listener){
		var _self = this;
		gv_AddOnSelectItemListener(this.vis, 
			function(cswEvent){
				listener(_self._adapt_CswItems(cswEvent.target));
			}
		);
	},
	
	addNodeSelectionListener: function(listener){
		var _self = this;
		gv_AddOnSelectNodeListener(this.vis, 
			function(cswEvent){
				listener(_self._adapt_CswItems(cswEvent.target));
			}
		);
	},

	addEdgeSelectionListener: function(listener){
		var _self = this;
		gv_AddOnSelectEdgeListener(this.vis, 
			function(cswEvent){
				listener(_self._adapt_CswItems(cswEvent.target));
			}
		);
	},

	
	addItemDeselectionListener: function(listener){
		var _self = this;
		gv_AddOnDeselectItemListener(this.vis, 
			function(cswEvent){
				listener(_self._adapt_CswItems(cswEvent.target));
			}
		);
	},

	addNodeDeselectionListener: function(listener){		
		var _self = this;
		gv_AddOnDeselectNodeListener(this.vis, 
			function(cswEvent){
				listener(_self._adapt_CswItems(cswEvent.target));
			}
		);
	},

	addEdgeDeselectionListener: function(listener){
		var _self = this;
		gv_AddOnDeselectEdgeListener(this.vis, 
			function(cswEvent){
				listener(_self._adapt_CswItems(cswEvent.target));
			}
		);
	},

	addAfterLayoutListener: function(listener){
    	this.vis.addListener("layout","", listener);
	},

	addDragDropListener: function(listener){
		var _self = this;
		gv_AddDragStopListener(this.vis, 
			function(cswEvent){
			
				var draggedNodes = [];
			
				var startDraggedIndex = -1;
				var selectedNodes = _self.getSelectedNodes();
				for (var i = 0; i < selectedNodes.length; i++) {						
					var node = selectedNodes[i];
					//is dragged node selected ?
					if(cswEvent.target.data.id == node.getId()){
						startDraggedIndex = i;
					}
					if(startDraggedIndex != -1){
						draggedNodes.push(node);							
					}
				}
				
				if(startDraggedIndex != -1){
					//add prévious selected nodes 
					for (var i = 0; i < startDraggedIndex; i++) {
						draggedNodes.push(selectedNodes[i]);
					}
				}else{
					//add only dragged node
					draggedNodes.push(_self.getNode(cswEvent.target.data.id));
				}
				
				//delegate to listener
				listener(draggedNodes);
			}
		);
	},

	addItemChangeListener: function(listener){
		this.itemChangeListener = listener;
	},
	
	_updateItem: function(adaptedItem, cswItem){
		this.vis.updateData([cswItem]);
		if(this.itemChangeListener){
			this.itemChangeListener(adaptedItem);
		}
	},
	
	_adapt_CswItems : function(arrCswItems){
		arrAdaptedItems = [];
		for(i=0; i < arrCswItems.length; i++){
			arrAdaptedItems.push(this._adapt_CswItem(arrCswItems[i]));
		}
		return arrAdaptedItems;
	},

	_adapt_CswItem : function(cswItem){
		if(cswItem.group == "nodes"){
			return new PrimeFaces.widget.CswNodeAdapter(this, cswItem)			
		}
		if(cswItem.group == "edges"){
			return new PrimeFaces.widget.CswEdgeAdapter(this, cswItem)			
		}
	}

};


/*
 * Cytoscape Node Class
 */
PrimeFaces.widget.CswNodeAdapter = function(cwsGraphAdapter, cwsNode) {
	
	this.cwsGraphAdapter = cwsGraphAdapter;
	this.cwsNode = cwsNode;
};

PrimeFaces.widget.CswNodeAdapter.prototype = { 

	getType: function() {
		return "NODE";
	},

	setId: function(id) {
		this.cwsNode.data.id = id;
	},
	
	getId: function() {
		return this.cwsNode.data.id;
	},

	setLabel: function(label) {
		this.cwsNode.data.label = label;
		this.update();
	},
	
	getLabel: function() {
		return this.cwsNode.data.label;
	},

	//XXX
	//Changement de position d'un noeud déjà créé
	// ne semble pas fonctionner
	setX: function(x) {
		this.cwsNode.x = x;
	},
	
	getX: function() {
		return this.cwsNode.x;
	},

	setY: function(y) {
		this.cwsNode.y = y;
		this.update();
	},
	
	getY: function() {
		return this.cwsNode.y;
	},

	
	setColor: function(color) {
		this.cwsNode.data.color = color;
		this.update();
	},
	
	getColor: function() {
		return this.cwsNode.data.color;
	},

	setSize: function(size) {
		this.cwsNode.data.size = size;
		this.update();
	},
	
	getSize: function() {
		return this.cwsNode.data.size;
	},

	setShape: function(shape) {
		this.cwsNode.data.shape = gv_convertNodeShape(shape);
		this.update();
	},
	
	getShape: function() {
		return this.cwsNode.data.shape;
	},

	setDatas: function(datas) {
	},
	
	getDatas: function(){
		return {};
	},
	
	update: function() {
		this.cwsGraphAdapter._updateItem(this, this.cwsNode);
	},

};

/*
 * Cytoscape Edge Class
 */
PrimeFaces.widget.CswEdgeAdapter = function(cwsGraphAdapter, cwsEdge) {
	
	this.cwsGraphAdapter = cwsGraphAdapter;
	this.cwsEdge = cwsEdge;
};

PrimeFaces.widget.CswEdgeAdapter.prototype = { 

	getType: function() {
		return "EDGE";
	},

	setId: function(id) {
		this.cwsEdge.data.id = id;
	},
	
	getId: function() {
		return this.cwsEdge.data.id;
	},

	setLabel: function(label) {
		this.cwsEdge.data.label = label;
		this.update();
	},
	
	getLabel: function() {
		return this.cwsEdge.data.label;
	},

	setWidth: function(width) {
		this.cwsEdge.data.width = width;
		this.update();
	},
	
	getWidth: function() {
		return this.cwsEdge.data.width;
	},

	setShape: function(shape) {
		this.cwsEdge.data.shape = gv_convertEdgeShape(shape);;
		this.update();
	},
	
	getShape: function() {
		if(this.cwsEdge.data.shape == null){
			return 'NONE';
		}
		return this.cwsEdge.data.shape;
	},

	setColor: function(color) {
		this.cwsEdge.data.color = color;
		this.update();
	},
	
	getColor: function() {
		return this.cwsEdge.data.color;
	},


	setDirected: function(directed) {
		this.cwsEdge.data.directed = directed;
		this.update();
	},
	
	getDirected: function() {
		return this.cwsEdge.data.directed;
	},

	getSource: function() {
		return this.cwsGraphAdapter.getNode(this.cwsEdge.data.source);
	},

	getTarget: function() {
		return this.cwsGraphAdapter.getNode(this.cwsEdge.data.target);
	},

	setDatas: function(datas) {
	},
	
	getDatas: function(){
		return {};
	},

	
	update: function() {
		//this.cwsGraphAdapter.vis.updateData([this.cwsEdge]);
		this.cwsGraphAdapter._updateItem(this, this.cwsEdge);

	},

};
