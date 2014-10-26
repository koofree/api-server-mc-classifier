package kr.ac.korea.mobide.sigma.vsm;

import java.util.HashMap;

import kr.ac.korea.mobide.sigma.common.Tokenizer;

public class VectorSpaceModel implements VectorGenerator {
	public VectorSpaceModel() {
		this.mapTermTermID = new HashMap<String, Integer>();
		this.mapTermIDDF = new HashMap<Integer, Integer>();
		this.mapDocIDVector = new HashMap<Integer, Vector>();
		this.mapDocIDDocument = new HashMap<Integer, Document>();
		this.numTerm = 0;
		this.numDoc = 0;
	}
	
	public void addDocument(int docID, String content, String section) {
		Document document;
		if (this.mapDocIDDocument.containsKey(docID)) {
			document = this.mapDocIDDocument.get(docID);
		} else {
			document = new Document();
			this.mapDocIDDocument.put(docID, document);
			this.numDoc += 1;
		}
		for (String term : Tokenizer.getListToken(content)) {
			int termID;
			if (this.mapTermTermID.containsKey(term)) {
				termID = this.mapTermTermID.get(term);
			} else {
				termID = this.numTerm;
				this.mapTermTermID.put(term, this.numTerm++);
			}
			document.addTerm(termID, section);
		}
	}
	
	public void setMapDocIDVector() {
		for (Document document : this.mapDocIDDocument.values()) {
			for (int termID : document.keySet()) {
				if (this.mapTermIDDF.containsKey(termID)) {
					this.mapTermIDDF.put(termID, this.mapTermIDDF.get(termID)+1);
				} else {
					this.mapTermIDDF.put(termID, 1);
				}
			}
		}
		for (Integer docID : this.mapDocIDDocument.keySet()) {
			Document document = this.mapDocIDDocument.get(docID);
			Vector vector = document.getVector(this);
			this.mapDocIDVector.put(docID, vector);
		}
		this.mapDocIDDocument.clear();
	}
	
	public void clearMapDocIDVector() {
		this.mapDocIDVector.clear();
	}
	
	public HashMap<String, Integer> getMapTermTermID() {
		return this.mapTermTermID;
	}
	
	public HashMap<Integer, Integer> getMapTermIDDF() {
		return this.mapTermIDDF;
	}
	
	public HashMap<Integer, Vector> getMapDocIDVector() {
		return this.mapDocIDVector;
	}

	public Vector getVector(int docID) {
		return this.mapDocIDVector.get(docID);
	}
	
	public int getNumTerm() {
		return this.numTerm;
	}
	
	@Override
	public int getTermID(String term) {
		int termID = -1;
		if (term == null) return termID;
		if (this.mapTermTermID.containsKey(term)) {
			termID = this.mapTermTermID.get(term);
		}
		return termID;
	}
	
	@Override
	public int getDF(int termID) {
		int df = 1;
		if (this.mapTermIDDF.containsKey(termID)) {
			df = this.mapTermIDDF.get(termID);
		}
		return df;
	}
	
	@Override
	public int getNumDoc() {
		return this.numDoc;
	}
	
	public void clear() {
		this.mapTermTermID.clear();
		this.mapTermIDDF.clear();
		this.mapDocIDVector.clear();
		this.mapDocIDDocument.clear();
	}
	
	public static Vector getVector(String content, String section, VectorGenerator generator) {
		return new Document(content, section, generator).getVector(generator);
	}
	
	public static Vector getVector(HashMap<String, String> mapSectionContent, VectorGenerator generator) {
		return new Document(mapSectionContent, generator).getVector(generator);
	}
	
	public static HashMap<Integer, HashMap<Integer, Double>> getMapTermIDPosting(HashMap<Integer, Vector> mapIDVector) {
		HashMap<Integer, HashMap<Integer, Double>> mapTermIDPosting = new HashMap<Integer, HashMap<Integer, Double>>();
		for (int id : mapIDVector.keySet()) {
			Vector vector = mapIDVector.get(id);
			for (int termID : vector.keySet()) {
				HashMap<Integer, Double> posting;
				if (mapTermIDPosting.containsKey(termID)) {
					posting = mapTermIDPosting.get(termID);
					posting.put(id, vector.getWeight(termID));
				} else {
					posting = new HashMap<Integer, Double>();
					posting.put(id, vector.getWeight(termID));
					mapTermIDPosting.put(termID, posting);
				}
			}
		}
		return mapTermIDPosting;
	}
	
	public static HashMap<Integer, Double> getMapIDScalar(HashMap<Integer, Vector> mapIDVector) {
		HashMap<Integer, Double> mapIDScalar = new HashMap<Integer, Double>();
		for (int id : mapIDVector.keySet()) {
			Vector vector = mapIDVector.get(id);
			mapIDScalar.put(id, vector.scalar());
		}
		return mapIDScalar;
	}
	
	public static HashMap<Integer, Double> getMapIDCosine(Vector queryVector, InvertedIndex index) {
		HashMap<Integer, Double> mapIDScore = new HashMap<Integer, Double>();
		if (queryVector.isEmpty() || queryVector.scalar() == 0.0) return mapIDScore;
		for (int termID : queryVector.keySet()) {
			HashMap<Integer, Double> posting = index.getPosting(termID);
			for (int id : posting.keySet()) {
				double score = posting.get(id)*queryVector.getWeight(termID);
				if (mapIDScore.containsKey(id)) {
					mapIDScore.put(id, mapIDScore.get(id)+score);
				} else {
					mapIDScore.put(id, score);
				}
			}
		}
		double queryScalar = queryVector.scalar();
		for (int id : mapIDScore.keySet()) {
			mapIDScore.put(id, mapIDScore.get(id)/(queryScalar*index.getScalar(id)));
		}
		return mapIDScore;
	}
	
	private HashMap<String, Integer> mapTermTermID;
	private HashMap<Integer, Integer> mapTermIDDF;
	private HashMap<Integer, Vector> mapDocIDVector;
	private HashMap<Integer, Document> mapDocIDDocument;
	private int numDoc;
	private int numTerm;
	
	public static final String	TERM_NUM_TRAINING_DOC	= "number_of_training_doc";
	public static final int		TERMID_NUM_TRAINING_DOC	= Integer.MAX_VALUE;
}
