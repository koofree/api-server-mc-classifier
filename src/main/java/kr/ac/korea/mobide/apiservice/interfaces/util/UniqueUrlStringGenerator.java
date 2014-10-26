package kr.ac.korea.mobide.apiservice.interfaces.util;

import org.springframework.util.Assert;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Created by Koo Lee on 2014-09-02.
 */
public abstract class UniqueUrlStringGenerator {
    private static final String range1 = "02468acegikmoqsuwyACEGIKMOQSUWY-";
    private static final String range2 = "13579bdfhjlnprtvxzBDFHJLNPRTVXZ_";
    private static final int B = range2.length();

    public static String generate() {
        long num = UUID.randomUUID().getLeastSignificantBits();
        return encode(num);
    }

    public static String encode(long num) {
        StringBuilder sb = new StringBuilder();
        while (num != 0) {
            int rest = (int) (num % B);
            if (rest < 0)
                sb.append(range2.charAt(Math.abs(rest)));
            else
                sb.append(range1.charAt(rest));

            num /= B;
        }
        return sb.reverse().toString();
    }

    public static Long decode(String random) {
        long num = 0;
        for (char ch : random.toCharArray()) {
            num *= B;
            int n = range2.indexOf(ch);
            if (n > -1)
                num += -n;
            else
                num += range1.indexOf(ch);
        }
        return num;
    }
}
