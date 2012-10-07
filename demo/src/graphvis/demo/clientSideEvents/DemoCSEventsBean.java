package graphvis.demo.clientSideEvents;

import graphvis.demo.common.GraphModelGenerator;

import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.graphvis.GraphvisModel;
import org.primefaces.model.graphvis.GraphvisNode;
import org.primefaces.model.graphvis.GraphvisNode.NODE_SHAPE;
import org.primefaces.model.graphvis.impl.GraphvisModelImpl;

@ManagedBean
@SessionScoped
public class DemoCSEventsBean {

	protected GraphvisModel graphModel;
	
	protected List<NODE_SHAPE> nodeShapes = Arrays.asList(GraphvisNode.NODE_SHAPE.values());
	
	public GraphvisModel getGraphModel() {
		if(graphModel == null){
			fillGraphModel();
		}
		return graphModel;
	}

	public void setGraphModel(GraphvisModel graphModel) {
		this.graphModel = graphModel;
	}
	

	public void fillGraphModel() {
		System.out.println("FILL GRAPH MODEL");
		if(graphModel == null){
			graphModel = new GraphvisModelImpl();
		}
		GraphModelGenerator.fillGraphModel(graphModel, 5, 10, 20);		
		graphModel.setLayout("ForceDirected");
	}

}
