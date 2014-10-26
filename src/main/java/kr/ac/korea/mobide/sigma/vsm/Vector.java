package kr.ac.korea.mobide.sigma.vsm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Vector implements Serializable {
	public Vector() {
		this.mapTermIDWeight = new HashMap<Integer, Double>();
	}
	
	public Vector(HashMap<Integer, Double> mapTermIDWeight) {
		this.mapTermIDWeight = mapTermIDWeight;
	}
	
	public HashMap<Integer, Double> getMapTermIDWeight() {
		return this.mapTermIDWeight;
	}
	
	public double getWeight(int termID) {
		if (this.mapTermIDWeight.containsKey(termID)) {
			return this.mapTermIDWeight.get(termID);
		} else {
			return 0.0;
		}
	}
	
	public Set<Integer> keySet() {
		return this.mapTermIDWeight.keySet();
	}
		
	public double scalar() {
		double scalar = 0.0;
		for (double weight : this.mapTermIDWeight.values()) {
			scalar += weight*weight;
		}
		return Math.sqrt(scalar);
	}
		
	public boolean isEmpty() {
		if (this.mapTermIDWeight.size() == 0) return true;
		else return false;
	}
	
	public double cosine(Vector vector) {
		double cosine = 0.0;
		if (this.isEmpty() || vector.isEmpty()) return cosine;
		if (this.scalar() == 0 || vector.scalar() == 0) return cosine;
		if (this.mapTermIDWeight.size() <= vector.mapTermIDWeight.size()) {
			for (int termID : this.mapTermIDWeight.keySet()) {
				if (vector.mapTermIDWeight.containsKey(termID)) {
					cosine += this.mapTermIDWeight.get(termID)*vector.mapTermIDWeight.get(termID);
				}
			}
		} else {
			for (int termID : vector.mapTermIDWeight.keySet()) {
				if (this.mapTermIDWeight.containsKey(termID)) {
					cosine += this.mapTermIDWeight.get(termID)*vector.mapTermIDWeight.get(termID);
				}
			}
		}
		cosine /= (this.scalar()*vector.scalar());
		return cosine;
	}
	
	public String toString() {
		return getClass().getName()+"[mapTermIDWeight="+this.mapTermIDWeight.toString()+",scalar="+this.scalar()+"]";
	}
	
	public double remove(int termID) {
		return this.mapTermIDWeight.remove(termID);
	}
	
	public Vector multiply(double value) {
		for (int termID : this.mapTermIDWeight.keySet()) {
			this.mapTermIDWeight.put(termID, this.mapTermIDWeight.get(termID)*value);
		}
		return this;
	}
	
	public Vector unitVector() {
		return this.multiply(1.0/this.scalar());
	}
	
	public void clear() {
		this.mapTermIDWeight.clear();
	}
	
	public static Vector unitVector(Vector vector) {
		Vector uv = new Vector();
		if (vector.isEmpty()) return uv;
		for (int termID : vector.mapTermIDWeight.keySet()) {
			uv.mapTermIDWeight.put(termID, vector.mapTermIDWeight.get(termID));
		}
		return uv.unitVector();
	}
	
	public static Vector sum(ArrayList<Vector> listVector) {
		Vector sum = new Vector();
		for (Vector vector : listVector) {
			for (int termID : vector.mapTermIDWeight.keySet()) {
				if (sum.mapTermIDWeight.containsKey(termID)) {
					sum.mapTermIDWeight.put(termID, sum.mapTermIDWeight.get(termID)+vector.mapTermIDWeight.get(termID));
				} else {
					sum.mapTermIDWeight.put(termID, vector.mapTermIDWeight.get(termID));
				}
			}
		}
		return sum;
	}
	
	public static Vector average(ArrayList<Vector> listVector) {
		if (listVector.size() == 0) return new Vector();
		else return Vector.sum(listVector).multiply(1.0/(double)listVector.size());
	}

	private HashMap<Integer, Double> mapTermIDWeight;
	private static final long serialVersionUID = 1L;
}
