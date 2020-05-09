package it.polito.tdp.extflightdelays.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private Graph<Airport,DefaultWeightedEdge> graph;
	private Map<Integer, Airport> idMap;
    private ExtFlightDelaysDAO dao;
	
	public Model() {
		idMap = new HashMap<Integer,Airport>();
		dao = new ExtFlightDelaysDAO();
		dao.loadAllAirports(idMap);
	}
	
	/**
	 * Controllo se esiste l'arco:
	 * se non esiste edge==null, lo creo. 
	 * se esiste aggiorno il peso 
	 */
	public void creaGrafo(int dMinima) {

		this.graph=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//vertici
		Graphs.addAllVertices(this.graph, idMap.values());
		
		//archi
		List<Tragitto> tragitti =dao.migliorCollegamento(idMap, dMinima);
		
		for(Tragitto t: tragitti) {
			
			DefaultWeightedEdge edge = graph.getEdge(t.getA1(), t.getA2());
			
			if(edge==null) {
				Graphs.addEdge(this.graph, t.getA1(), t.getA2(), t.getPeso());
			}else {
				double peso = graph.getEdgeWeight(edge);
				double newPeso = (peso + t.getPeso())/2;
				graph.setEdgeWeight(edge, newPeso);
			}
		}
		
	}
	
	public int nVertici() {
		return this.graph.vertexSet().size();
	}
	
	public int nArchi() {
		return this.graph.edgeSet().size();
	}
	
	public List<Tragitto> getTragitti() {

		List<Tragitto> result = new ArrayList<Tragitto>();
		
		for (DefaultWeightedEdge e : this.graph.edgeSet()) {
			
			result.add(new Tragitto(this.graph.getEdgeSource(e), this.graph.getEdgeTarget(e),
					this.graph.getEdgeWeight(e)));
		}
		return result;

	}
	
}
