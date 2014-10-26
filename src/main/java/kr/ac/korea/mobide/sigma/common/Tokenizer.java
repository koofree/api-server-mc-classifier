package kr.ac.korea.mobide.sigma.common;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class Tokenizer {
    public static ArrayList<String> getListToken(String doc) {
        if (doc == null) doc = "";
        ArrayList<String> listToken = new ArrayList<String>();
        try {
            TokenStream stream = Tokenizer.Analyzer.tokenStream("contents", new StringReader(doc));
            stream.reset();
            while (stream.incrementToken()) {
                String termText = stream.getAttribute(CharTermAttribute.class).toString();
                listToken.add(termText);
            }
            stream.end();
            stream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return listToken;
    }

    private static Analyzer Analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
}
