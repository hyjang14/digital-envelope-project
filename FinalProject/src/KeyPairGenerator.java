import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class KeyPairGenerator {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		Scanner sc = new Scanner(System.in);

        // 소유자 입력받기
        System.out.print("키를 생성할 소유자를 입력해주세요 (예: 고인, 상속자) : ");
        String owner = sc.nextLine().trim();

        // RSA 키쌍 생성하기
        KeyPair keyPair = KeyManagement.generateRSAKeyPair();

        // 파일명 설정하기
        String privateKeyFile = owner + "_private.key";
        String publicKeyFile = owner + "_public.key";

        // 키를 파일에 저장하기
        KeyManagement.saveKeyToFile(privateKeyFile, keyPair.getPrivate().getEncoded());
        KeyManagement.saveKeyToFile(publicKeyFile, keyPair.getPublic().getEncoded());

        // 결과 콘솔에 출력하기
        System.out.println("\n==========================================");
        System.out.println(owner + "의 RSA 공개키/개인키 생성이 완료되었습니다.");
        System.out.println("- 개인키 파일: " + privateKeyFile);
        System.out.println("- 공개키 파일: " + publicKeyFile);
        System.out.println("==========================================\n");

        sc.close();
	}

}
