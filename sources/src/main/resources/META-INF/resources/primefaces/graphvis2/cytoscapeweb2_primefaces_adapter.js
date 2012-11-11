/*
 * Cytoscape Graph Class
 */

/**
 * CytoscapeWeb 2  Adapter Classes
 * 
 * http://www.sysord.com 
 */

PrimeFaces.widget.Csw2GraphAdapter = function(divId, graphModel, visualStyle, onVisReadyListener, afterLayoutListener) {
	

	this.divId = divId;
	this.graphModel = graphModel;
	
	//set visualStyle or create default visualStyle
	if(visualStyle){
		this.visualStyle = visualStyle;
	}else{
		//create default visual style		
		this.visualStyle = cytoscape.stylesheet()
						.selector("node").css({
						//label
						"content": "data(label)",
						//shapes
						shape: "data(shape)",
						//sizes
						width: "data(size)",
						height: "data(size)",
						//colors
						"background-color": "data(color)",
						"cursor": "pointer",
					})	
					.selector("edge").css({
						//label
						"content": "data(label)",
						//colors
						'line-color' : "data(color)",
						'target-arrow-color' : "data(color)",
						'target-arrow-shape' : "data(shape)",
						'width' :  "data(width)", 
						"cursor": "pointer",
					})
					.selector(":selected")
					.css({
						"border-color": "yellow",
						"line-color": "yellow",
						"border-width": 2,
						"source-arrow-color": "#000",
						"target-arrow-color": "#000"
					})
					;			
	};

	if(onVisReadyListener){
		this.onVisReadyListener = onVisReadyListener;
	}
	
	if(afterLayoutListener){
		this.afterLayoutListener = afterLayoutListener;
	}
	
	//create predifined layouts
	this._createLayouts();
			    
};

