package graphvis.demo.dijkstra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.primefaces.model.graphvis.GraphvisEdge;
import org.primefaces.model.graphvis.GraphvisModel;
import org.primefaces.model.graphvis.GraphvisNode;

/**
 * Dijkstra implementation adapted from http://www.vogella.com/articles/JavaAlgorithmsDijkstra/article.html 
 */
public class AlgoDijkstra {

	
	 private final List<GraphvisNode> nodes;
	 private final List<GraphvisEdge> edges;
	 private Set<GraphvisNode> settledNodes;
	 private Set<GraphvisNode> unSettledNodes;
	 private Map<GraphvisNode, GraphvisNode> predecessors;
	 private Map<GraphvisNode, Integer> distance;

	  public AlgoDijkstra(GraphvisModel GraphvisModel) {
	    // Create a copy of the array so that we can operate on this array
	    this.nodes = new ArrayList<GraphvisNode>(GraphvisModel.getNodes());
	    this.edges = new ArrayList<GraphvisEdge>(GraphvisModel.getEdges());
	  }

	  public void execute(GraphvisNode source) {
	    settledNodes = new HashSet<GraphvisNode>();
	    unSettledNodes = new HashSet<GraphvisNode>();
	    distance = new HashMap<GraphvisNode, Integer>();
	    predecessors = new HashMap<GraphvisNode, GraphvisNode>();
	    distance.put(source, 0);
	    unSettledNodes.add(source);
	    while (unSettledNodes.size() > 0) {
	      GraphvisNode node = getMinimum(unSettledNodes);
	      settledNodes.add(node);
	      unSettledNodes.remove(node);
	      findMinimalDistances(node);
	    }
	  }

	  private void findMinimalDistances(GraphvisNode node) {
	    List<GraphvisNode> adjacentNodes = getNeighbors(node);
	    for (GraphvisNode target : adjacentNodes) {
	      if (getShortestDistance(target) > getShortestDistance(node)
	          + getDistance(node, target)) {
	        distance.put(target, getShortestDistance(node)
	            + getDistance(node, target));
	        predecessors.put(target, node);
	        unSettledNodes.add(target);
	      }
	    }

	  }

	  private int getDistance(GraphvisNode node, GraphvisNode target) {
	    for (GraphvisEdge GraphvisEdge : edges) {
	      if (GraphvisEdge.getSourceNode().equals(node)
	          && GraphvisEdge.getTargetNode().equals(target)) {
	    	  //TODO use getData('distance')
	        return GraphvisEdge.getWidth();
	      }
	    }
	    throw new RuntimeException("Should not happen");
	  }

	  private List<GraphvisNode> getNeighbors(GraphvisNode node) {
	    List<GraphvisNode> neighbors = new ArrayList<GraphvisNode>();
	    for (GraphvisEdge GraphvisEdge : edges) {
	      if (GraphvisEdge.getSourceNode().equals(node)
	          && !isSettled(GraphvisEdge.getTargetNode())) {
	        neighbors.add(GraphvisEdge.getTargetNode());
	      }
	    }
	    return neighbors;
	  }

	  private GraphvisNode getMinimum(Set<GraphvisNode> vertexes) {
	    GraphvisNode minimum = null;
	    for (GraphvisNode GraphvisNode : vertexes) {
	      if (minimum == null) {
	        minimum = GraphvisNode;
	      } else {
	        if (getShortestDistance(GraphvisNode) < getShortestDistance(minimum)) {
	          minimum = GraphvisNode;
	        }
	      }
	    }
	    return minimum;
	  }

	  private boolean isSettled(GraphvisNode GraphvisNode) {
	    return settledNodes.contains(GraphvisNode);
	  }

	  private int getShortestDistance(GraphvisNode destination) {
	    Integer d = distance.get(destination);
	    if (d == null) {
	      return Integer.MAX_VALUE;
	    } else {
	      return d;
	    }
	  }

	  /*
	   * This method returns the path from the source to the selected target and
	   * NULL if no path exists
	   */
	  public LinkedList<GraphvisNode> getPath(GraphvisNode target) {
	    LinkedList<GraphvisNode> path = new LinkedList<GraphvisNode>();
	    GraphvisNode step = target;
	    // Check if a path exists
	    if (predecessors.get(step) == null) {
	      return null;
	    }
	    path.add(step);
	    while (predecessors.get(step) != null) {
	      step = predecessors.get(step);
	      path.add(step);
	    }
	    // Put it into the correct order
	    Collections.reverse(path);
	    return path;
	  }

	
}
