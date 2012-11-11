package org.primefaces.component.graphvis;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.context.RequestContext;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.model.graphvis.GraphvisEdge;
import org.primefaces.model.graphvis.GraphvisEdge.ARROW_SHAPE;
import org.primefaces.model.graphvis.GraphvisModel;
import org.primefaces.model.graphvis.GraphvisModel.GVMODEL_ACTION_TYPE;
import org.primefaces.model.graphvis.GraphvisModel.GraphvisModelAction;
import org.primefaces.model.graphvis.GraphvisModelElement;
import org.primefaces.model.graphvis.GraphvisModelElement.GRAPHVIS_ELEMENT_TYPE;
import org.primefaces.model.graphvis.GraphvisNode;
import org.primefaces.model.graphvis.GraphvisNode.NODE_SHAPE;
import org.primefaces.renderkit.CoreRenderer;

/**
 * Graphvis renderer 
 * 
 * http://www.sysord.com
 */
public abstract class AbstractGraphvisRenderer extends CoreRenderer {

	
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    	if(isAjaxRequest(context)){
    		return;
    	}

    	Graphvis graphvisComp = (Graphvis) component;
        encodeMarkup(context, graphvisComp);
        encodeScript(context, graphvisComp);
	}


    /**
     * Emit graph container
     * 
     * @param context
     * @param graphvisComp
     * @throws IOException
     */
    protected abstract void encodeMarkup(FacesContext context, Graphvis graphvisComp) throws IOException;
   


    /**
     * encode graphvis script creation
     * 
     * @param context
     * @param graphvisComp
     * @throws IOException
     */
	protected void encodeScript(FacesContext context, Graphvis graphvisComp) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = graphvisComp.getClientId(context);

        startScript(writer, clientId);
        
        //emit json graph model as stored in a variable named ID__graphvisModel .
        String graphModelVarname = getGraphModelVarname(graphvisComp);
        emitGrpahvisModel(graphvisComp, writer);
        writer.write(";\n");

        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('Graphvis','" + graphvisComp.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        
        writer.write(", graphRenderer:'CSW2'");

        writer.write(", ajax:" + graphvisComp.isAjax());

        writer.write(", graphModel:" + graphModelVarname );

        //writer.write(", graphStyle:" + graphModelVarname + "_visualStyle");

        writer.write(", update:'" + graphvisComp.getUpdate() + "'");
        
        
        encodeAdditionalParameters(context, graphvisComp, writer);
        
        //-------------------
        //js client side 
        //-------------------
        encodeClientSideScripts(graphvisComp, writer);
        
        //Ajax Behaviors
        encodeClientBehaviors(context, graphvisComp);

        writer.write("});});");

		endScript(writer);
	}

    /**
     * Hook for extends the graphvis cfg structure with custom parameters
     * 
     * @param context
     * @param graphvisComp
     * @throws IOException
     */
    protected void encodeAdditionalParameters(FacesContext context, Graphvis graphvisComp, ResponseWriter writer) throws IOException{};

    
