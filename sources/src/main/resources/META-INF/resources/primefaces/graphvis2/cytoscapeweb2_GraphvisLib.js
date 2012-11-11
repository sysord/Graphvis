

/**
 * Utility function for CystoscapeWeb 2 graph manipulation 
 * 
 * http://www.sysord.com 
 */

//-------------------------------------------------
// Draw Graph:
//	div_od: id element graph location
//	jsonGraphModel: graph model provided as json
//-------------------------------------------------
function gv2_drawgraph(div_id, jsonGraphModel) {

    // initialization options
    var options = {
        // where you have the Cytoscape Web SWF
        swfPath: "graphlib/CytoscapeWeb",
        // where you have the Flash installer SWF
        flashInstallerPath: "graphlib/playerProductInstall"
    };        
    // init and draw
    var vis = new org.cytoscapeweb.Visualization(div_id, options);
    vis.draw({ network: jsonGraphModel });
    return vis;
}

function gv2_refresh(gv) {
	gv.renderer().redraw();
}


function gv2_addNode(gv, nodeId, label, x, y, size, shape, color, datas) {
	if(!nodeId){
		nodeId = "N" + gv.nodes().length;
	}
	var nodeX = 0; if(x) nodeX = x;
	var nodeY = 0; if(y) nodeY = y;
	var nodeDatas = gv2_prepareNodeElement(nodeId, label, size, shape, color, datas);
	var collNodes = gv.add({ group: "nodes", data: nodeDatas, position: { x: nodeX, y: nodeY } });
	var node = collNodes[0];
	//node.updateStyle();
	return node;
}

function gv2_removeNode(gv, nodeId) {
	var node = gv2_getNodeById(gv, nodeId);
	if(node) gv.remove(node); 
	return node;
}

function gv2_addEdge(gv, edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas) {
	if(!edgeId){
		edgeId = sourceNodeId + "_" + targetNodeId;
	}
	var edgeDatas = gv2_prepareEdgeElement(edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas);
	var collEdges = gv.add({ group: "edges", data: edgeDatas});
	var edge = collEdges[0];
	//edge.updateStyle();
	return edge;
}

function gv2_removeEdge(gv, edgeId) {
	var edge = gv2_getEdgeById(gv, edgeId);
	if(edge) gv.remove(edge); 
	return edge;
}

function gv2_removeElements(gv, gvElements) {
	//TODO: use remove(collection)
	//return gv.removeElements(gvElements);
}



//private utility function
function gv2_prepareNodeElement(nodeId, label, size, shape, color, datas) {
	var elementDatas;
	if(datas){
		elementDatas = datas;
	}else{
		elementDatas = {};		
	}
	
	if(size) elementDatas.size = size;
	if(shape) elementDatas.shape = gv2_convertNodeShape(shape);
	if(color) elementDatas.color = color.toLowerCase();	
	elementDatas.id = nodeId;
	elementDatas.label = label;
	return elementDatas;
}

function gv2_prepareEdgeElement(edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas) {
	var edgeDatas;
	if(datas){
		edgeDatas = datas;
	}else{
		edgeDatas = {};		
	}
	edgeDatas.id = edgeId;
	edgeDatas.label = (label == null ? "" : label) ;
	edgeDatas.source = sourceNodeId;
	edgeDatas.target = targetNodeId;
	edgeDatas.directed = (directed == true);
	if(edgeDatas.directed == true) edgeDatas.shape = 'triangle';
	if(width) edgeDatas.width = width;
	if(color) edgeDatas.color = color.toLowerCase();
	
	return edgeDatas;
}


//-----------------------------------
// select gv graph items where their ids
// are present in the array arrItemIds
//-----------------------------------
function gv2_SelectItems(gv, arrItemIds) {
	arrItemIds.map(function(itemId){gv2_getElementById(gv, itemId).select()})
}

function gv2_DeselectedItems(gv, arrItemIds) {
	arrItemIds.map(function(itemId){gv2_getElementById(gv, itemId).unselect()})
}

function gv2_SelectedItems(gv) {
	return gv.elements(':selected');
}

function gv2_SelectedNodes(gv) {
	return gv.elements('node:selected');
}

function gv2_SelectedEdges(gv) {
	return gv.elements('edge:selected');
}




//-----------------------------------
//Synchronize gv graph 
//-----------------------------------
function gv2_Sychronize(gv, newElements, removedElements, updatedElements) {
}

//-----------------------------------
//Add new nodes
//TODO: fix X and Y position
//-----------------------------------
function gv2_addElements(gv, newElements) {
	gv.addElements(0, 0, newElements)	
}


//----------------------------------------------------------
//Delete gv elements where id exists in the removedElements 
//----------------------------------------------------------
function gv2_removeElements(gv, removedElements) {
	gv.removeElements("nodes", removedElements["nodes"])	
	gv.removeElements("edges", removedElements["edges"])	
}

//-------------------------------------------
// Update gv elements from the updatedElements
//-------------------------------------------
function gv2_updateElements(gv, updatedElements) {
	for(var updatedElement in  updatedElements){
		gv2_updateElement(gv, updatedElement);
	}
}

