/*
 * Copyright 2009-2011 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Sysord 2012
 * 
 */
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
 * Graphvis renderer for CytoscapeWeb (flash version)
 * 
 * http://www.sysord.com
 */
public class GraphvisRenderer extends AbstractGraphvisRenderer {

	
    /**
     * Emit the div for the CytoscapeWeb graph
     * 
     * @param context
     * @param graphvisComp
     * @throws IOException
     */
	protected void encodeMarkup(FacesContext context, Graphvis graphvisComp) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);
		writer.writeAttribute("id", graphvisComp.getClientId(context), null);
        if(graphvisComp.getStyle() != null){
            writer.writeAttribute("style", graphvisComp.getStyle(), "style");        	
        }
		if(graphvisComp.getStyleClass() != null)
			writer.writeAttribute("class", graphvisComp.getStyleClass(), "styleClass");

    	writer.write("Cytoscape Web will replace the contents of this div with your graph.");		
	    		
		writer.endElement("div");
	}
	
	
	
	@Override
	protected void encodeAdditionalParameters(FacesContext context, Graphvis graphvisComp, ResponseWriter writer) throws IOException {

        writer.write(", graphRenderer:'CSW'");

		//swf resources specific to cytoscape
        writer.write(", cytoscapeWebPath:'" + getResourceRequestPath(context, "graphvis/CytoscapeWeb.swf") + "'");
        writer.write(", flashInstallerPath:'" + getResourceRequestPath(context, "graphvis/playerProductInstall.swf") + "'");
		
	}
	
}