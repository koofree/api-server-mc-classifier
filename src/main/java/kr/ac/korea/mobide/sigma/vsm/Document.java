package kr.ac.korea.mobide.sigma.vsm;

import java.util.HashMap;
import java.util.Set;

import kr.ac.korea.mobide.sigma.common.Tokenizer;

public class Document {
	protected Document() {
		this.mapTermIDDocElement = new HashMap<Integer, DocElement>();
	}
	
	protected Document(String string, String section, VectorGenerator generator) {
		this();
		this.addContent(string, section, generator);
	}
	
	protected Document(HashMap<String, String> mapSectionString, VectorGenerator generator) {
		this();
		for (String section : mapSectionString.keySet()) {
			this.addContent(mapSectionString.get(section), section, generator);
		}
	}
	
	public HashMap<Integer, DocElement> getMapTermIDDocElement() {
		return this.mapTermIDDocElement;
	}
	
	public Set<Integer> keySet() {
		return this.mapTermIDDocElement.keySet();
	}
	
	public Vector getVector(VectorGenerator generator) {
		HashMap<Integer, Double> mapTermIDWeight = new HashMap<Integer, Double>();
		for (int termID : this.mapTermIDDocElement.keySet()) {
			DocElement docElement = this.mapTermIDDocElement.get(termID);
			double tf = (double)docElement.tf;
			double idf = Math.log((double)(generator.getNumDoc()+1)/(double)generator.getDF(termID));
			mapTermIDWeight.put(termID, tf*idf*docElement.sectionWeight);
		}
		return new Vector(mapTermIDWeight);
	}
	
	protected void addTerm(Integer termID, String section) {
		double sectionWeight = Document.getSectionWeight(section);
		if (this.mapTermIDDocElement.containsKey(termID)) {
			DocElement docElement = this.mapTermIDDocElement.get(termID);
			docElement.tf += 1;
			if (docElement.sectionWeight < sectionWeight) {
				docElement.sectionWeight = sectionWeight;
			}
		} else {
			DocElement docElement = new DocElement(1, sectionWeight);
			this.mapTermIDDocElement.put(termID, docElement);
		}
	}
	
	protected void addContent(String content, String section, VectorGenerator generator) {
		for (String term : Tokenizer.getListToken(content)) {
			int termID = generator.getTermID(term);
			if (termID == -1) continue;
			this.addTerm(termID, section);
		}
	}
	
	public static double getSectionWeight(String section) {
		double weight = 1.0;
		if (Document.MapSectionWeight.containsKey(section)) {
			weight = Document.MapSectionWeight.get(section);
		}
		return weight;
	}
	
	public class DocElement {
		public DocElement(int tf, double sectionWeight) {
			this.tf = tf;
			this.sectionWeight = sectionWeight;
		}
		
		private int tf;
		private double sectionWeight;
	}
	
	private HashMap<Integer, DocElement> mapTermIDDocElement;
	
	private static HashMap<String, Double> MapSectionWeight;
	
	public static final String	SECTION_PLAIN				= "plain";
	public static final String	SECTION_DMOZ_TITLE			= "dmoz_title";
	public static final String	SECTION_DMOZ_DESCRIPTION	= "dmoz_description";
	public static final String	SECTION_DMOZ_URL			= "dmoz_url";
	public static final String	SECTION_ITEM_TITLE			= "item_title";
	public static final String	SECTION_ITEM_CATEGORY		= "item_category";
	public static final String	SECTION_ITEM_DESCRIPTION	= "item_description";
	public static final String	SECTION_SMS_SEND			= "sms_send";
	public static final String	SECTION_SMS_RECEIVED		= "sms_received";
	public static final String	SECTION_FILE_INFO			= "file_info";
	public static final String	SECTION_WEB_USAGE			= "web_usage";
	public static final String	SECTION_EMAIL_TITLE			= "email_title";
	public static final String	SECTION_EMAIL_CONTENT		= "email_content";
	
	static {
		Document.MapSectionWeight = new HashMap<String, Double>();
		Document.MapSectionWeight.put(Document.SECTION_PLAIN, 1.0);
		Document.MapSectionWeight.put(Document.SECTION_DMOZ_TITLE, 2.0);
		Document.MapSectionWeight.put(Document.SECTION_DMOZ_DESCRIPTION, 1.0);
		Document.MapSectionWeight.put(Document.SECTION_DMOZ_URL, 1.0);
		Document.MapSectionWeight.put(Document.SECTION_ITEM_TITLE, 2.0);
		Document.MapSectionWeight.put(Document.SECTION_ITEM_CATEGORY, 1.0);
		Document.MapSectionWeight.put(Document.SECTION_ITEM_DESCRIPTION, 1.0);
		Document.MapSectionWeight.put(Document.SECTION_SMS_SEND, 1.0);
		Document.MapSectionWeight.put(Document.SECTION_SMS_RECEIVED, 1.0);
		Document.MapSectionWeight.put(Document.SECTION_FILE_INFO, 1.0);
		Document.MapSectionWeight.put(Document.SECTION_WEB_USAGE, 1.0);
		Document.MapSectionWeight.put(Document.SECTION_EMAIL_TITLE, 2.0);
		Document.MapSectionWeight.put(Document.SECTION_EMAIL_CONTENT, 1.0);
	}
}
