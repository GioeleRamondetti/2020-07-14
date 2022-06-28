package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;
import it.polito.tdp.PremierLeague.model.Event.EventType;

public class Model {
	private PremierLeagueDAO dao=new PremierLeagueDAO();
	private Graph<Team,DefaultWeightedEdge> grafo;
	private int narchi;
	private int nverici;
	private List<Team> peggiori;
	private List<Team> migliori;
	private List<reporter> reporter;
	private Map<String,Team>  ordineMigliori;
	private Map<String,Team>  ordinePeggiori;
	private PriorityQueue<Event> queue;
	private List<Team> listami;
	private List<Team> listap;
	private int contacritico;
	public int getNarchi() {
		return narchi;
	}

	

	public int getNverici() {
		return nverici;
	}

	public void creagrafo() {
		grafo=new SimpleDirectedWeightedGraph<Team,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, dao.listAllTeams());
		List<Team> lista=new ArrayList<Team>(squadre());
		for(Team t1:lista) {
			for(Team t2:lista) {
				if(t1.getTeamID()!=t2.getTeamID()  && grafo.getEdge(t2, t1)==null && grafo.getEdge(t1, t2)==null) {
					int puntit1=0;
					int puntit2=0;
					puntit1=dao.listAllMatchesDRAWbyteam(t1.getTeamID());
					puntit1=puntit1+3*dao.listAllMatchesWONbyteam(t1.getTeamID());
					puntit1=puntit1 +dao.listAllMatchesDRAWbyteamaway(t1.getTeamID());
					puntit1=puntit1+3*dao.listAllMatchesWONbyteamaway(t1.getTeamID());
					puntit2=dao.listAllMatchesDRAWbyteam(t2.getTeamID());
					puntit2=puntit2+3*dao.listAllMatchesWONbyteam(t2.getTeamID());
					puntit2=puntit2 +dao.listAllMatchesDRAWbyteamaway(t2.getTeamID());
					puntit2=puntit2+3*dao.listAllMatchesWONbyteamaway(t2.getTeamID());
					if(dao.derby(t1.getTeamID(),t2.teamID)==0) {
						puntit1=puntit1-1;
						puntit2=puntit2-1;
						}
					if((puntit1-puntit2)!=0) {
						if((puntit1-puntit2)>0) {
						grafo.addEdge(t1, t2);
						grafo.setEdgeWeight(grafo.getEdge(t1, t2), (puntit1-puntit2));
						}else {
							grafo.addEdge(t2, t1);
							grafo.setEdgeWeight(grafo.getEdge(t2, t1), (puntit2-puntit1));
						}
					}
				}
			}
		}
		this.narchi=grafo.edgeSet().size();
		this.nverici=grafo.vertexSet().size();
	}
	
	public void cercamigliori(Team t) {
		ordineMigliori=new TreeMap<String,Team>();
		ordinePeggiori=new TreeMap<String,Team>();
		int puntit1=0;
		migliori=new LinkedList<Team>();
		peggiori=new LinkedList<Team>();
		puntit1=dao.listAllMatchesDRAWbyteam(t.getTeamID());
		puntit1=puntit1+3*dao.listAllMatchesWONbyteam(t.getTeamID());
		puntit1=puntit1 +dao.listAllMatchesDRAWbyteamaway(t.getTeamID());
		puntit1=puntit1+3*dao.listAllMatchesWONbyteamaway(t.getTeamID());
		for(Team t1:squadre()) {
			if(t1.getTeamID()!=t.getTeamID()) {
				int punti=0;
				punti=dao.listAllMatchesDRAWbyteam(t1.getTeamID());
				punti=punti+3*dao.listAllMatchesWONbyteam(t1.getTeamID());
				punti=punti +dao.listAllMatchesDRAWbyteamaway(t1.getTeamID());
				punti=punti+3*dao.listAllMatchesWONbyteamaway(t1.getTeamID());
				if(dao.derby(t1.getTeamID(),t.teamID)==0) {
					punti=punti-1;
					puntit1=puntit1-1;
				}
					
				
				
				if(punti>puntit1) {
					migliori.add(t1);
					this.ordineMigliori.put(t1.getName()+" ("+(-(punti-puntit1))+") ", t1);
					listami.add(t1);
				}else {
					peggiori.add(t1);
					this.ordinePeggiori.put(t1.getName()+" ("+(puntit1-punti)+") ", t1);
					listap.add(t1);
				}
			}
		}
		System.out.println(squadre().size());
		
	}
	
	

	public Map<String, Team> getOrdineMigliori() {
		return ordineMigliori;
	}



	


	public Map<String, Team> getOrdinePeggiori() {
		return ordinePeggiori;
	}

	public void simula(int numero, int soglia) {
		this.queue=new PriorityQueue<Event>();
		this.listami=new LinkedList<Team>();
		this.listap=new LinkedList<Team>();
		this.reporter=new LinkedList<reporter>();
		this.contacritico=0;
		for(Team t:squadre()) {
			reporter.add(new reporter(numero,t));
		}
		creaEventi(soglia);
	}

	public void run(int soglia) {
		while(!this.queue.isEmpty()) {
			Event e=this.queue.poll();
			processEvent(e,soglia);
		}
	}

	private void processEvent(Event e,int soglia) {
		
	int x=(int)(Math.random()*100);
		switch(e.getType()) {
			case vittoria:
				for(int i=0;i<reporter.size();i++) {
					if(reporter.get(i).getT().teamID==e.getM().getTeamHomeID()) {
						if(reporter.get(i).getN()<soglia) {
							this.contacritico++;
						}
						if(x>50) {
							if(reporter.get(i).getN()>0) {
								
								//sposto reporter
								reporter.get(i).setN(reporter.get(i).getN()-1);
								cercamigliori(reporter.get(i).getT());
								int migliore=(int)(Math.random()*ordineMigliori.size());
								
								for(int j=0;j<reporter.size();j++) {
									if(reporter.get(j).getT().equals(listami.get(migliore))) {
										reporter.get(j).setN(reporter.get(j).getN()+1);
										reporter.get(j).getListaRep().add(reporter.get(j).getN());
									}
								}
							}
						}
					}
				}
				break;
			case sconfitta:
				for(int i=0;i<reporter.size();i++) {
					if(reporter.get(i).getT().teamID==e.getM().getTeamHomeID()) {
						if(reporter.get(i).getN()<soglia) {
							this.contacritico++;
						}
						if(x>80) {
							
							if(reporter.get(i).getN()>0) {
								int spostati=(int)(Math.random()*reporter.get(i).getN());
								reporter.get(i).setN(reporter.get(i).getN()-spostati);
								cercamigliori(reporter.get(i).getT());
								int migliore=(int)(Math.random()*ordineMigliori.size());
								
								for(int j=0;j<reporter.size();j++) {
									if(reporter.get(j).getT().equals(listami.get(migliore))) {
										reporter.get(j).setN(reporter.get(j).getN()+spostati);
										reporter.get(j).getListaRep().add(reporter.get(j).getN());
									}
								}
							}
						}
					}
				}
				break;
			case pareggio:
				for(int i=0;i<reporter.size();i++) {
					if(reporter.get(i).getT().teamID==e.getM().getTeamHomeID()) {
						if(reporter.get(i).getN()<soglia) {
							this.contacritico++;
						}
					}
				}
				break;
		
		}
	}

		public List<reporter> getReporter() {
		return reporter;
	}






		public int getContacritico() {
		return contacritico;
	}



	


		private void creaEventi(int soglia) {
			// 
			// tempo parte da 0
			int time=0;
			List<Match> l=new LinkedList<Match>(dao.listAllMatches());
			for(int i=0;i<l.size() ;i++) {
				if(l.get(i).resultOfTeamHome==1) {
					this.queue.add(new Event(time++,l.get(i),EventType.vittoria));
				}
				if(l.get(i).resultOfTeamHome==0) {
					this.queue.add(new Event(time++,l.get(i),EventType.pareggio));
				}
				if(l.get(i).resultOfTeamHome==-1) {
					this.queue.add(new Event(time++,l.get(i),EventType.sconfitta));
				}
				
				
			}
			run(soglia);
			
			
		}

	public Graph<Team, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public List<Team> squadre(){
		return dao.listAllTeams();
	}
}