//-------------------------------------------
//Update a gv element using the updatedElement
//-------------------------------------------
function gv2_updateElement(gv, updatedElement) {
	var gvElement = gv2_getElementById(updatedElement.id);
	if(gvElement){
		for(var propName in element.data){
			gvElement.data[propname] = updatedElement.data[propName];					
		}
	}
}


function gv2_getElementById(gv, id){
	var elements;
	var element;
	
	elements = gv.elements('#' + id);
	if(elements.length > 0){
		element = elements[0];
		return element;
	}	
	return undefined;
	
}

function gv2_getNodeById(gv, nodeId){
	var elements;
	var element;
	
	elements = gv.nodes('#' + nodeId);
	if(elements.length > 0){
		element = elements[0];
		return element;
	}	
	return undefined;
}

function gv2_getEdgeById(gv, edgeId){
	var elements;
	var element;
	
	elements = gv.edges('#' + edgeId);
	if(elements.length > 0){
		element = elements[0];
		return element;
	}	
	return undefined;
}


//------------------
//Click listeners
//------------------
function gv2_AddClickItemListener(gv, listener){
	gv2_AddOnNodeClickListener(gv, listener);
	gv2_AddOnEdgeClickListener(gv, listener);
}

function gv2_AddOnNodeClickListener(gv, listener){
	gv.on('click', 'node', undefined, listener);
}

function gv2_AddOnEdgeClickListener(gv, listener){
	gv.on('click', 'edge', undefined, listener);
}

//------------------
//Dblclick listeners
//------------------
function gv2_AddDblclickItemListener(gv, listener){
	gv2_AddOnNodeDblclickListener(gv, listener);
	gv2_AddOnEdgeDblclickListener(gv, listener);
}

function gv2_AddOnNodeDblclickListener(gv, listener){
	gv.addListener("dblclick", "nodes", listener);
}

function gv2_AddOnEdgeDblclickListener(gv, listener){
	gv.addListener("dblclick", "edges", listener);
}

//------------------
//Drag listeners
//------------------
function gv2_AddDragStartListener(gv, listener){
	gv.on("drag", "node", undefined, listener);

}
function gv2_AddDragStopListener(gv, listener){
	gv.on("free", "node", undefined, listener);
}


//------------------
//Selection listeners
//------------------
function gv2_AddOnSelectItemListener(gv, listener){
	gv2_AddOnSelectNodeListener(gv, listener);
	gv2_AddOnSelectEdgeListener(gv, listener);
}

function gv2_AddOnSelectNodeListener(gv, listener){
	gv.on('select', 'node', undefined, listener);
}

function gv2_AddOnSelectEdgeListener(gv, listener){
	gv.on('select', 'edge', undefined, listener);
}

//----------------------
//Unselection listeners
//----------------------
function gv2_AddOnDeselectItemListener(gv, listener){
	gv2_AddOnDeselectNodeListener(gv, listener);
	gv2_AddOnDeselectEdgeListener(gv, listener);
}

function gv2_AddOnDeselectNodeListener(gv, listener){
	gv.on("unselect", "node", undefined, listener);
}

function gv2_AddOnDeselectEdgeListener(gv, listener){
	gv.on("unselect", "edge", undefined, listener);
}



// Return a coma separated string containing items id.
function gv2_ArrItemToIds(arrItems){
	var csIds = "";
	for(i=0; i<arrItems.length; i++){
		csIds += arrItems[i].data.id + ","
	}
	return csIds.substring(0, csIds.length - 1);
}


//------------------------------------------------
//Shape converter
//convert shape generic name to cytoscape shape
//------------------------------------------------

var CYTOSCAPE2_NODE_SHAPES = {
		ELLIPSE: 'ellipse',
		RECTANGLE: 'rectangle',
		TRIANGLE: 'triangle',
		ROUNDRECTANGLE:	'roundrectangle',
		DIAMOND: 'triangle',
		HEXAGON: 'rectangle',
		OCTAGON: 'rectangle',
		PARALLELOGRAM: 'rectangle',		
}

var CYTOSCAPE2_EDGE_SHAPES = {
		NONE: 			'none',
		TEE:			'tee',
		TRIANGLE:		'triangle',
		SQUARE:			'square',
		CIRCLE: 		'circle',
		DELTA: 			'triangle',
		ARROW: 			'triangle',
		DIAMOND: 		'triangle',
		OCTAGON:		'square',
		PARALLELOGRAM:	'square',
}


function gv2_convertNodeShape(genericShapeName){
	var shapeName = CYTOSCAPE2_NODE_SHAPES[genericShapeName];
	if(shapeName == null){
		shapeName = "ellipse";
	}
	return shapeName;
}



function gv2_convertEdgeShape(genericShapeName){
	var shapeName = CYTOSCAPE2_EDGE_SHAPES[genericShapeName];
	if(shapeName == null){
		shapeName = "triangle";
	}
	return shapeName;
}


