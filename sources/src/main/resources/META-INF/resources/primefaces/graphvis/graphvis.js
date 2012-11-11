
/**
 * PrimeFaces Graphvis Widget
 * 
 * http://www.sysord.com 
 */
PrimeFaces.widget.Graphvis = PrimeFaces.widget.BaseWidget.extend({
	
	init: function(cfg) {
		this._super(cfg);
	    
	    this.jqId = PrimeFaces.escapeClientId(this.id);
	    this.jq = $(this.jqId);
	    this.cfg.formId = this.jq.parents('form:first').attr('id');
	     	   	    
	    //synchronization structure
	    this.syncStruct = []; 
	    
	    //--------------------------------------------------------
	    // create visualisation and draw model    
	    //--------------------------------------------------------	    
	    this.synchronizeAfterLayout = true;
	    var _self = this;

	    var onVisReadyListener = function(){_self.onVisReady();};
	    var afterLayoutListener = function(){_self.onLayoutApplied();};
	    
	    
//	    this.graphAdapter = new PrimeFaces.widget.CswGraphAdapter(this.cfg.id, cfg.graphModel, null,
//	    						function(){_self.onVisReady();},
//	    						function(){_self.onLayoutApplied();},
//	    						this.cfg.cytoscapeWebPath, this.cfg.flashInstallerPath);
	    
	    var graphAdapterFactory = new PrimeFaces.widget.GraphAdapterFactory();
	    this.graphAdapter = graphAdapterFactory.createGraphAdapter(this.cfg.graphRenderer, this.cfg.id, this.cfg.graphModel, null, onVisReadyListener, 
												afterLayoutListener, this.cfg);			    	    
	    this.graphAdapter.init();
	},
	
	
	redraw: function(){
		this.graphAdapter.redraw();
	},
	
	doLayout: function(layout, synchronizeAfterLayout){
		//force synchronization after Layout even if ajax property is set to false
		if(synchronizeAfterLayout == true){
			this.synchronizeAfterLayout = true;
		}
		this.graphAdapter.applyLayout(layout);
	},
	
	/**
	 * get node by id
	 */
	getNode: function(nodeId) {
		var node =  this.graphAdapter.getNode(nodeId);
		return node;
	},

	/**
	 * return all nodes
	 */
	getNodes: function() {
		return this.graphAdapter.getNodes();
	},
	
	
	/**
	 * Add node
	 */
	addNode: function(nodeId, label, x, y, size, shape, color, datas, redraw) {
		var newNode = this._addNode(nodeId, label, x, y, size, shape, color, datas, redraw);
		if(this.cfg.ajax){
			this.synchronize();
		}
		return newNode;
	},
	
	_addNode: function(nodeId, label, x, y, size, shape, color, datas, redraw) {
		var newNode = this.graphAdapter.addNode(nodeId, label, x, y, size, shape, color, datas, redraw);
		//log action
		this.syncStruct.push({actionType:"ADD_NODE",target: this._toGenericNode(newNode)});
		return newNode;
	},
	
	
	/**
	 * Remove node
	 */
	removeNode: function(nodeId, redraw) {
		var removedNode = this._removeNode(nodeId, redraw);
		if(this.cfg.ajax){
			this.synchronize();
		}
		return removedNode;	
	},

	_removeNode: function(nodeId, redraw) {
		var removedNode = this.graphAdapter.removeNode(nodeId, redraw);
		//log action
		this.syncStruct.push({actionType:"REMOVE_NODE", targetId:nodeId});
		return removedNode;	
	},

	
	/**
	 * get edge by id
	 */
	getEdge: function(edgeId) {
		var edge =  this.graphAdapter.getEdge(edgeId);
		return edge;
	},

	/**
	 * return all edges
	 */
	getEdges: function() {
		return this.graphAdapter.getEdges();
	},


	/**
	 * Add edge
	 */
	addEdge: function(edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas, redraw) {
		var newEdge = this._addEdge(edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas, redraw);
		if(this.cfg.ajax){
			this.synchronize();
		}
		return newEdge;
	},

	_addEdge: function(edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas, redraw) {
		var newEdge = this.graphAdapter.addEdge(edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas, redraw);
		//log action
		this.syncStruct.push({actionType:"ADD_EDGE", target:this._toGenericEdge(newEdge)});
		return newEdge;
	},
	
	
	/**
	 * Remove edge
	 */
	removeEdge: function(edgeId, redraw) {
		var removedEdge = this._removeEdge(edgeId, redraw);
		if(this.cfg.ajax){
			this.synchronize();
		}
		return removedEdge;
	},

	_removeEdge: function(edgeId, redraw) {
		var removedEdge = this.graphAdapter.removeEdge(edgeId, redraw);
		//log action
		this.syncStruct.push({actionType:"REMOVE_EDGE", targetId:edgeId});
		return removedEdge;
	},

	
	/**
	 * Remove elements (nodes or edges)
	 */
	removeElements: function(arrElements) {
		for(var i=0; i < arrElements.length; i++){
			element = arrElements[i];
			if(element.getType() == "NODE"){
				this._removeNode(element.getId(), false);
			}else if(element.getType() == "EDGE"){
				this._removeEdge(element.getId(), false);			
			}
		}

		if(this.cfg.ajax){
			this.synchronize();
		}
		return arrElements;
	},
	
	
	/**
	 * Selection
	 */
	getSelectedElements: function() {
		return this.graphAdapter.getSelectedElements();
	},
	selectElements: function(arrElements) {
		return this.graphAdapter.selectElements(arrElements);
	},
	deselectElements: function(arrElements) {
		return this.graphAdapter.deselectElements(arrElements);
	},
	
	getSelectedNodes: function() {
		return this.graphAdapter.getSelectedNodes();
	},

	getSelectedEdges: function() {
		return this.graphAdapter.getSelectedEdges();
	},
	

	/**
	 * request graph model from the server and render it
	 */
	reload: function(){
		$(this.jqId).css('visibility', 'hidden');
		//remove all nodes and edges
		this.removeElements(this.graphAdapter.getEdges());
		this.removeElements(this.graphAdapter.getNodes());
		//flush syncStruct: don't apply change to server
		 this.syncStruct = [];
		//request synchronization
		 this.synchronize(true, true);
	},
	
	/**
	 * Synchronization with server model
	 * 
	 * @param: hide:  if true, graph is hidden during synchronization
	 * @param: all: request synchronization for all elements (full model synchronization)
	 */
	synchronize: function(hide, all) {
		var options = {
				source: this.id,
			    update: this.cfg.update,
			    process: this.id,
			    formId: this.cfg.formId
		};
		
		var _self = this;

		if(!all){
			all = false;
		}
		
		if(hide){
			$(this.jqId).css('visibility', 'hidden');
		}
		
		//see datatable.js
		//onSuccess callBack
		options.onsuccess = function(responseXML) {
		    //receive syncStruct for client side model synchronization
	        var xmlDoc = $(responseXML.documentElement);                
	        var callbackParams = xmlDoc.find('extension[ln="primefaces"][type="args"]')
	        var params = callbackParams.length > 0 ? $.parseJSON(callbackParams.text()) : {};
	        _self.synchronizeClientSide($.parseJSON(params[_self.id + "_synchronizeModel"]));
	        
	        //process updates
            updates = xmlDoc.find("update");
            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i);
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, update.attr('id'), update.text());
            }
	        	        
	        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
	        
	        return true;
	    };
	    
	    options.oncomplete = function(){
	    	$(_self.jqId).css('visibility', 'visible');
	    }; 
	    
	    //send syncStruct for server side model synchronization
	    var params = {};
	    options.params = params;	    
	    options.params = [
	                      {name: this.id + "_synchronizeModel", value: JSON.stringify(this.syncStruct)},
	                      {name: "synchronizeAll", value: "" + all},
                      ];
	    
	    PrimeFaces.ajax.AjaxRequest(options);
	    this.syncStruct = [];
	    
	},

	
	
	/**
	 * Apply graph synchronization using received server syncStruct
	 */
	synchronizeClientSide: function(serverSyncStruct) {
		for(var i=0; i < serverSyncStruct.length; i++) {
	        var action = serverSyncStruct[i];
	        var actionType = action.actionType;
	        if(actionType == 'ADD_NODE'){
		        var target = action.target;
	        	var newNode = this._addNode(target.id, target.label, target.x, target.y, target.size, target.shape, target.color, target.datas, false);	        	
	        }else if(actionType == 'UPDATE_NODE'){
		        var targetNode = this.graphAdapter.getNode(action.target.id);
		        if(targetNode){
			        var target = action.target;
		        	targetNode.setLabel(target.label);
		        	targetNode.setX(target.x);
		        	targetNode.setY(target.y);
		        	targetNode.setSize(target.size);
		        	targetNode.setShape(target.shape);
		        	targetNode.setColor(target.color);
		        	targetNode.setDatas(target.datas);		        	
		        }
	        }else if(actionType == 'REMOVE_NODE'){
	        	this._removeNode(action.targetId, false);        	
	        }else if(actionType == 'ADD_EDGE'){
		        var target = action.target;
		        var newEdge = this._addEdge(target.id, target.label, target.sourceNodeId, target.targetNodeId, target.directed, target.width, target.color, target.datas, false);
		        newEdge.setShape(target.shape);
	        }else if(actionType == 'UPDATE_EDGE'){
		        var targetEdge = this.graphAdapter.getEdge(action.target.id);
		        if(targetEdge){
			        var target = action.target;
		        	targetEdge.setLabel(target.label);
		        	targetEdge.setDirected(target.directed);
		        	targetEdge.setWidth(target.width);
		        	targetEdge.setColor(target.color);
		        	targetEdge.setShape(target.shape);
		        	targetEdge.setDatas(target.datas);		        	
		        }
	        
	        }else if(actionType == 'REMOVE_EDGE'){
	        	this._removeEdge(action.targetId, false);
	        }else if(actionType == 'DO_LAYOUT'){
	        	//when layout is applied all nodes position must be 
	        	//updated on the server side
	        	this.graphAdapter.applyLayout(action.targetId);
	        } 
	    }
		
		this.syncStruct = [];
		this.graphAdapter.redraw();
				
	},


	
	/**
	 * Setup listeners on Cytoscape notification
	 */
	onVisReady: function() {
		var _self = this;
		this.graphAdapter.addItemChangeListener(function(item){_self.onItemChange(item)});
		this.bindClientSideListeners();
		if(this.cfg.behaviors)this.bindAjaxBeahviorsListeners();
		this.bindSynchronizationListeners();
	},

	/**
	 * Layout listener
	 */
	onLayoutApplied: function() {
		this._createSynchronizeAllStruct();
		if(this.syncStruct.length == 0 ) return;
		//synchronize if ajax enabled or first layout
		if(this.cfg.ajax || this.synchronizeAfterLayout){
			this.synchronize();
			this.synchronizeAfterLayout = false;
		}

	},
	
	onItemChange: function(item){
		if(item.getType() == "NODE"){
			this.syncStruct.push({actionType:"UPDATE_NODE", target: this._toGenericNode(item)});							
		}else if(item.getType() == "EDGE"){
			this.syncStruct.push({actionType:"UPDATE_EDGE", target: this._toGenericEdge(item)});							
		}
	},
	
	/**
	 * send all node update to server 
	 */
	synchronizeAll: function() {
		this._createSynchronizeAllStruct();
		this.synchronize();		
	},

	_createSynchronizeAllStruct: function() {
		var nodes = this.graphAdapter.getNodes();
		if(nodes.length == 0) return;
		this.syncStruct = [];		
		for (var i = 0; i < nodes.length; i++) {
			var node = nodes[i];
			this.syncStruct.push({actionType:"UPDATE_NODE", target: this._toGenericNode(node)});
		}

		var edges = this.graphAdapter.getEdges();
		for (var i = 0; i < edges.length; i++) {
			var edge = edges[i];
			this.syncStruct.push({actionType:"UPDATE_EDGE", target: this._toGenericEdge(edge)});
		}
		
	},
	
	/**
	 * Binds the client side event listeners for automatic synchronization
	 * onDrag stop nodes positions are updated
	 */
	bindSynchronizationListeners: function() {
		var _self = this;
		//FIXME: create a generic drapdrop event
		this.graphAdapter.addDragDropListener( 
				function(arrDraggedNodes){

					for (var i = 0; i < arrDraggedNodes.length; i++) {						
						var draggedNode = arrDraggedNodes[i];
						_self.syncStruct.push({actionType:"UPDATE_NODE", target: _self._toGenericNode(draggedNode)});							
					}
					
					//synchronize only if ajax = true
					if(_self.cfg.ajax){
						_self.synchronize();
					}
										
//					var startSync = -1;
//					//update all  dragged nodes
//					var selectedNodes = _self.graphAdapter.getSelectedNodes();
//					for (var i = 0; i < selectedNodes.length; i++) {						
//						var node = selectedNodes[i];
//						//is dragged node selected ?
//						if(event.target.data.id == node.getId()){
//							startSync = i;
//						}
//						if(startSync != -1){
//							_self.syncStruct.push({actionType:"UPDATE_NODE", target: _self._toGenericNode(node)});							
//						}
//					}					
//					if(startSync != -1){
//						//update prévious selected nodes 
//						for (var i = 0; i < startSync; i++) {						
//							_self.syncStruct.push({actionType:"UPDATE_NODE", target: _self._toGenericNode(selectedNodes[i])});							
//						}
//					}else{
//						//update only dragged node
//						var draggedNode = _self.graphAdapter.getNode(event.target.data.id);
//						_self.syncStruct.push({actionType:"UPDATE_NODE", target:_self._toGenericNode(draggedNode)});						
//					}
//					
//					//synchronize only if ajax = true
//					if(_self.cfg.ajax){
//						_self.synchronize();
//					}
			}
		);
	},

	
	
	/**
	 * Binds the client side event listeners 
	 */
	bindClientSideListeners: function() {
		var _self = this;
		//item click
	    if(_self.cfg.onItemClick) {
	    	this.graphAdapter.addItemClickListener(_self.cfg.onItemClick);
	    }
		//node click
	    if(_self.cfg.onNodeClick) {
	    	this.graphAdapter.addNodeClickListener(_self.cfg.onNodeClick);
	    }
		//edge click
	    if(_self.cfg.onEdgeClick) {
	    	this.graphAdapter.addEdgeClickListener(_self.cfg.onEdgeClick);
	    }
		
	    //item dblClick
	    if(_self.cfg.onItemDblclick) {
	    	this.graphAdapter.addItemDblClickListener(_self.cfg.onItemDblclick);
	    }
		//node dblClick
	    if(_self.cfg.onNodeDblclick) {
	    	this.graphAdapter.addNodeDblClickListener(_self.cfg.onNodeDblclick);
	    }
		//edge dblClick
	    if(_self.cfg.onEdgeDblclick) {
	    	this.graphAdapter.addEdgeDblClickListener(_self.cfg.onEdgeDblclick);
	    }

	    //item selection
	    if(_self.cfg.onItemSelection) {
	    	this.graphAdapter.addItemSelectionListener(_self.cfg.onItemSelection);
	    }
		//node selection
	    if(_self.cfg.onNodeSelection) {
	    	this.graphAdapter.addNodeSelectionListener(_self.cfg.onNodeSelection);
	    }
		//edge selection
	    if(_self.cfg.onEdgeSelection) {
	    	this.graphAdapter.addEdgeSelectionListener(_self.cfg.onEdgeSelection);
	    }

	    //item unselection
	    if(_self.cfg.onItemUnselection) {
	    	this.graphAdapter.addItemDeselectionListener(_self.cfg.onItemUnselection);
	    }
		//node unselection
	    if(_self.cfg.onNodeUnselection) {
	    	this.graphAdapter.addNodeDeselectionListener(_self.cfg.onNodeUnselection);
	    }
		//edge unselection
	    if(_self.cfg.onEdgeUnselection) {
	    	this.graphAdapter.addEdgeDeselectionListener(_self.cfg.onEdgeUnselection);
	    } 
	},

	/**
	 * Binds Ajax behaviors client side listeners 
	 */
	bindAjaxBeahviorsListeners: function() {
		var _self = this;

		
		if(this.cfg.behaviors['select']){
			this.graphAdapter.addItemSelectionListener(function(items){
						_self.fireSelectUnselectEvent('select', _self._ArrItemsToIds(items));
			});
		}

		if(this.cfg.behaviors['nodeSelect']){
			this.graphAdapter.addNodeSelectionListener(function(nodes){
						_self.fireSelectUnselectEvent('nodeSelect', _self._ArrItemsToIds(nodes));
			});
		}

		if(this.cfg.behaviors['edgeSelect']){
			this.graphAdapter.addEdgeSelectionListener(function(edges){
						_self.fireSelectUnselectEvent('edgeSelect', _self._ArrItemsToIds(edges));
			});
		}

		if(this.cfg.behaviors['unselect']){
			this.graphAdapter.addItemDeselectionListener(function(items){
						_self.fireSelectUnselectEvent('unselect', _self._ArrItemsToIds(items));
			});
		}

		if(this.cfg.behaviors['nodeUnselect']){
			this.graphAdapter.addNodeDeselectionListener(function(nodes){
						_self.fireSelectUnselectEvent('nodeUnselect', _self._ArrItemsToIds(nodes));
			});
		}

		if(this.cfg.behaviors['edgeUnselect']){
			this.graphAdapter.addEdgeDeselectionListener(function(edges){
						_self.fireSelectUnselectEvent('edgeUnselect', _self._ArrItemsToIds(edges));
			});
		}

	},
	
	/**
	 *  Sends a Select or Unselect Event on server side to invoke a Listener if defined
	 * (Ajax Behaviors)  
	 */
	fireSelectUnselectEvent: function(behaviorsName, arrSelectedItems) {
	    if(this.cfg.behaviors) {
	        var selectUnselectBehavior = this.cfg.behaviors[behaviorsName];
	        
	        if(selectUnselectBehavior) {
	            var ext = {};
	            ext.params = [
	                          {name: this.id + '_itemIds', value: arrSelectedItems}
	                          ];
	            selectUnselectBehavior.call(this, arrSelectedItems, ext);
	        }
	    }
	},	
	
	
	/**
	 * create a comma separated String containing each item id.
	 */
	_ArrItemsToIds : function(arrItems){
		var itemIds = "";
		for(i=0; i<arrItems.length; i++){
			itemIds += arrItems[i].getId() + ","
		}
		return itemIds.substring(0, itemIds.length - 1);
	},

	/**
	 * convert adapted nodes to generic nodes for exchange with server
	 */
	_toGenericNodes: function(arrNodes){
		var genNodes =[];
		for(var i = 0; i < arrNodes.length, i++;){
			genNodes.push(this._toGenericNode(arrNodes[i]));
		}
		return genNodes;
	},

	/**
	 * convert adapted node to generic structure for exchange with server
	 */
	_toGenericNode: function(node){
		return {id:node.getId(), label:node.getLabel(), x:node.getX(), y:node.getY(), size:node.getSize(), shape:node.getShape().toUpperCase(), color:node.getColor(), datas:node.getDatas()};
	},

	/**
	 * convert adapted edges to generic edges for exchange with server
	 */
	_toGenericEdges: function(arrEdges){
		var genEdges =[];
		for(var i = 0; i < arrEdges.length, i++;){
			genEdges.push(this._toGenericEdge(arrEdges[i]));
		}
		return genEdges;
	},

	/**
	 * convert adapted edge to generic edge for exchange with server
	 */
	_toGenericEdge: function(edge){
		return {id:edge.getId(), label:edge.getLabel(), sourceNodeId:edge.getSource().getId(), targetNodeId:edge.getTarget().getId(), 
				directed:edge.getDirected(),  width: edge.getWidth() , color:edge.getColor(), shape:edge.getShape().toUpperCase(), datas:edge.getDatas()};
	},

	
});












