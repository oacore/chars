package uk.ac.core.oadiscover.services;

import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class OADiscoveryHashingService {
    public static final String SECRET = "SECRETKEYCORE345";

    public String getKey(String fullTextLink) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(URLEncoder.encode(fullTextLink + SECRET, "UTF-8").getBytes());
        byte[] digest = md.digest();
        String hash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        return hash;
    }

    public boolean isValid(String url, String key) {
        try {
            System.out.println(key);
            System.out.println(this.getKey(url) );
            return this.getKey(url).equalsIgnoreCase(key.trim());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return false;
        }
    }
}
