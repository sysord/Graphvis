
package org.primefaces.component.graphvis;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * Graphvis renderer for CytoscapeWeb 2 (html5 version)
 *
 * http://www.sysord.com
 */
public class Graphvis2Renderer extends AbstractGraphvisRenderer {

	
    /**
     * Emit the div for the graph
     * 
     * @param context
     * @param graphvisComp
     * @throws IOException
     */
	protected void encodeMarkup(FacesContext context, Graphvis graphvisComp) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);
		writer.writeAttribute("id", graphvisComp.getClientId(context), null);
    	String defaultStyle = "border: 1px solid #ddd;margin: Opx 0; ";
		String style = defaultStyle + "height:100%;width:90%; ";
        if(graphvisComp.getStyle() != null){
        	style += graphvisComp.getStyle();
        }
        writer.writeAttribute("style", style, "style");        	

        if(graphvisComp.getStyleClass() != null){
			writer.writeAttribute("class", graphvisComp.getStyleClass(), "styleClass");        	
        }
		
		writer.endElement("div");
	}
	

	@Override
	protected void encodeAdditionalParameters(FacesContext context, Graphvis graphvisComp, ResponseWriter writer) throws IOException {
        writer.write(", graphRenderer:'CSW2'");		
	}
	
	
}