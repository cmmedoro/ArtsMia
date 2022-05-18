package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private Graph<ArtObject, DefaultWeightedEdge> grafo;
	private ArtsmiaDAO dao;
	//Identity Map ---> mantiene corrispondenza fra id e oggetto ---> utile per creare gli oggetti una sola volta
	private Map<Integer, ArtObject> idMap;
	
	public Model() {
		this.dao = new ArtsmiaDAO();
		idMap = new HashMap<>(); //per riempirla modifico il metodo del dao fornito
	}
	
	public void creaGrafo() {
		this.grafo = new SimpleWeightedGraph<ArtObject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		//aggiungo i vertici
		this.dao.listObjects(idMap);
		Graphs.addAllVertices(this.grafo, idMap.values());
		//Aggiungo gli archi
		//APPROCCIO 1: 	doppo ciclo for sui vertici per recuperare tutte le coppie di vertici e chiedo se fra questi vi è un arco
		/*for(ArtObject a1 : this.grafo.vertexSet()) {
			for(ArtObject a2 : this.grafo.vertexSet()) {
				if(!a1.equals(a2) && !this.grafo.containsEdge(a1, a2)) {
					//se i vertici sono diversi e se non vi è già un arco fra i due (grafo non orientato)
					int peso = dao.getPeso(a1,a2);
					if(peso>0) {
						Graphs.addEdgeWithVertices(this.grafo, a1, a2, peso);
					}
				}
			}
		}*/
		//APPROCCIO 2
		for(Adiacenza a : this.dao.getAdiacenze(idMap)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getA1(), a.getA2(), a.getPeso());
		}
		System.out.println("Grafo creato!");
		System.out.println("Numero vertici: "+this.grafo.vertexSet().size());
		System.out.println("Numero archi: "+this.grafo.edgeSet().size());
	}
	public int numeroVertici() {
		return this.grafo.vertexSet().size();
	}
	public int numeroArchi() {
		return this.grafo.edgeSet().size();
	}

	public ArtObject getObject(int objectId) {
		return idMap.get(objectId);
	}

	public int getComponenteConnessa(ArtObject vertice) {
		Set<ArtObject> visitati = new HashSet<>();
		DepthFirstIterator<ArtObject, DefaultWeightedEdge> dfi = new DepthFirstIterator<>(this.grafo, vertice); 
		while(dfi.hasNext()) {
			visitati.add(dfi.next());
		}
		return visitati.size();
	}
}
