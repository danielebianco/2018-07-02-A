package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private AirportIdMap airportIdMap;
	private List<Airport> airports;
	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	
	public Model() {
		this.airportIdMap = new AirportIdMap();
		this.dao = new ExtFlightDelaysDAO();
		this.airports = new ArrayList<Airport>();
	}
	
	public void creaGrafo(double  miglia) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.airports = dao.getAirports(airportIdMap);
		Graphs.addAllVertices(grafo, airports);
		for(Airport a1 : airports) {
			for (Airport a2 : airports) {
				if(a1 != a2) {
					double peso = dao.getAvgDistance(a1, a2);
					if(peso != 0.0 && peso > miglia) {
						Graphs.addEdge(grafo, a1, a2, peso);
					}
				}
			}
		}
	}
	
	public List<Airport> displayNeighbours(Airport airport) {
		
		List<Airport> vicini = new ArrayList<Airport>();
		
		vicini.addAll(Graphs.neighborListOf(grafo, airport));		

		return vicini;
	}
	
	public List<Airport> stampaRisultato(Airport airport) {
		
		List<DefaultWeightedEdge> list = new ArrayList<DefaultWeightedEdge>(grafo.edgeSet());
		
		Collections.sort(list, new Comparator<DefaultWeightedEdge>() {
			
			@Override
			public int compare(DefaultWeightedEdge o1, DefaultWeightedEdge o2) {
				return - Double.compare(grafo.getEdgeWeight(o1), grafo.getEdgeWeight(o2)) ;
			}
		});
		
		List<AirportResult> result = new ArrayList<AirportResult>();
		List<Airport> output = new ArrayList<Airport>();
		
		for(int i=0 ; i<grafo.edgeSet().size(); i++) {
			DefaultWeightedEdge arco = list.get(i);
			Airport a1 = grafo.getEdgeSource(arco);
			Airport a2 = grafo.getEdgeTarget(arco);
			double avg = grafo.getEdgeWeight(arco);
			AirportResult r = new AirportResult(a1,a2,avg);
			result.add(r);
		}
		
		List<Airport> adiacenti = this.displayNeighbours(airport);
		
		for(AirportResult r : result) {
			for(Airport a : adiacenti) {
				if(r.getA1().getId() == airport.getId() && a.getId() == r.getA2().getId()) {
					output.add(r.getA2());
				}
			}
		}
			
		return output;
	}
	
	
	public SimpleWeightedGraph<Airport, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	

}
