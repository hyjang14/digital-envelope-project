import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

// 유언장 해독 및 검증하기
public class VerifyEnvelope {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        // 상속자만 열 수 있으므로
        System.out.print("1. 상속자의 개인키 파일명을 입력해주세요: ");
        String heritorPrivateKeyFile = sc.nextLine();

        System.out.print("2. 암호화된 유언장 파일명을 입력해주세요: ");
        String secretWillFile = sc.nextLine();

        System.out.print("3. 암호화된 대칭키 파일명을 입력해주세요: ");
        String encryptedKeyFile = sc.nextLine();

        System.out.print("4. 원본 유언장의 해시값 파일명을 입력해주세요: ");
        String hashFile = sc.nextLine();

        // 전자서명 검증을 위해
        System.out.print("5. 고인의 공개키 파일명을 입력해주세요: ");
        String writerPublicKeyFile = sc.nextLine();

        System.out.print("6. 전자서명 파일명을 입력해주세요: ");
        String signatureFile = sc.nextLine();

        // 1. 상속자의 개인키 불러오기
        byte[] encryptedKey = Files.readAllBytes(Paths.get(encryptedKeyFile));
        byte[] privateKeyBytes = Files.readAllBytes(Paths.get(heritorPrivateKeyFile));
        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        // 상속자의 개인키로 대칭키 복호화하기 -> 이제 유언장 열람 가능해짐
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKeyBytes = rsaCipher.doFinal(encryptedKey);
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        // 2. 암호화된 유언장 복호화하기(대칭키 사용)
        byte[] encryptedData = Files.readAllBytes(Paths.get(secretWillFile));
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] originalData = aesCipher.doFinal(encryptedData);

        // 3. 해시 검증(유언장 변조 여부 확인) -> 현재 해시값 계산하기
        byte[] expectedHash = Base64.getDecoder().decode(Files.readAllBytes(Paths.get(hashFile)));
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] actualHash = digest.digest(originalData);

        System.out.println("\n==========================================");
        
        // 기존 해시값과 해시값 비교
        String hashResult = "";
        
        if (MessageDigest.isEqual(expectedHash, actualHash)) {
        	hashResult = "유언장이 변조되지 않았습니다.";
            System.out.println("- 해시 검증 성공:" + hashResult);
        } else {
        	hashResult = "유언장이 변조되었습니다.";
        	System.out.println("- 해시 검증 실패:" + hashResult);
            return;
        }

        // 4. 고인 공개키로 전자서명 검증(본인 인증-부인 방지)
        // 고인의 공개키 불러오기
        byte[] publicKeyBytes = Files.readAllBytes(Paths.get(writerPublicKeyFile));
        
        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        byte[] signatureBytes = Files.readAllBytes(Paths.get(signatureFile));
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(originalData);

        // 서명 검증 결과 출력
        String signatureResult = "";
        
        if (signature.verify(signatureBytes)) {
        	signatureResult = "유언장이 고인에 의해 작성되었습니다.";
            System.out.println("- 전자서명 검증 성공:" + signatureResult);
        } else {
        	signatureResult = "고인의 서명과 일치하지 않습니다.";
            System.out.println("- 전자서명 검증 실패:" + signatureResult);
            return;
        }

        // 5. 복호화된 유언장 출력
        String willContent = new String(originalData);
        
        System.out.println("==========================================");
        System.out.println("[복호화된 유언장 내용]");
        System.out.println("==========================================");
 
        System.out.println(willContent);
        
        System.out.println("==========================================\n");
        
        // 복호화 결과 HTML로 생성하기
        HtmlGenerator.generateHtml(hashResult, signatureResult, willContent);
        
        sc.close();
    }

}
