package it.polito.tdp.crimes.model;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private SimpleWeightedGraph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	private List<String> percorsoMigliore;
	
	public Model() {
		dao = new EventsDao();
	}
	
	public List<String> getCategorie(){
		return dao.getCategorie();
	}
	
	public void creaGrafo(String categoria, int mese) {
		grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, dao.getVertici(categoria, mese));
		//aggiunta archi
		
		for(Adiacenza a : dao.getArchi(categoria, mese)) {
			if(this.grafo.getEdge(a.getV1(), a.getV2())==null) {
				Graphs.addEdgeWithVertices(grafo, a.getV1(), a.getV2(), a.getPeso());
			}
		}
		System.out.println("Grafo creato con "+ grafo.vertexSet().size() + " vertici");
		System.out.println(" e con archi " + grafo.edgeSet().size());
	}
	
	public List<Adiacenza> getArchi(){
		//calcolo il peso medio degli archi presenti nel grafo
		double pesoMedio = 0;
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			pesoMedio += grafo.getEdgeWeight(e);
		}
		pesoMedio = pesoMedio/grafo.edgeSet().size();
		
		//filtro gli archi tenendo quelli con peso > di quello medio
		List<Adiacenza> result = new LinkedList<>();
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e) > pesoMedio) {
				result.add(new Adiacenza(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), grafo.getEdgeWeight(e)));
			}
		}
		return result;
	}
	
	public List<String> trovaPercorso(String sorgente, String destinazione){
		this.percorsoMigliore = new LinkedList<String>();
		List <String> parziale = new LinkedList<String>();
		parziale.add(sorgente);
		cerca(destinazione, parziale);
		return this.percorsoMigliore;
	}
	
	private void cerca(String destinazione, List<String> parziale) {
		//caso terminale
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			if(parziale.size() > this.percorsoMigliore.size()) {
				this.percorsoMigliore = new LinkedList<>(parziale);
			}
			return;
		}
		
		//...altrimenti 
		//devo aggiungere un nuovo vertice
		//essi sono i vicini del mio ultimo vertice e provo ad aggiungerli uno ad uno
		for(String vicino: Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(vicino)) {
				parziale.add(vicino);
				cerca(destinazione, parziale);
				parziale.remove(parziale.size()-1);
			}
		}
	}
	
}
