import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/* 
Key Management
- 전자서명에 필요한 비대칭키를 생성/저장/복구하는 기능
- 대칭암호화에 필요한 비밀키를 생성/저장/복구하는 기능
*/

public class KeyManagement {

	// 대칭키 생성 -> AES 알고리즘 사용
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }
    
	// 비대칭키 생성 -> RSA 알고리즘 사용
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        return keyGen.generateKeyPair();
    }
    
    // 키 저장
    public static void saveKeyToFile(String filename, byte[] keyBytes) throws IOException {
        Files.write(Paths.get(filename), keyBytes);
    }

    // 비대칭 개인키 복구
    public static PrivateKey loadRSAPrivateKey(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    // 비대칭 공개키 복구
    public static PublicKey loadRSAPublicKey(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    // 대칭키 복구
    public static SecretKey loadAESKey(String filename) throws IOException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        return new SecretKeySpec(keyBytes, "AES");
    }
}
