import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.Desktop;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class WillHashVerifier {
 
    public static String sha256(String data) throws Exception {
    	MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data.getBytes());

        String str = "";
        
        for (byte b : hashBytes) {
        	str += String.format("%02x", b);
        }

        return str;
    }

   
    public static String normalize(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }

    public static void main(String[] args) throws Exception {
        // 1. 원본 유언장(유언장.txt) 파일 읽기
        String originalWill = Files.readString(Paths.get("유언장.txt"), StandardCharsets.UTF_8);

        // 2. 생성된 willContent.html 파일 읽어서 열람 후의 유언장 내용의 해시값 추출
        String html = Files.readString(Paths.get("./static/html/willContent.html"), StandardCharsets.UTF_8);
        Document doc = Jsoup.parse(html);

        Element contentBox = doc.selectFirst("div.content-box2");
        if (contentBox == null) {
            System.out.println("유언장 내용을 찾을 수 없습니다.");
            
            return;
        }
        String viewedWill = contentBox.text();

        // 3. 해시값 변환
        String originalHash = sha256(normalize(originalWill));
        String viewedHash = sha256(normalize(viewedWill));

        System.out.println("원본 유언장 해시값: " + originalHash);
        System.out.println("열람 유언장 해시값: " + viewedHash);
        
        // 4. 원본 유언장과 해시값 비교하기
        if (originalHash.equals(viewedHash)) {
            System.out.println("\n-유언장 내용이 원본과 일치합니다: 열람 후 위변조 가능성 없음");
            
            File htmlFile = new File("static/html/verificationTrue.html");
            Desktop.getDesktop().browse(htmlFile.toURI());
        } else {
            System.out.println("\n-유언장 내용이 원본과 다릅니다: 열람 후 위변조 가능성 있음");
            
            File htmlFile = new File("static/html/verificationFalse.html");
            Desktop.getDesktop().browse(htmlFile.toURI());
        }
    }
}