//    protected abstract void encodeScript(FacesContext context, Graphvis graphvisComp) throws IOException;
//	protected void encodeMarkup(FacesContext context, Graphvis graphvisComp) throws IOException {
//		ResponseWriter writer = context.getResponseWriter();
//
//		writer.startElement("div", null);
//		writer.writeAttribute("id", graphvisComp.getClientId(context), null);
//        if(graphvisComp.getStyle() != null){
//            writer.writeAttribute("style", graphvisComp.getStyle(), "style");        	
//        }
//		if(graphvisComp.getStyleClass() != null)
//			writer.writeAttribute("class", graphvisComp.getStyleClass(), "styleClass");
//
//
//    	writer.write("Cytoscape Web will replace the contents of this div with your graph.");		
//	    
//		
//		writer.endElement("div");
//	}
//	
//	protected void encodeScript(FacesContext context, Graphvis graphvisComp) throws IOException{
//		ResponseWriter writer = context.getResponseWriter();
//		String clientId = graphvisComp.getClientId(context);
//
//        startScript(writer, clientId);
//        
//        //emit json graph model as stored in a variable named ID__graphvisModel .
//        String graphModelVarname = getGraphModelVarname(graphvisComp);
//        emitGrpahvisModel(graphvisComp, writer);
//        writer.write(";\n");
//
//        writer.write("$(function() {");
//        
//        writer.write("PrimeFaces.cw('Graphvis','" + graphvisComp.resolveWidgetVar() + "',{");
//        writer.write("id:'" + clientId + "'");
//        writer.write(", ajax:" + graphvisComp.isAjax());
//
//        writer.write(", graphModel:" + graphModelVarname );
//
//        //writer.write(", graphStyle:" + graphModelVarname + "_visualStyle");
//
//        writer.write(", update:'" + graphvisComp.getUpdate() + "'");
//
//        //swf resources specific to cytoscape
//        writer.write(", cytoscapeWebPath:'" + getResourceRequestPath(context, "graphvis/CytoscapeWeb.swf") + "'");
//        writer.write(", flashInstallerPath:'" + getResourceRequestPath(context, "graphvis/playerProductInstall.swf") + "'");
//        
//        
//        //-------------------
//        //js client side 
//        //-------------------
//        encodeClientSideScripts(graphvisComp, writer);
//        
//        //Ajax Behaviors
//        encodeClientBehaviors(context, graphvisComp);
//
//        writer.write("});});");
//
//		endScript(writer);
//	}

	
	/**
	 * Encode Client side script
	 * @param graphvisComp
	 * @param writer
	 * @throws IOException 
	 */
	protected void encodeClientSideScripts(Graphvis graphvisComp, ResponseWriter writer) throws IOException{
		//click
		encodeClientSideEventScript("onItemClick", graphvisComp.getOnItemClick(), false, writer);
		encodeClientSideEventScript("onNodeClick", graphvisComp.getOnNodeClick(), false, writer);
		encodeClientSideEventScript("onEdgeClick", graphvisComp.getOnEdgeClick(), false, writer);

		//dblclick
		encodeClientSideEventScript("onItemDblclick", graphvisComp.getOnItemDblclick(), false, writer);
		encodeClientSideEventScript("onNodeDblclick", graphvisComp.getOnNodeDblclick(), false, writer);
		encodeClientSideEventScript("onEdgeDblclick", graphvisComp.getOnEdgeDblclick(), false, writer);

		//Select
		encodeClientSideEventScript("onItemSelection", graphvisComp.getOnItemSelection(), true, writer);
		encodeClientSideEventScript("onNodeSelection", graphvisComp.getOnNodeSelection(), true, writer);
		encodeClientSideEventScript("onEdgeSelection", graphvisComp.getOnEdgeSelection(), true, writer);

		//Unselect
		encodeClientSideEventScript("onItemUnselection", graphvisComp.getOnItemUnselection(), true, writer);
		encodeClientSideEventScript("onNodeUnselection", graphvisComp.getOnNodeUnselection(), true, writer);
		encodeClientSideEventScript("onEdgeUnselection", graphvisComp.getOnEdgeUnselection(), true, writer);

	}

	/**
	 * Create clientSideEventScript parameter
	 * @param eventName
	 * @param eventScript
	 * @param writer
	 * @throws IOException
	 */
	protected void encodeClientSideEventScript(String eventName, String eventScript, boolean isMultiItems, ResponseWriter writer) throws IOException{
		if(eventScript != null) {
			String listenerParmName = isMultiItems ? "gvitems": "gvitem";	 
			String listener = createJsAdaptedItemListenerSource(listenerParmName, eventScript, "", null);
	        writer.write(", " + eventName + ":" + listener + "");
	    }
	}

	
	/**
	 * Create source for an event listener declaration
	 * parameter is an adapted Item (nodeAdapter, edgeAdapter)
	 * 
	 * @param script
	 * @param preScript
	 * @param postScript
	 * @return
	 */
	public static String createJsAdaptedItemListenerSource(String parmName, String script, String preScript, String postScript){
		String source ="";
		source += "function(" + parmName + ") {\n";
		source += (preScript != null) ? preScript + ";\n" : "";	
		source += script + ";\n";		
		source += (postScript != null) ? postScript + ";\n" : "";	
		source += "}\n";
		return source;
	}

	

	protected String getGraphModelVarname(Graphvis graphvisComp){
		return graphvisComp.getId() + "_graphvisModel";
	}
	
	
		
	/**
	 * Generate and emit JSON graphmodel
	 * @param graphvisComp
	 * @param writer
	 * @throws IOException
	 */
	protected void emitGrpahvisModel(Graphvis graphvisComp, ResponseWriter writer) throws IOException{
		GraphvisModel model = graphvisComp.getValue();
		
		//TODO: externalize emit using a Visitor
				
		writer.write("var " + getGraphModelVarname(graphvisComp) + " = {");
		
		//TODO: extract dataschema from model 
//		writer.write("dataSchema: { nodes: [ " +
//									" { name: \"label\", type: \"string\" }," +
//									" { name: \"shape\", type: \"string\" }," +
//									" { name: \"color\", type: \"string\" }," +
//									" { name: \"size\", type: \"number\", defValue: 10 }" +
//								"], " 
//								+ " edges: [ { name: \"label\", type: \"string\" }] "
//								+ "},"								
//				);
		
		writer.write("properties: {" +
				(model.getLayout() != null ? "layout: \"" + model.getLayout() + "\"" : "") +						
				"},");

		writer.write("nodes: [");
		//{id:nodeId, label:node Label, x:, y:, size: node size, shape: node Shape, color: node color (rgb), datas:{...}},
		for(GraphvisNode node : model.getNodes()){
			writer.write("{ id: \"" + node.getId() + "\"" + ", label: \"" +  node.getLabel() + "\", " +
							"x: " +  node.getX()  +  ", y: " + node.getY() + ", size: " + node.getSize() + ", " +
							"shape: \"" +  node.getShape().toString()  +  "\", color: \"" +  node.getColor()  +  "\", " +
							"datas: {}"	+	
						" },");			
		}
		writer.write("],");

		writer.write("edges: [");
		//{id:edgeId, label:edge Label, sourceNodeId:, targetNodeId:, directed:(true|false),  width: edge width, color: edge color (rgb), datas:{...}},
		for(GraphvisEdge edge : model.getEdges()){
			writer.write("{ id: \"" + edge.getId() + "\", label: \"" + edge.getLabel() + "\", " +
							"targetNodeId: \"" + edge.getTargetNode().getId() + "\", sourceNodeId: \"" + edge.getSourceNode().getId()  + "\", " +
							"directed:" + edge.getDirected() +", width: " + edge.getWidth() + ", color: \"" + edge.getColor() + "\", " +							
							" datas: {" +  "shape: \"" + edge.getShape().toString() + "\"" + "}" +							
						  "},");			
		}
		writer.write("],");

		writer.write("};\n");
						
		model.clearModelSynchronizationStructure();
		
	}

	
    @Override
	public void decode(FacesContext context, UIComponent component) {
        super.decodeBehaviors(context, component);


        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        System.out.println("REQUEST PARAMS:" + params);
        Graphvis graphvisComp = (Graphvis) component;
        
        //receive synchronization structure
        if(params.containsKey(graphvisComp.getClientId(context) + "_synchronizeModel")){
        	processSynchronizationAjaxRequest(context, graphvisComp, params);
        	return;
        }
	}

    /**
     * GraphModel synchronization
     * @param context
     * @param graphvisComp
     */
    protected void processSynchronizationAjaxRequest(FacesContext context, Graphvis graphvisComp, Map<String,String> params ){
    	try {
        	//Create response message for client model synchronization
    		boolean fullSync = (params.containsKey("synchronizeAll") && params.get("synchronizeAll").equals("true"));
			String clientSyncStruct = prepareSynchronizeClientResponse(graphvisComp, fullSync);
			//server model synchronization
	    	synchronizeServerSideModel(context, graphvisComp, params);
	    	
	    	//clear model syncStructure
	    	graphvisComp.getValue().getModelSynchronizationStructure().clear();

	    	RequestContext requestContext = RequestContext.getCurrentInstance();
            if(requestContext != null) {
                requestContext.addCallbackParam(graphvisComp.getClientId(context) + "_synchronizeModel", clientSyncStruct);
            }
	    	
		} catch (JSONException e) {
			// TODO : get an onError callback on client side
			e.printStackTrace();
		}finally{
			//context.renderResponse();
		}
    	
    }

    
    /**
     * GraphModel synchronization
     * 
     * @param context
     * @param graphvisComp
     */
    protected void synchronizeServerSideModel(FacesContext context, Graphvis graphvisComp, Map<String,String> params ){
    	String jsonSyncStructure = params.get(graphvisComp.getClientId(context) + "_synchronizeModel");
        System.out.println("REQUEST PARAMS:" + params);
        try {
        	JSONArray actions = new JSONArray(jsonSyncStructure);
        	
        	for(int iAction = 0; iAction < actions.length(); iAction++){
    			JSONObject action= actions.getJSONObject(iAction);
    			String actionType = "";
        		actionType = action.getString("actionType");
    			if(GVMODEL_ACTION_TYPE.REMOVE_NODE.toString().equals(actionType)){        			
        			graphvisComp.getValue().removeNode(action.getString("targetId"));

    			}else if(GVMODEL_ACTION_TYPE.REMOVE_EDGE.toString().equals(actionType)){        			
            		graphvisComp.getValue().removeEdge(action.getString("targetId"));

    			}else if(GVMODEL_ACTION_TYPE.ADD_NODE.toString().equals(actionType)){
    				JSONObject jsonNode = action.getJSONObject("target");    				
    				JSONObject data = jsonNode.getJSONObject("datas");
    				//create node
    				graphvisComp.getValue().addNode(jsonNode.getString("id"), jsonNode.getString("label"));
    				//update node
    				synchronizeNodeFromJson(jsonNode, graphvisComp);

    			}else if(GVMODEL_ACTION_TYPE.UPDATE_NODE.toString().equals(actionType)){
    				//update node
    				synchronizeNodeFromJson(action.getJSONObject("target"), graphvisComp);

    			}else if(GVMODEL_ACTION_TYPE.ADD_EDGE.toString().equals(actionType)){        			
    				JSONObject jsonEdge = action.getJSONObject("target");
    				JSONObject data = jsonEdge.getJSONObject("datas");
    				graphvisComp.getValue().addEdge(jsonEdge.getString("id"), jsonEdge.getString("label"), jsonEdge.getString("sourceNodeId"), jsonEdge.getString("targetNodeId"));
    				//update edge
    				synchronizeEdgeFromJson(action.getJSONObject("target"), graphvisComp);

    			}else if(GVMODEL_ACTION_TYPE.UPDATE_EDGE.toString().equals(actionType)){
    				//update EDGE
    				synchronizeEdgeFromJson(action.getJSONObject("target"), graphvisComp);
    			}
        	}

        } catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * Update GraphvisNode from a json node
     * 
     * @param jsonNode
     * @param graphvisComp
     * @throws JSONException
     */
    protected void synchronizeNodeFromJson(JSONObject jsonNode, Graphvis graphvisComp) throws JSONException{
		GraphvisNode updatedNode = graphvisComp.getValue().getNode(jsonNode.getString("id"));
		if(updatedNode != null){
			//visual synchronization
			updatedNode.setLabel(jsonNode.getString("label"));    					
			updatedNode.setX(Float.parseFloat(jsonNode.getString("x")));
			updatedNode.setY(Float.parseFloat(jsonNode.getString("y")));
			updatedNode.setShape(NODE_SHAPE.valueOf(jsonNode.getString("shape")));
			updatedNode.setSize(jsonNode.getInt("size"));
			updatedNode.setColor(jsonNode.getString("color"));
			//data synchronization
			//TODO
		}

    }

    /**
     * Update GraphvisEdge from a json edge
     * 
     * @param jsonEdge
     * @param graphvisComp
     * @throws JSONException
     */
    protected void synchronizeEdgeFromJson(JSONObject jsonEdge, Graphvis graphvisComp) throws JSONException{
		GraphvisEdge updatedEdge = graphvisComp.getValue().getEdge(jsonEdge.getString("id"));
		if(updatedEdge != null){
			//visual synchronization
			updatedEdge.setLabel(jsonEdge.getString("label"));    					
			updatedEdge.setSourceNode(graphvisComp.getValue().getNode(jsonEdge.getString("sourceNodeId")));
			updatedEdge.setTargetNode(graphvisComp.getValue().getNode(jsonEdge.getString("targetNodeId")));
			updatedEdge.setDirected(jsonEdge.getBoolean("directed"));
			updatedEdge.setWidth(jsonEdge.getInt("width"));
			updatedEdge.setColor(jsonEdge.getString("color"));
			updatedEdge.setShape(ARROW_SHAPE.valueOf(jsonEdge.getString("shape")));
						
			//data synchronization
			//TODO data synchronization
		}

    }

    
    /**
     * Create a Json structure for client side synchronization 
     * @param graphvisComp
     * @return
     * @throws JSONException
     */
    protected String prepareSynchronizeClientResponse(Graphvis graphvisComp, boolean isFullSync) throws JSONException{
    	
    	//all items synchronization
    	if(isFullSync){
    		return prepareFullSynchronizeClientResponse(graphvisComp);
    	}
    	
    	//standard synchronization
    	JSONArray jsonSyncStruct = new JSONArray();
    	for(GraphvisModelAction action : graphvisComp.getValue().getModelSynchronizationStructure()){
   			String actionType = action.getActionType().toString();

   			JSONObject jsonSyncAction = new JSONObject();
			jsonSyncAction.put("actionType", actionType);
   			
			if(GVMODEL_ACTION_TYPE.REMOVE_NODE.toString().equals(actionType)){        			
				jsonSyncAction.put("targetId", action.getTargetElementId());

				
			}else if(GVMODEL_ACTION_TYPE.REMOVE_EDGE.toString().equals(actionType)){        			
				jsonSyncAction.put("targetId", action.getTargetElementId());

				
			}else if(GVMODEL_ACTION_TYPE.ADD_NODE.toString().equals(actionType)
					 || GVMODEL_ACTION_TYPE.UPDATE_NODE.toString().equals(actionType)){
				GraphvisModelElement addedElement = graphvisComp.getValue().getGraphvisModelElement(action.getTargetElementId());
				if(addedElement != null && addedElement.getElementType() == GRAPHVIS_ELEMENT_TYPE.NODE){
					GraphvisNode addedNode = (GraphvisNode) addedElement;
					JSONObject jsonTargetNode = nodeToJSON(addedNode);
		   			jsonSyncAction.put("target", jsonTargetNode);					
				}
								
			}else if(GVMODEL_ACTION_TYPE.ADD_EDGE.toString().equals(actionType)
				|| GVMODEL_ACTION_TYPE.UPDATE_EDGE.toString().equals(actionType) ){        			
				GraphvisModelElement addedElement = graphvisComp.getValue().getGraphvisModelElement(action.getTargetElementId());
				if(addedElement != null && addedElement.getElementType() == GRAPHVIS_ELEMENT_TYPE.EDGE){
					GraphvisEdge addedEdge = (GraphvisEdge) addedElement;
					JSONObject jsonTargetEdge = edgeToJSON(addedEdge);
					jsonSyncAction.put("target", jsonTargetEdge);					
				}

			}else if(GVMODEL_ACTION_TYPE.DO_LAYOUT.toString().equals(actionType)){        			
				jsonSyncAction.put("targetId", action.getTargetElementId());
			}
			
			jsonSyncStruct.put(jsonSyncAction);
    	}
    	
    	return jsonSyncStruct.toString();
    }
    
    
    protected String prepareFullSynchronizeClientResponse(Graphvis graphvisComp) throws JSONException{
    	    	
    	JSONArray jsonSyncStruct = new JSONArray();
    	
    	//add all nodes
    	for(GraphvisNode addedNode : graphvisComp.getValue().getNodes()){
   			JSONObject jsonSyncAction = new JSONObject();
			jsonSyncAction.put("actionType", GVMODEL_ACTION_TYPE.ADD_NODE.toString());   							
			JSONObject jsonTargetNode = nodeToJSON(addedNode);			
   			jsonSyncAction.put("target", jsonTargetNode);																
			jsonSyncStruct.put(jsonSyncAction);
    	}

    	//add all edges
    	for(GraphvisEdge addedEdge : graphvisComp.getValue().getEdges()){
   			JSONObject jsonSyncAction = new JSONObject();
			jsonSyncAction.put("actionType", GVMODEL_ACTION_TYPE.ADD_EDGE.toString());
			JSONObject jsonTargetEdge = edgeToJSON(addedEdge);
   			jsonSyncAction.put("target", jsonTargetEdge);																
			jsonSyncStruct.put(jsonSyncAction);
    	}
    	
    	System.out.println("prepareFullSynchronizeClientResponse:" + jsonSyncStruct.toString());
    	return jsonSyncStruct.toString();
    }
    

    private JSONObject nodeToJSON(GraphvisNode node) throws JSONException{
   		JSONObject jsonNode = new JSONObject();	   			
   		jsonNode.put("id", node.getId());
   		jsonNode.put("label", node.getLabel());	   							
   		jsonNode.put("x", node.getX());
   		jsonNode.put("y", node.getY());
   		jsonNode.put("color", node.getColor());
   		jsonNode.put("shape", node.getShape().toString());	   			
   		jsonNode.put("size", node.getSize());
		return jsonNode;
    }
		
    private JSONObject edgeToJSON(GraphvisEdge edge) throws JSONException{
		JSONObject jsonEdge = new JSONObject();
		jsonEdge.put("id", edge.getId());
		jsonEdge.put("label", edge.getLabel());
		jsonEdge.put("sourceNodeId", edge.getSourceNode().getId());
		jsonEdge.put("targetNodeId", edge.getTargetNode().getId());
		jsonEdge.put("directed", edge.getDirected());
		jsonEdge.put("width", edge.getWidth());
		jsonEdge.put("color", edge.getColor());
		jsonEdge.put("shape", edge.getShape().toString());
		return jsonEdge;
    }
	
}