package utilitarios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import sistema.No;
import sistema.Roteador;
import sistema.Vizinho;

/**
 * 
 * Classe utilizada para ler informacoes contidas no arquivo enlaces.config
 * 
 * @author Wilson
 * @author Pablo
 * 
 */
public class LeitorEnlacesConfig {
	private static LeitorEnlacesConfig instancia;

	public static LeitorEnlacesConfig getInstance(Roteador roteador) {
		if (instancia == null) {
			instancia = new LeitorEnlacesConfig(roteador);
		}
		return instancia;

	}

	final int INFINITY = 999;

	private Roteador roteador;

	private List<String> conteudo;

	private final String nome = "enlaces.config";

	private Map<List, String> mapa;

	private Map<String, Vizinho> vizinhos;

	/**
	 * @param roteador O roteador que cricou esse leitor
	 */
	private LeitorEnlacesConfig(Roteador roteador) {
		mapa = new HashMap<List, String>();
		this.roteador = roteador;
		conteudo = LeitorArquivo.leiaArquivo(nome);
		this.mapeiaConteudo();
		this.lerVizinhos();
	}

	/**
	 * Retorna o custo do enlace entre dois roteadores.
	 * Caso nao exista um enlace entre eles, sera retornado infinito.
	 * @param id1 
	 * @param id2
	 * @return O custo do enlace
	 */
	private int getCusto(String id1, String id2) {
		if (id1.equals(id2))
			return 0;

		List<String> enlace1 = new ArrayList<String>();
		enlace1.add(id1);
		enlace1.add(id2);
		if (mapa.containsKey(enlace1))
			return Integer.parseInt(mapa.get(enlace1));

		List<String> enlace2 = new ArrayList<String>();
		enlace2.add(id2);
		enlace2.add(id1);
		if (mapa.containsKey(enlace2))
			return Integer.parseInt(mapa.get(enlace2));

		return INFINITY;
	}

	/**
	 * Retorna uma lista contendo todos ids os vizinhos de um roteador especifico
	 * @param id O id do roteador que se deseja obter seus vizinhos
	 * @return Uma lista contendo todos os ids dos vizinhos.
	 */
	private List<String> getIdVizinhos(String id) {
		List<String> vizinhos = new ArrayList<String>();
		Set<List> chaves = mapa.keySet();
		Iterator it = chaves.iterator();
		while (it.hasNext()) {
			List enlace = (List) it.next();

			if (enlace.get(0).equals(id))
				vizinhos.add((String) enlace.get(1));
			else if (enlace.get(1).equals(id))
				vizinhos.add((String) enlace.get(0));

		}
		return vizinhos;
	}

	/**
	 * Retorna um mapa contendo todos os vizinhos do roteador que criou o leitor.
	 * @return
	 */
	public Map<String, Vizinho> getVizinhos() {
		return this.vizinhos;
	}

	/**
	 * Metodo utilizado para ler todos os vizinhos do roteador que criou o leitor. 
	 */
	private void lerVizinhos() {
		this.vizinhos = new HashMap<String, Vizinho>();
		LeitorRoteadorConfig leitorRoteadorConfig = LeitorRoteadorConfig
				.getInstance();
		List<String> vizinhosIds = this.getIdVizinhos(this.roteador.getId());

		Map<String, No> roteadores = leitorRoteadorConfig.getRoteadores();

		Iterator roteadoresIt = roteadores.keySet().iterator();
		while (roteadoresIt.hasNext()) {
			String roteadorId = (String) roteadoresIt.next();
			if (vizinhosIds.contains(roteadorId)) {
				vizinhos.put(roteadorId, new Vizinho(
						roteadores.get(roteadorId), this.getCusto(this.roteador
								.getId(), roteadorId), false));
			}
		}

	}

	/**
	 * Metodo utilizado para a partir dos dados fornecidos pelo leitor de arquivo.
	 * montar uma estrutura contendo as informacoes relevantes dos enlaces.
	 */
	private void mapeiaConteudo() {

		for (int i = 0; i < conteudo.size(); i++) {
			StringTokenizer st = new StringTokenizer(conteudo.get(i), " ");
			List<String> a = new ArrayList<String>();
			a.add(st.nextToken());
			a.add(st.nextToken());
			String id = st.nextToken();
			mapa.put(a, id);
		}

	}

}
