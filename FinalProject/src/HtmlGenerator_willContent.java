import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// willContent.html 생성
public class HtmlGenerator_willContent {

    public static void generateHtml(String content, String signatureResult) throws IOException {
        String fileName = "./static/html/willContent.html";
        FileWriter writer = new FileWriter(fileName);

        writer.write("<!DOCTYPE html>\n");
        writer.write("<html lang='ko'>\n");
        writer.write("<head>\n");
        writer.write("  <meta charset='UTF-8'>\n");
        writer.write("  <title>유언장 검증 결과</title>\n");
        writer.write("  <link rel='stylesheet' href='../css/will.css'>\n");
        writer.write("  <link rel='stylesheet' href='../css/main.css'>\n");
        writer.write("  <link rel='stylesheet' href='../css/content.css'>\n");
        writer.write("</head>\n");

        writer.write("<body>\n");
        writer.write("  <div class='phone-frame'>\n");

        writer.write("    <h1 class=\"title-with-icon\">\n");
        writer.write("      <img src=\"../images/open_letter.svg\" alt=\"유언장 이미지\" class=\"title-icon\">\n");
        writer.write("      <span>유언장 검증 결과</span>\n");
        writer.write("    </h1>\n");

        // 유언장 내용 출력
        writer.write("    <div class='section'>\n");
        writer.write("      <h2>유언장 내용</h2>\n");
        writer.write("      <br>\n");
        writer.write("      <div class='content-box2'>\n");
        String convertedContent = content.replace("\n", "<br>\n");
        writer.write(convertedContent + "\n");
        writer.write("      </div>\n");

        // 검증 결과 출력
        writer.write("    <div class='section2'><br>\n");
        writer.write("      <h2>유언장 검증 결과</h2>\n");
        writer.write("      <br>\n");
        String convertedResult = signatureResult.replace("\n", "<br>\n");
        writer.write("      <span>" + convertedResult + "</span>\n");
        writer.write("    </div>\n");
        writer.write("    <div class=\"button-container2\">\n");
        writer.write("      <button onclick=\"openModal()\">재검증 후 종료하기</button>\n");
        writer.write("    </div>\n");
        writer.write("  </div>\n");
        writer.write("  <script>\n");
        writer.write("    function openModal() {\n");
        writer.write("      document.getElementById(\"successModal\").style.display = \"block\";\n");
        writer.write("    }\n");
        writer.write("  </script>\n");
        writer.write("</body>\n");
        writer.write("</html>\n");

        writer.close();

        System.out.println("willContent.html 파일 생성 완료");

        File htmlFile = new File("static/html/willContent.html");
        Desktop.getDesktop().browse(htmlFile.toURI());
    }
}
