package kr.ac.korea.mobide.sigma.wppr.sqlite;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

import kr.ac.korea.mobide.sigma.common.ScoreData;
import kr.ac.korea.mobide.sigma.wppr.WPPR;

/**
 * A subclass of WPPR used to refer wPPR values stored in SQLite wPPR database file.
 * Note that this class is used in server/PC environment only. In order to construct
 * WPPR object in Android platform, use WPPRAndroid class.
 *
 * @author okcomputer
 */
public class WPPRSQLite extends WPPR {

    private String url;

    /**
     * Constructs a WPPR object from a SQLite wPPR database file specified by filePath and
     * fileName.
     *
     * @param filePath A file path that contains a SQLite wPPR database file.
     * @param fileName The file name of a SQLite wPPR database file.
     */
    public WPPRSQLite(String filePath, String fileName) {
        url = filePath + fileName;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void prepareStatement() throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + url);
        this.pstmtScore = this.conn.prepareStatement("select relevance from links where categoryidA = ? and categoryidB = ?");
        this.pstmtList = this.conn.prepareStatement("select categoryidB, relevance from links where categoryidA = ?");
    }

    @Override
    public ArrayList<ScoreData> getSortedListScore(int cidJ) {
        ArrayList<ScoreData> listScore = new ArrayList<ScoreData>();
        ResultSet result = null;
        try {
            prepareStatement();
            this.pstmtList.setInt(1, cidJ);
            result = this.pstmtList.executeQuery();
            while (result.next()) {
                int cidI = result.getInt("categoryidB");
                double relevance = result.getDouble("relevance");
                listScore.add(new ScoreData(cidI, relevance));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close(result);
            close();
        }
        Collections.sort(listScore);
        return listScore;
    }

    @Override
    public double getScore(int cidI, int cidJ) {
        double score = 0.0;
        ResultSet result = null;
        try {
            prepareStatement();
            this.pstmtScore.setInt(1, cidJ);
            this.pstmtScore.setInt(2, cidI);
            result = this.pstmtScore.executeQuery();
            if (result.next()) score = result.getDouble("relevance");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close(result);
            close();
        }
        return score;
    }

    public void close(ResultSet result) {
        try {
            result.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.pstmtList.close();
            this.pstmtScore.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection conn;
    private PreparedStatement pstmtScore;
    private PreparedStatement pstmtList;
}
