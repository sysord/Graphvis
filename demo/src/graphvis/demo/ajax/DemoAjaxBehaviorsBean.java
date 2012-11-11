package graphvis.demo.ajax;


import graphvis.demo.common.GraphModelGenerator;

import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.graphvis.GraphvisEdge;
import org.primefaces.model.graphvis.GraphvisModel;
import org.primefaces.model.graphvis.GraphvisModelElement;
import org.primefaces.model.graphvis.GraphvisNode;
import org.primefaces.model.graphvis.impl.GraphvisModelImpl;

@ManagedBean
@SessionScoped
public class DemoAjaxBehaviorsBean {

	protected GraphvisModel graphModel;
	
	
	public GraphvisModel getGraphModel() {
		if(graphModel == null){
			graphModel = new GraphvisModelImpl();
			fillGraphModel();
		}
		return graphModel;
	}

	public void setGraphModel(GraphvisModel graphModel) {
		this.graphModel = graphModel;
	}
	

	public void fillGraphModel() {
		GraphModelGenerator.fillGraphModel(graphModel, 5, 10, 20);		
		graphModel.setLayout("ForceDirected");
	}
	
	
	public void onSelectItems(SelectEvent event){
		System.out.println("SELECTION:" + event.getObject());
		FacesContext context = FacesContext.getCurrentInstance();		
		context.addMessage(null, new FacesMessage("Selection:" +  dumpElementList((List<GraphvisModelElement>) event.getObject())));
	}

	public void onUnselectItems(UnselectEvent event){
		System.out.println("UNSELECTION:" + event.getObject());
		FacesContext context = FacesContext.getCurrentInstance();		
		context.addMessage(null, new FacesMessage("Unselection: " + dumpElementList((List<GraphvisModelElement>) event.getObject())));
	}

	
	public void onSelectNodes(SelectEvent event){
		System.out.println("NODES SELECTION:" + event.getObject());
		FacesContext context = FacesContext.getCurrentInstance();		
		context.addMessage(null, new FacesMessage("Nodes Selection:" + dumpElementList((List<GraphvisModelElement>) event.getObject())));
	}

	public void onUnselectNodes(UnselectEvent event){
		System.out.println("NODES UNSELECTION:" + event.getObject());
		FacesContext context = FacesContext.getCurrentInstance();		
		context.addMessage(null, new FacesMessage("Nodes Unselection: " + dumpElementList((List<GraphvisModelElement>) event.getObject())));
	}
	

	public void onSelectEdges(SelectEvent event){
		System.out.println("EDGES SELECTION:" + event.getObject());
		FacesContext context = FacesContext.getCurrentInstance();		
		context.addMessage(null, new FacesMessage("Edges Selection:" + dumpElementList((List<GraphvisModelElement>) event.getObject())));
	}

	public void onUnselectEdges(UnselectEvent event){
		System.out.println("EDGES UNSELECTION:" + event.getObject());
		FacesContext context = FacesContext.getCurrentInstance();		
		context.addMessage(null, new FacesMessage("Edges Unselection: " + dumpElementList((List<GraphvisModelElement>) event.getObject())));
	}

	
	protected String dumpElementList(List<GraphvisModelElement> lElements){
		StringBuffer sb = new StringBuffer();
		for(GraphvisModelElement element : lElements){
			sb.append(element.getId() + " " + element.getLabel() + "\n");
		}
		return sb.toString();
	}

	
	public void setModelDescription(String modelDescription){		
	}
	
	public String getModelDescription(){
		StringBuffer sb = new StringBuffer();
		
		getGraphModel();
		
		sb.append(new Date().toString() + "\n");
		sb.append("NODES:\n");
		for(GraphvisNode node : graphModel.getNodes()){
			sb.append("Node " + node.getId() + ": " + node.getLabel() + " X:" + node.getX() + " Y:" + node.getY() + " " + node.getShape() + "\n");
		}
		
		sb.append("EDGES:\n");
		for(GraphvisEdge node : graphModel.getEdges()){
			sb.append("Node " + node.getId() + ": " + node.getLabel() 
					+ " From " + node.getSourceNode().getId() + " to " + node.getTargetNode().getId() + "\n");
		}

		return sb.toString();
	}

	

}
