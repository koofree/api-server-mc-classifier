package kr.ac.korea.mobide.sigma.wppr.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
 *
 */
public class WPPRSQLite extends WPPR {
	/**
	 * Constructs a WPPR object from a SQLite wPPR database file specified by filePath and
	 * fileName.
	 * @param filePath A file path that contains a SQLite wPPR database file.
	 * @param fileName The file name of a SQLite wPPR database file.
	 */
	public WPPRSQLite(String filePath, String fileName) {
		try {
			Class.forName("org.sqlite.JDBC");
			this.conn = DriverManager.getConnection("jdbc:sqlite:"+filePath+fileName);
			this.pstmtScore = this.conn.prepareStatement("select relevance from links where categoryidA = ? and categoryidB = ?");
			this.pstmtList = this.conn.prepareStatement("select categoryidB, relevance from links where categoryidA = ?");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ArrayList<ScoreData> getSortedListScore(int cidJ) {
		ArrayList<ScoreData> listScore = new ArrayList<ScoreData>();
		try {
			this.pstmtList.setInt(1, cidJ);
			ResultSet result = this.pstmtList.executeQuery();
			while (result.next()) {
				int cidI = result.getInt("categoryidB");
				double relevance = result.getDouble("relevance");
				listScore.add(new ScoreData(cidI, relevance));
			}
			result.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Collections.sort(listScore);
		return listScore;
	}

	@Override
	public double getScore(int cidI, int cidJ) {
		double score = 0.0;
		try {
			this.pstmtScore.setInt(1, cidJ);
			this.pstmtScore.setInt(2, cidI);
			ResultSet result = this.pstmtScore.executeQuery();
			if (result.next()) score = result.getDouble("relevance");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return score;
	}
	
	private Connection conn;
	private PreparedStatement pstmtScore;
	private PreparedStatement pstmtList;
}
