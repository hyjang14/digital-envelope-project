import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

// 전자봉투 생성하기 (암호화)
public class CreateEnvelope {

	public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("1. 원본 유언장의 파일명을 입력해주세요 : ");
        String plainWillFile = sc.nextLine();

        System.out.print("2. 암호화된 유언장을 저장할 파일명을 입력해주세요 : ");
        String secretWillFile = sc.nextLine();

        System.out.print("3. 상속자의 공개키 파일명을 입력해주세요 : ");
        String heritorPublicKeyFile = sc.nextLine();

        // 유언장은 대칭키로 암호화되고, 이 대칭키는 상속자의 공개키로 다시 암호화된다.
        System.out.print("4. 암호화된 대칭키를 저장할 파일명을 입력해주세요 : ");
        String keyFile = sc.nextLine();

        System.out.print("5. 유언장 작성자의 개인키 파일명을 입력해주세요 : ");
        String writerPrivateKeyFile = sc.nextLine();

        // 해시값을 작성자의 개인키로 암호화해서 전자서명 생성
        System.out.print("6. 전자서명을 저장할 파일명을 입력해주세요 : ");
        String signatureFile = sc.nextLine();

        
        // 1. 원본 유언장 데이터 읽기
        byte[] data = Files.readAllBytes(Paths.get(plainWillFile));

        // 2. 작성자의 대칭키 생성
        SecretKey secretKey = KeyManagement.generateAESKey();

        // 3. 생성된 대칭키로 원본 유언장 암호화하기 -> 암호화된 유언장 생성
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = aesCipher.doFinal(data);
        Files.write(Paths.get(secretWillFile), encryptedData);

        // 4. 상속자의 공개키 불러오기
        PublicKey publicKey = KeyManagement.loadRSAPublicKey(heritorPublicKeyFile);

        // 5. 작성자의 대칭키를 상속자의 공개키로 암호화하기
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = rsaCipher.doFinal(secretKey.getEncoded());
        Files.write(Paths.get(keyFile), encryptedKey);

        // 6. 유언장 작성자의 개인키 불러오기
        PrivateKey privateKey = KeyManagement.loadRSAPrivateKey(writerPrivateKeyFile);

        // 7. 전자서명 생성
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        byte[] signed = signature.sign();
        Files.write(Paths.get(signatureFile), signed);

        // 전자봉투 출력
        System.out.println("\n==========================================");
        System.out.println("전자봉투가 성공적으로 생성되었습니다.");
        System.out.println("==========================================");

        System.out.println("[전자봉투 구성 요소]");
        System.out.println("(1) 암호화된 유언장 파일 이름: " + secretWillFile);
        System.out.println("(2) 암호화된 대칭키 파일 이름: " + keyFile);
        System.out.println("(3) 전자서명 파일 이름: " + signatureFile);

        System.out.println("==========================================\n");

        sc.close();
    }
}