PrimeFaces.widget.Csw2GraphAdapter.prototype = { 

	init: function(){
	    //--------------------------------------------------------
	    // create visualisation and draw model    
	    //--------------------------------------------------------
		var _self = this;

		//draw an empty graph 
		$(PrimeFaces.escapeClientId(this.divId)).cytoscape({
		
			elements: {
				nodes: [],
				edges: [],									
			},
			
			style: this.visualStyle,
			
			ready: function(){
						_self.vis = this;

						//attach after layout listener
					    if(_self.afterLayoutListener){
					    	_self.addAfterLayoutListener(_self.afterLayoutListener);
					    }

				    	_self._convertGraphModel(_self.graphModel);
				    	if(_self.onVisReadyListener){
				    		_self.onVisReadyListener();
				    	}
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
			//gv_refresh(this.vis);			
		}
	},	

	/**
	 * do layout
	 */
	applyLayout: function(layoutName) {
		if(!layoutName){
			layoutName ='arborLayout';
		}
		var layout = this.layouts[layoutName];
		if(!layout){
			layout = this.layouts.randomLayout;
		}
		this.vis.layout(layout);
	},

	redraw: function(){
		gv2_refresh(this.vis);
	},
	
	/**
	 * get node by id
	 */
	getNode: function(nodeId) {
		var cswNode =  gv2_getNodeById(this.vis, nodeId);
		var cswNodeAdapter = new PrimeFaces.widget.Csw2NodeAdapter(this, cswNode);
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
		var newCswNode = gv2_addNode(this.vis, nodeId, label, x, y, size, shape, color, datas);
		var pfNode = new PrimeFaces.widget.Csw2NodeAdapter(this, newCswNode);
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
		var removedNode = gv2_removeNode(this.vis, nodeId);
		var pfRemovedNode = new PrimeFaces.widget.Csw2NodeAdapter(this, removedNode);	
		if(redraw == true){
			gv_refresh(this.vis);
		}
		return pfRemovedNode;	
	},


	/**
	 * get edge by id
	 */
	getEdge: function(edgeId) {
		var cswEdge =  gv2_getEdgeById(this.vis, edgeId);
		var cswEdgeAdapter = new PrimeFaces.widget.Csw2EdgeAdapter(this, cswEdge);
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
		var newCswEdge = gv2_addEdge(this.vis, edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas);
		var cswEdgeAdapter = new PrimeFaces.widget.Csw2EdgeAdapter(this, newCswEdge);
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
		var removedEdge = gv2_removeEdge(this.vis, edgeId);
		var cswEdgeAdapter = new PrimeFaces.widget.Csw2EdgeAdapter(this, removedEdge);
		if(redraw == true){
			gv_refresh(this.vis);
		}
		return cswEdgeAdapter;	
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
		return gv2_SelectItems(this.vis, arrElementsId);
	},

	/**
	 * Unselect elements by id 
	 */
	deselectElements: function(arrElementsId) {
		return gv2_DeselectedItems(this.vis, arrElementsId);
	},

	
	/**
	 * Return an array containing selected Nodes
	 */
	getSelectedNodes: function() {
		return this._adapt_CswItems(gv2_SelectedNodes(this.vis));
	},

	/**
	 * Return an array containing selected Edges
	 */
	getSelectedEdges: function() {
		console.log(gv2_SelectedEdges(this.vis));
		return this._adapt_CswItems(gv2_SelectedEdges(this.vis));
	},

	

	/**
	 * Binds the client side event listeners binders
	 */
	addItemClickListener: function(listener){
		var _self = this;
		gv2_AddClickItemListener(this.vis, 
			function(cswEvent){
				var item = this;
				listener(_self._adapt_CswItem(item));
			}
		);
	},

	addNodeClickListener: function(listener){
		gv2_AddOnNodeClickListener(this.vis, 
			function(cswEvent){
				var node = this;
				listener(new PrimeFaces.widget.Csw2NodeAdapter(this, node));
			}
		);
	},

	addEdgeClickListener: function(listener){
		gv2_AddOnEdgeClickListener(this.vis, 
			function(cswEvent){
				var edge = this;
				listener(new PrimeFaces.widget.Csw2EdgeAdapter(this, edge));
			}
		);
	},

	addItemDblClickListener: function(listener){
//		var _self = this;
//		gv_AddDblclickItemListener(this.vis, 
//			function(cswEvent){
//				listener(_self._adapt_CswItem(cswEvent.target));
//			}
//		);
		console.log("sorry, CytoscapeWeb 2 don't provide listener ItemDblClickListener.")
	},

	addNodeDblClickListener: function(listener){
//		gv_AddOnNodeDblclickListener(this.vis, 
//			function(cswEvent){
//				listener(new PrimeFaces.widget.Csw2NodeAdapter(this, cswEvent.target));
//			}
//		);
		console.log("sorry, CytoscapeWeb 2 don't provide listener NodeDblClickListener.")
	},

	addEdgeDblClickListener: function(listener){
//		gv_AddOnEdgeDblclickListener(this.vis, 
//			function(cswEvent){
//				listener(new PrimeFaces.widget.Csw2EdgeAdapter(this, cswEvent.target));
//			}
//		);
		console.log("sorry, CytoscapeWeb 2 don't provide listener EdgeDblClickListener.")
	},

	
	addItemSelectionListener: function(listener){
		var _self = this;
		gv2_AddOnSelectItemListener(this.vis, 
			function(cswEvent){
				var items = this;
				listener(_self._adapt_CswItems(items));
			}
		);
	},
	
	addNodeSelectionListener: function(listener){
		var _self = this;
		gv2_AddOnSelectNodeListener(this.vis, 
			function(cswEvent){
				var nodes = this;
				listener(_self._adapt_CswItems(nodes));
			}
		);
	},

	addEdgeSelectionListener: function(listener){
		var _self = this;
		gv2_AddOnSelectEdgeListener(this.vis, 
			function(cswEvent){
				var edges = this;
				listener(_self._adapt_CswItems(edges));
			}
		);
	},

	
	addItemDeselectionListener: function(listener){
		var _self = this;
		gv2_AddOnDeselectItemListener(this.vis, 
			function(cswEvent){
				var items = this;
				listener(_self._adapt_CswItems(items));
			}
		);
	},

	addNodeDeselectionListener: function(listener){		
		var _self = this;
		gv2_AddOnDeselectNodeListener(this.vis, 
			function(cswEvent){
				var nodes = this;
				listener(_self._adapt_CswItems(nodes));
			}
		);
	},

	addEdgeDeselectionListener: function(listener){
		var _self = this;
		gv2_AddOnDeselectEdgeListener(this.vis, 
			function(cswEvent){
				var edges = this;
				listener(_self._adapt_CswItems(edges));
			}
		);
	},

	addDragDropListener: function(listener){
		var _self = this;
		_self.draggedNodes = [];
		gv2_AddDragStopListener(this.vis, 
			function(cswEvent){
				//unfortunately: one event by dragged node   
				var item = this;
				_self.draggedNodes.push(new PrimeFaces.widget.Csw2NodeAdapter(_self, item));
				//if it's the last dragged node: fire event
				if(_self.vis.elements('node:grabbed').length == 0){
					listener(_self.draggedNodes);					
					_self.draggedNodes = [];
				}
			}
		);
	},

	addAfterLayoutListener: function(listener){
		this.vis.on('layoutready', '', undefined, listener);
	},
	
	addItemChangeListener: function(listener){
		this.itemChangeListener = listener;
	},
	
	_updateItem: function(adaptedItem, cswItem){
		cswItem.updateStyle();
		//this.vis.updateData([cswItem]);
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
		if(cswItem._private.group == "nodes"){
			return new PrimeFaces.widget.Csw2NodeAdapter(this, cswItem)			
		}
		if(cswItem._private.group == "edges"){
			return new PrimeFaces.widget.Csw2EdgeAdapter(this, cswItem)			
		}
	},
	
	
	_createLayouts: function(){
		this.layouts = {};
		
  		this.layouts.randomLayout = {
    		      name : 'random',
              ready: undefined, // callback on layoutready
              stop: undefined, // callback on layoutstop
              fit: true // whether to fit to viewport
        };

  		this.layouts.arborLayout = {
    		    name : 'arbor',
            liveUpdate: true, // whether to show the layout as it's running
            ready: undefined, // callback on layoutready 
            stop: undefined, // callback on layoutstop
            maxSimulationTime: 1000, // max length in ms to run the layout
            fit: true, // fit to viewport
            padding: [ 50, 50, 50, 50 ], // top, right, bottom, left
            ungrabifyWhileSimulating: true, // so you can't drag nodes during layout
        
            // forces used by arbor (use arbor default on undefined)
            repulsion: undefined,
            stiffness: undefined,
            friction: undefined,
            gravity: true,
            fps: undefined,
            precision: undefined,
        
            // static numbers or functions that dynamically return what these
            // values should be for each element
            nodeMass: undefined, 
            edgeLength: undefined,
        
            stepSize: 1, // size of timestep in simulation
        
            // function that returns true if the system is stable to indicate
            // that the layout can be stopped
            stableEnergy: function( energy ){
                var e = energy; 
                return (e.max <= 0.5) || (e.mean <= 0.3);
            }
        };

  		this.layouts.gridLayout = {
            name : 'grid',
            fit: true, // whether to fit the viewport to the graph
            rows: undefined, // force num of rows in the grid
            columns: undefined, // force num of cols in the grid
            ready: undefined, // callback on layoutready
            stop: undefined // callback on layoutstop
        };

	}


};


/*
 * Cytoscape Node Class
 */
PrimeFaces.widget.Csw2NodeAdapter = function(cwsGraphAdapter, cwsNode) {
	
	this.cwsGraphAdapter = cwsGraphAdapter;
	this.cwsNode = cwsNode;
};

PrimeFaces.widget.Csw2NodeAdapter.prototype = { 

	getType: function() {
		return "NODE";
	},

	setId: function(id) {
		this.cwsNode._private.data.id = id;
	},
	
	getId: function() {
		return this.cwsNode._private.data.id;
	},

	setLabel: function(label) {
		this.cwsNode._private.data.label = label;
		this.update();
	},
	
	getLabel: function() {
		return this.cwsNode._private.data.label;
	},

	setX: function(x) {
		this.cwsNode._private.position.x = x;
		this.update();
	},
	
	getX: function() {
		return this.cwsNode._private.position.x;
	},

	setY: function(y) {
		this.cwsNode._private.position.y = y;
		this.update();
	},
	
	getY: function() {
		return this.cwsNode._private.position.y;
	},

	
	setColor: function(color) {
		this.cwsNode._private.data.color = color.toLowerCase();
		this.update();
	},
	
	getColor: function() {
		return this.cwsNode._private.data.color;
	},

	setSize: function(size) {
		this.cwsNode._private.data.size = size;
		this.update();
	},
	
	getSize: function() {
		return this.cwsNode._private.data.size;
	},

	setShape: function(shape) {
		this.cwsNode._private.data.shape = gv2_convertNodeShape(shape);
		this.update();
	},
	
	getShape: function() {
		return this.cwsNode._private.data.shape;
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
PrimeFaces.widget.Csw2EdgeAdapter = function(cwsGraphAdapter, cwsEdge) {
	
	this.cwsGraphAdapter = cwsGraphAdapter;
	this.cwsEdge = cwsEdge;
};

PrimeFaces.widget.Csw2EdgeAdapter.prototype = { 

	getType: function() {
		return "EDGE";
	},

	setId: function(id) {
		this.cwsEdge.data.id = id;
	},
	
	getId: function() {
		return this.cwsEdge._private.data.id;
	},

	setLabel: function(label) {
		this.cwsEdge._private.data.label = label;
		this.update();
	},
	
	getLabel: function() {
		return this.cwsEdge._private.data.label;
	},

	setWidth: function(width) {
		this.cwsEdge._private.data.width = width;
		this.update();
	},
	
	getWidth: function() {
		return this.cwsEdge._private.data.width;
	},

	setShape: function(shape) {
		this.cwsEdge._private.data.shape = gv2_convertEdgeShape(shape);
		this.update();
	},
	
	getShape: function() {
		if(this.cwsEdge._private.data.shape == null){
			return 'none';
		}
		return this.cwsEdge._private.data.shape;
	},

	setColor: function(color) {
		this.cwsEdge._private.data.color = color.toLowerCase();
		this.update();
	},
	
	getColor: function() {
		return this.cwsEdge._private.data.color;
	},


	setDirected: function(directed) {
		this.cwsEdge._private.data.directed = directed;
		if(directed == true){
			this.setShape('triangle');
		}else{
			this.setShape(undefined);			
		}
	},
	
	getDirected: function() {
		return this.cwsEdge._private.data.directed;
	},

	getSource: function() {
		return this.cwsGraphAdapter.getNode(this.cwsEdge._private.data.source);
	},

	getTarget: function() {
		return this.cwsGraphAdapter.getNode(this.cwsEdge._private.data.target);
	},

	setDatas: function(datas) {
	},
	
	getDatas: function(){
		return {};
	},

	
	update: function() {
		this.cwsGraphAdapter._updateItem(this, this.cwsEdge);
	},

};
