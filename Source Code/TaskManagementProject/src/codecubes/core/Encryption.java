package codecubes.core;

import java.security.MessageDigest;

/**
 * Created by msaeed on 1/25/2017.
 */
public class Encryption {

    private static Encryption instance;

    private Encryption() {

    }

    public static Encryption getInstance() {
        if (instance == null) {
            instance = new Encryption();
        }
        return instance;
    }

    public String sha1(String string) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        byte[] stringBytes = string.getBytes("UTF-8");
        byte[] stringHashBytes = messageDigest.digest(stringBytes);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < stringHashBytes.length; i++) {
            stringBuffer.append(Integer.toString((stringHashBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuffer.toString();
    }

}
