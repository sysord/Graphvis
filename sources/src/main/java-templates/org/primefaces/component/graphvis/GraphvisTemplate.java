import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.graphvis.GraphvisModel;
import org.primefaces.model.graphvis.GraphvisModelElement;
import org.primefaces.util.Constants;



	
	public final static String GRAPHVIS_AJAX_EVENT = "graphvisAjax";
	public final static String ITEM_SELECT_EVENT = "select";
	public final static String ITEM_UNSELECT_EVENT = "unselect";
	public final static String NODE_SELECT_EVENT = "nodeSelect";
	public final static String NODE_UNSELECT_EVENT = "nodeUnselect";
	public final static String EDGE_SELECT_EVENT = "edgeSelect";
	public final static String EDGE_UNSELECT_EVENT = "edgeUnselect";
    private static final Collection<String> SELECTION_EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(ITEM_SELECT_EVENT, NODE_SELECT_EVENT, EDGE_SELECT_EVENT));
    private static final Collection<String> UNSELECTION_EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(ITEM_UNSELECT_EVENT, NODE_UNSELECT_EVENT, EDGE_UNSELECT_EVENT));

    
	private final static String DEFAULT_EVENT = GRAPHVIS_AJAX_EVENT;
	
    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(GRAPHVIS_AJAX_EVENT, ITEM_SELECT_EVENT, ITEM_UNSELECT_EVENT,
																								NODE_SELECT_EVENT, NODE_UNSELECT_EVENT, 
																								EDGE_SELECT_EVENT, EDGE_UNSELECT_EVENT
																								)
    														);

    public boolean isContentLoadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_contentLoad");
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }

    
    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

    
    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            //----------------------
        	//Selection events:
        	//----------------------
            if(SELECTION_EVENT_NAMES.contains(eventName)){

            	String itemIds = params.get(clientId + "_itemIds");

            	//Update graphviz model:
            	GraphvisModel graphModel = getValue(); 
            	List<GraphvisModelElement> lSelectedItems = getGraphvisItems(itemIds);
            	if(graphModel != null){
                    graphModel.selectItems(lSelectedItems);            		            			
            	}                	

                //raise select event
            	SelectEvent selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), lSelectedItems);
            	selectEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
                super.queueEvent(selectEvent);
            }
            
            //----------------------
        	//Unselection events:
        	//----------------------
            if(UNSELECTION_EVENT_NAMES.contains(eventName)){
            	String itemIds = params.get(clientId + "_itemIds");

            	//Update graphviz model:
            	GraphvisModel graphModel = getValue(); 
            	List<GraphvisModelElement> lUnselectedItems = getGraphvisItems(itemIds);
            	if(graphModel != null){
                    graphModel.unselectItems(lUnselectedItems);            		            			
            	}                	

                //raise select event
            	UnselectEvent selectEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), lUnselectedItems);
            	selectEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
                super.queueEvent(selectEvent);
            }
                                    
        }
        else {
            super.queueEvent(event);
        }
    }

    /**
     * Convert param id string to item list
     * @param itemIds
     * @return
     */
    private List<GraphvisModelElement> getGraphvisItems(String itemIds){ 
    	List<GraphvisModelElement> lSelectedItems = new ArrayList<GraphvisModelElement>();
       	GraphvisModel graphModel = getValue(); 
    	if(graphModel != null) {
    		for(String itemId : itemIds.split(",")){
    			GraphvisModelElement item = graphModel.getGraphvisModelElement(itemId);
        		if(item != null){
        			lSelectedItems.add(item);
        		}
    		}
    	}
    	return lSelectedItems;
    }


