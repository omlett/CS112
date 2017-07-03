package apps;

import structures.*;
import java.util.ArrayList;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
	
		/* COMPLETE THIS METHOD */
		
		PartialTreeList L = new PartialTreeList();
		
		for (int x = 0; x < graph.vertices.length; x++){
			Vertex vx = graph.vertices[x];
			PartialTree T = new PartialTree(vx);
			Vertex.Neighbor nn = graph.vertices[x].neighbors;
			MinHeap<PartialTree.Arc> arcHeap = T.getArcs();
			
			while (nn != null){
				PartialTree.Arc tempArc = new PartialTree.Arc(graph.vertices[x], nn.vertex, nn.weight);
				arcHeap.insert(tempArc);
				
				if (nn.next == null){
					break;
				}
				nn = nn.next;
				
				if (nn.vertex == graph.vertices[x]){
					nn = nn.next;
				}
			}
			
			L.append(T);
		}
		
		return L;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		
		/* COMPLETE THIS METHOD */
		ArrayList<PartialTree.Arc> finalList = new ArrayList<PartialTree.Arc>();
		int count = ptlist.size();
		
		while (count>1){
			PartialTree tracker = ptlist.remove();
			
			if(tracker == null){
				break;
			}
			
			MinHeap<PartialTree.Arc> arcs = tracker.getArcs();
			PartialTree.Arc high = arcs.deleteMin();
			Vertex v2 = high.v2;
			Vertex v1 = tracker.getRoot();
			
			if ((v1 == v2) || (v1 == v2.parent)){
				high = arcs.deleteMin();
				v2 = high.v2.parent;
			}
			
			finalList.add(high);
			
			PartialTree removal = ptlist.removeTreeContaining(v2);
			if (removal == null){
				continue;
			}
			
			removal.getRoot().parent = tracker.getRoot();
			removal.merge(tracker);
			ptlist.append(tracker);
			
			count--;
		}
		return null;
	}
}
