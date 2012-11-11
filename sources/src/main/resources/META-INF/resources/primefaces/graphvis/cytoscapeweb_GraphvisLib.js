

/**
 * Utility function for CystoscapeWeb graph manipulation
 * 
 * http://www.sysord.com  
 */

//-------------------------------------------------
// Draw Graph:
//	div_od: id element graph location
//	jsonGraphModel: graph model provided as json
//-------------------------------------------------
function gv_drawgraph(div_id, jsonGraphModel) {

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

function gv_refresh(gv) {
	gv.addElements([],true);	
}


function gv_addNode(gv, nodeId, label, x, y, size, shape, color, datas) {
	if(!nodeId){
		nodeId = "N" + gv.nodes().length;
	}
	var nodeX = 0; if(x) nodeX = x;
	var nodeY = 0; if(y) nodeY = y;
	var nodeDatas = gv_prepareNodeElement(nodeId, label, size, shape, color, datas);
	return gv.addNode(nodeX, nodeY, nodeDatas);	
}

function gv_removeNode(gv, nodeId) {
	return gv.removeNode(nodeId);	
}

function gv_addEdge(gv, edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas) {
	if(!edgeId){
		edgeId = sourceNodeId + "_" + targetNodeId;
	}
	var edgeDatas = gv_prepareEdgeElement(edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas);
	return gv.addEdge(edgeDatas);
}

function gv_removeEdge(gv, edgeId) {
	return gv.removeEdge(edgeId);
}

function gv_removeElements(gv, gvElements) {
	return gv.removeElements(gvElements);
}



//private utility function
function gv_prepareNodeElement(nodeId, label, size, shape, color, datas) {
	var elementDatas;
	if(datas){
		elementDatas = datas;
	}else{
		elementDatas = {};		
	}
	
	if(size) elementDatas.size = size;
	if(shape) elementDatas.shape = gv_convertNodeShape(shape);
	if(color) elementDatas.color = color;	
	elementDatas.id = nodeId;
	elementDatas.label = label;
	return elementDatas;
}

function gv_prepareEdgeElement(edgeId, label, sourceNodeId, targetNodeId, directed, width, color, datas) {
	var edgeDatas;
	if(datas){
		edgeDatas = datas;
	}else{
		edgeDatas = {};		
	}
	edgeDatas.id = edgeId;
	edgeDatas.label = label;
	edgeDatas.source = sourceNodeId;
	edgeDatas.target = targetNodeId;
	edgeDatas.directed = (directed == true);
	if(width) edgeDatas.width = width;
	if(color) edgeDatas.color = color;
	
	return edgeDatas;
}


//-----------------------------------
// select gv graph items where their ids
// are present in the array arrItemIds
//-----------------------------------
function gv_SelectItems(gv, arrItemIds) {
	gv.select(null, arrItemIds);
}

function gv_SelectedItems(gv) {
	return gv.selected();
}

function gv_SelectedNodes(gv) {
	return gv.selected("nodes");
}

function gv_SelectedEdges(gv) {
	return gv.selected("edges");
}


function gv_DeselectedItems(gv, arrItemIds) {
	gv.deselect(arrItemIds);
}


//-----------------------------------
//Synchronize gv graph 
//-----------------------------------
function gv_Sychronize(gv, newElements, removedElements, updatedElements) {
}

//-----------------------------------
//Add new nodes
//TODO: fix X and Y position
//-----------------------------------
function gv_addElements(gv, newElements) {
	gv.addElements(0, 0, newElements)	
}


//----------------------------------------------------------
//Delete gv elements where id exists in the removedElements 
//----------------------------------------------------------
function gv_removeElements(gv, removedElements) {
	gv.removeElements("nodes", removedElements["nodes"])	
	gv.removeElements("edges", removedElements["edges"])	
}

//-------------------------------------------
// Update gv elements from the updatedElements
//-------------------------------------------
function gv_updateElements(gv, updatedElements) {
	for(var updatedElement in  updatedElements){
		gv_updateElement(gv, updatedElement);
	}
}

//-------------------------------------------
//Update a gv element using the updatedElement
//-------------------------------------------
function gv_updateElement(gv, updatedElement) {
	var gvElement = gv_getElementById(updatedElement.id);
	if(gvElement){
		for(var propName in element.data){
			gvElement.data[propname] = updatedElement.data[propName];					
		}
	}
}


function gv_getElementById(gv, id){
	var element;
	
	element = gv.node(id);
	if(element) return element;

	element = gv.edge(id);
	if(element) return element;
	
	return element;
}


//------------------
//Click listeners
//------------------
function gv_AddClickItemListener(gv, listener){
	gv_AddOnNodeClickListener(gv, listener);
	gv_AddOnEdgeClickListener(gv, listener);
}

function gv_AddOnNodeClickListener(gv, listener){
	gv.addListener("click", "nodes", listener);
}

function gv_AddOnEdgeClickListener(gv, listener){
	gv.addListener("click", "edges", listener);
}

//------------------
//Dblclick listeners
//------------------
function gv_AddDblclickItemListener(gv, listener){
	gv_AddOnNodeDblclickListener(gv, listener);
	gv_AddOnEdgeDblclickListener(gv, listener);
}

function gv_AddOnNodeDblclickListener(gv, listener){
	gv.addListener("dblclick", "nodes", listener);
}

function gv_AddOnEdgeDblclickListener(gv, listener){
	gv.addListener("dblclick", "edges", listener);
}

//------------------
//Drag listeners
//------------------
function gv_AddDragStartListener(gv, listener){
	gv.addListener("dragstart", "nodes", listener);
}
function gv_AddDragStopListener(gv, listener){
	gv.addListener("dragstop", "nodes", listener);
}


//------------------
//Selection listeners
//------------------
function gv_AddOnSelectItemListener(gv, listener){
	gv.addListener("select", null, listener);
}

function gv_AddOnSelectNodeListener(gv, listener){
	gv.addListener("select", "nodes", listener);
}

function gv_AddOnSelectEdgeListener(gv, listener){
	gv.addListener("select", "edges", listener);
}

//----------------------
//Unselection listeners
//----------------------
function gv_AddOnDeselectItemListener(gv, listener){
	gv.addListener("deselect", null, listener);
}

function gv_AddOnDeselectNodeListener(gv, listener){
	gv.addListener("deselect", "nodes", listener);
}

function gv_AddOnDeselectEdgeListener(gv, listener){
	gv.addListener("deselect", "edges", listener);
}


// Return a coma separated string containing items id.
function gv_ArrItemToIds(arrItems){
	var csIds = "";
	for(i=0; i<arrItems.length; i++){
		csIds += arrItems[i].data.id + ","
	}
	return csIds.substring(0, csIds.length - 1);
}

//------------------------------------------------
// Shape converter
// convert shape generic name to cytoscape shape
//------------------------------------------------

var CYTOSCAPE_NODE_SHAPES = {
		ELLIPSE: 'ELLIPSE',
		RECTANGLE: 'RECTANGLE',
		TRIANGLE: 'TRIANGLE',
		DIAMOND: 'DIAMOND',
		HEXAGON: 'HEXAGON',
		OCTAGON: 'OCTAGON',
		PARALLELOGRAM: 'PARALLELOGRAM',		
		ROUNDRECTANGLE:	'RECTANGLE'
}

var CYTOSCAPE_EDGE_SHAPES = {
		NONE: 			'NONE',
		DELTA: 			'DELTA',
		ARROW: 			'ARROW',
		DIAMOND: 		'DIAMOND',
		CIRCLE: 		'CIRCLE',
		OCTAGON:		'OCTAGON',
		PARALLELOGRAM:	'PARALLELOGRAM',
		TEE:			'DELTA',
		TRIANGLE:		'DELTA',
		SQUARE:			'PARALLELOGRAM'
}


function gv_convertNodeShape(genericShapeName){
	var shapeName = CYTOSCAPE_NODE_SHAPES[genericShapeName];
	if(shapeName == null){
		shapeName = "ELLIPSE";
	}
	return shapeName;
}



function gv_convertEdgeShape(genericShapeName){
	var shapeName = CYTOSCAPE_EDGE_SHAPES[genericShapeName];
	if(shapeName == null){
		shapeName = "DELTA";
	}
	return shapeName;
}


