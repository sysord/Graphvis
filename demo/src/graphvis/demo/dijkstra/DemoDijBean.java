package graphvis.demo.dijkstra;

import graphvis.demo.common.GraphModelGenerator;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.graphvis.GraphvisEdge;
import org.primefaces.model.graphvis.GraphvisModel;
import org.primefaces.model.graphvis.GraphvisNode;
import org.primefaces.model.graphvis.impl.GraphvisModelImpl;

@ManagedBean
@SessionScoped
public class DemoDijBean {

	/**
	 * the graph model
	 */
	protected GraphvisModel graphModel = new GraphvisModelImpl();

	
	public GraphvisModel getGraphModel() {		
		return graphModel;
	}

	public void setGraphModel(GraphvisModel graphModel) {
		this.graphModel = graphModel;
	}
	
	/**
	 * Generate randomly a graph
	 */
	public void generateGraph(){

		GraphModelGenerator.fillGraphModel(graphModel, 10, 30, 50);
		FacesContext context = FacesContext.getCurrentInstance();	
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Graph generated with: " 
							+ graphModel.getNodes().size() + " nodes and " 
							+ graphModel.getEdges().size()  + " edeges.", ""));			
		
		graphModel.setLayout("ForceDirected");
		
	}
	
	/**
	 * lookup for a path and apply color on elements
	 */
	public void applyDijkstra(){
		
		AlgoDijkstra algo = new AlgoDijkstra(graphModel);
		algo.execute(graphModel.getNodes().get(0));
		List<GraphvisNode> path = algo.getPath(graphModel.getNodes().get(graphModel.getNodes().size() - 1));
		
		FacesContext context = FacesContext.getCurrentInstance();		
		//no path found
		if(path == null){
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No Path found between N1 and N" + (graphModel.getNodes().size() - 1), "" ));
			return;
		}
		
		//apply color on node and edges of the path
		GraphvisNode prevNode = null;
		String strPath = "";
		for(GraphvisNode pathNode : path){
			
			if(prevNode != null){
				for(GraphvisEdge edge : prevNode.getOutcomingEdges()){
					if(edge.getTargetNode().equals(pathNode)){
						edge.setColor("#ff0000");
						graphModel.updateEdge(edge);
					}
				}
				strPath += " -> ";
			}
			
			pathNode.setColor("#ff0000");
			graphModel.updateNode(pathNode);
			strPath += pathNode.getId();
			
			prevNode = pathNode;
		}
		
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Path found between N1 and N" + (graphModel.getNodes().size() - 1), "Path: " + strPath));			
		
	}
	
}
