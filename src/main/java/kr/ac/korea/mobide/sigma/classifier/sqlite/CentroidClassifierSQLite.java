package kr.ac.korea.mobide.sigma.classifier.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import kr.ac.korea.mobide.sigma.classifier.CentroidClassifier;
import kr.ac.korea.mobide.sigma.vsm.VectorSpaceModel;

/**
 * A subclass of CentroidClassifier that classifies arbitrary query text by using the
 * SQLite classifier database file. Note that this class is used in
 * server/PC environment only. In order to construct CentroidClassifier object
 * in Android platform, use CentroidClassifierAndroid class.
 *
 * @author okcomputer
 */
public class CentroidClassifierSQLite extends CentroidClassifier {

    /**
     * Constructs a CentroidClassifier object from the SQLite classifier database file
     * specified by filePath and fileName.
     *
     * @param filePath A file path that contains a SQLite classifier database file.
     * @param fileName The file name of a SQLite classifier database file.
     */
    public CentroidClassifierSQLite(String filePath, String fileName) {
        this(filePath + fileName);
    }

    public CentroidClassifierSQLite(String filePath) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.conn = DriverManager.getConnection("jdbc:sqlite:" + filePath);
            Statement stmt = this.conn.createStatement();
            ResultSet result = stmt.executeQuery("select df from vocabulary where termid = " + VectorSpaceModel.TERMID_NUM_TRAINING_DOC);
            if (!result.next()) {
                System.out.println(VectorSpaceModel.TERM_NUM_TRAINING_DOC + " is not in the vocabulary");
            }
            this.numDoc = result.getInt("df");
            result.close();
            this.mapTermIDDF = new HashMap<Integer, Integer>();
            this.pstmtVocabulary = this.conn.prepareStatement("select termid, df from vocabulary where term like ?");
            this.pstmtPostings = this.conn.prepareStatement("select CategoryID, TFIDF from postings where TermID = ?");
            this.pstmtScalar = this.conn.prepareStatement("select scalar from category where categoryID = ?");
            this.pstmtCName = this.conn.prepareStatement("select name from category where categoryID = ?");
            this.pstmtCID = this.conn.prepareStatement("select categoryID from category where name like ?");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getCategoryName(int cid) {
        String cname = "";
        try {
            this.pstmtCName.setInt(1, cid);
            ResultSet result = this.pstmtCName.executeQuery();
            if (result.next()) cname = result.getString("name");
            result.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cname;
    }

    @Override
    public int getCategoryID(String cname) {
        int cid = -1;
        try {
            this.pstmtCID.setString(1, cname);
            ResultSet result = this.pstmtCID.executeQuery();
            if (result.next()) cid = result.getInt("categoryID");
            result.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cid;
    }

    @Override
    public int getTermID(String term) {
        int termID = -1;
        if (term == null) return termID;
        try {
            this.pstmtVocabulary.setString(1, term);
            ResultSet result = this.pstmtVocabulary.executeQuery();
            if (result.next()) {
                termID = result.getInt("termid");
                this.mapTermIDDF.put(termID, result.getInt("df"));
            }
            result.close();
        } catch (Exception ex) {
            ex.printStackTrace();
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

    @Override
    public HashMap<Integer, Double> getPosting(int termID) {
        HashMap<Integer, Double> posting = new HashMap<Integer, Double>();
        try {
            this.pstmtPostings.setInt(1, termID);
            ResultSet result = this.pstmtPostings.executeQuery();
            while (result.next()) {
                int cid = result.getInt("CategoryID");
                double weight = result.getDouble("TFIDF");
                posting.put(cid, weight);
            }
            result.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return posting;
    }

    @Override
    public double getScalar(int id) {
        double scalar = 0.0;
        try {
            this.pstmtScalar.setInt(1, id);
            ResultSet result = this.pstmtScalar.executeQuery();
            if (result.next()) {
                scalar = result.getDouble("scalar");
            }
            result.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return scalar;
    }

    private int numDoc;
    private HashMap<Integer, Integer> mapTermIDDF;
    private Connection conn;
    private PreparedStatement pstmtVocabulary;
    private PreparedStatement pstmtPostings;
    private PreparedStatement pstmtScalar;
    private PreparedStatement pstmtCName;
    private PreparedStatement pstmtCID;
}
