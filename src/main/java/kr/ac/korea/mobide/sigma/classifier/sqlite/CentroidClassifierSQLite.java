package kr.ac.korea.mobide.sigma.classifier.sqlite;

import java.sql.*;
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

    private final String url;

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
        url = filePath;
        Statement stmt = null;
        ResultSet result = null;
        try {
            Class.forName("org.sqlite.JDBC");
            this.conn = DriverManager.getConnection("jdbc:sqlite:" + url);
            stmt = this.conn.createStatement();
            result = stmt.executeQuery("select df from vocabulary where termid = " + VectorSpaceModel.TERMID_NUM_TRAINING_DOC);
            if (!result.next()) {
                System.out.println(VectorSpaceModel.TERM_NUM_TRAINING_DOC + " is not in the vocabulary");
            }
            this.numDoc = result.getInt("df");

            this.mapTermIDDF = new HashMap<Integer, Integer>();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close(result);
            close(stmt);
        }
    }

    public void close(ResultSet result) {
        try {
            result.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close(Statement statement) {
        try {
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close(Connection conn) {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCategoryName(int cid) {
        String cname = "";
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            statement = this.conn.prepareStatement("select name from category where categoryID = ?");
            statement.setInt(1, cid);
            result = statement.executeQuery();
            if (result.next()) cname = result.getString("name");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close(result);
            close(statement);
        }
        return cname;
    }

    @Override
    public int getCategoryID(String cname) {
        int cid = -1;
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            statement = this.conn.prepareStatement("select categoryID from category where name like ?");
            statement.setString(1, cname);
            result = statement.executeQuery();
            if (result.next()) cid = result.getInt("categoryID");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close(result);
            close(statement);
        }
        return cid;
    }

    @Override
    public int getTermID(String term) {
        int termID = -1;
        if (term == null) return termID;
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            statement = this.conn.prepareStatement("select termid, df from vocabulary where term like ?");
            statement.setString(1, term);
            result = statement.executeQuery();
            if (result.next()) {
                termID = result.getInt("termid");
                this.mapTermIDDF.put(termID, result.getInt("df"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close(result);
            close(statement);
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
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            statement = this.conn.prepareStatement("select CategoryID, TFIDF from postings where TermID = ?");
            statement.setInt(1, termID);
            result = statement.executeQuery();
            while (result.next()) {
                int cid = result.getInt("CategoryID");
                double weight = result.getDouble("TFIDF");
                posting.put(cid, weight);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close(result);
            close(statement);
        }
        return posting;
    }

    @Override
    public double getScalar(int id) {
        double scalar = 0.0;
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            statement = this.conn.prepareStatement("select scalar from category where categoryID = ?");
            statement.setInt(1, id);
            result = statement.executeQuery();
            if (result.next()) {
                scalar = result.getDouble("scalar");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close(result);
            close(statement);
        }
        return scalar;
    }

    private int numDoc;
    private HashMap<Integer, Integer> mapTermIDDF;
    private Connection conn;
}
