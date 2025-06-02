import java.awt.Desktop;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

// 유언장 해독 및 검증하기 (복호화)
public class VerifyEnvelope {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        // 상속자만 열 수 있으므로 -> 본인인증 과정
        System.out.print("1. 상속자의 개인키 파일명을 입력해주세요: ");
        String heritorPrivateKeyFile = sc.nextLine();

        System.out.print("2. 암호화된 유언장 파일명을 입력해주세요: ");
        String secretWillFile = sc.nextLine();

        System.out.print("3. 암호화된 대칭키 파일명을 입력해주세요: ");
        String encryptedKeyFile = sc.nextLine();

        // 전자서명 검증을 위해
        System.out.print("4. 작성자의 공개키 파일명을 입력해주세요: ");
        String writerPublicKeyFile = sc.nextLine();

        System.out.print("5. 작성자의 전자서명 파일명을 입력해주세요: ");
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

        // 3. 작성자의 공개키로 전자서명 검증(본인 인증-부인 방지)
        // 작성자의 공개키 불러오기
        byte[] publicKeyBytes = Files.readAllBytes(Paths.get(writerPublicKeyFile));
        
        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        // 전자서명 파일 읽기
        byte[] signatureBytes = Files.readAllBytes(Paths.get(signatureFile));
        
        // 전자서명 검증
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(originalData);

        // 서명 검증 결과 출력
        String signatureResult = "";

        boolean isVerified = false;
        try {
            isVerified = signature.verify(signatureBytes);
        } catch (Exception e) {
            isVerified = false;
            throw new RuntimeException("전자서명 검증 중 오류 발생", e);
        }

        if (isVerified) {
            signatureResult = "상속자 본인 확인이 완료되었습니다.\r\n"
                    + "유언장 검증이 성공적으로 완료되었습니다.\r\n"
                    + "유언장이 위조 및 변조되지 않았습니다.";
            
            System.out.println("\n- 전자서명 검증 성공:" + signatureResult + "\n");

            // 4. 복호화된 유언장 콘솔에 출력
            String willContent = new String(originalData);

            System.out.println("==========================================");
            System.out.println("[복호화된 유언장 내용]");
            System.out.println("==========================================");

            System.out.println(willContent);

            System.out.println("==========================================\n");

            // 복호화 결과를 html로 생성
            HtmlGenerator_willContent.generateHtml(willContent, signatureResult);
            
        } else {
            signatureResult = "디지털 유언장 검증 실패";
            System.out.println("\n- 전자서명 검증 실패: " + signatureResult + "\n");

            File htmlFile = new File("static/html/verificationFalse.html");

            if (htmlFile.exists()) {
                Desktop.getDesktop().browse(htmlFile.toURI());
            } else {
                System.out.println("파일이 존재하지 않음.");
            }
        }
        
        sc.close();
    }

}