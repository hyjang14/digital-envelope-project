import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HtmlGenerator {

    public static void generateHtml(String hashResult, String signatureResult, String content) throws IOException {
        String fileName = "./static/html/will.html";
        FileWriter writer = new FileWriter(fileName);

        writer.write("<!DOCTYPE html>\n");
        writer.write("<html lang='ko'>\n");
        writer.write("<head>\n");
        writer.write("  <meta charset='UTF-8'>\n");
        writer.write("  <title>유언장 검증 결과</title>\n");
        writer.write("  <link rel='stylesheet' href='../css/will.css'>\n");
        writer.write("</head>\n");
        writer.write("<body>\n");
        writer.write("  <div class='phone-frame'>\n");

        writer.write("    <div style='display: flex; align-items: center;'>\n");
        writer.write("      <h1 style=\"margin: 0; width: 100%; padding: 20px; background: #333; color: white; box-sizing: border-box; text-align: center;\">\r\n"
        		+ "  유언장 검증 결과\r\n"
        		+ "</h1>\n");
        writer.write("    </div>\n");

        // 유언장 내용 출력
        writer.write("    <div class='section'>\n");
        writer.write("      <h2>유언장 내용</h2>\n");
        writer.write("      <div class='content-box'>" + content.replaceAll("\n", "<br>") + "</div>\n");
        writer.write("    </div>\n");

        // 유언장 검증 결과 출력
        writer.write("    <div class='section'>\n");
        writer.write("      <h2>유언장 위변조 검증 결과</h2>\n");
        writer.write("      <p>" + hashResult + "</p>\n");
        writer.write("    </div>\n");

        writer.write("    <div class='section'>\n");
        writer.write("      <h2>유언장 본인확인 검증 결과</h2>\n");
        writer.write("      <p>" + signatureResult + "</p>\n");
        writer.write("    </div>\n");

        writer.write("    <button onclick='history.back()' style=\"font-size: 18px;\">돌아가기</button>\n");
        writer.write("  </div>\n");
        writer.write("</body>\n");
        writer.write("</html>");
        writer.close();

        System.out.println("will.html(유언장 검증 페이지) 파일이 생성되었습니다.");

        // 자동 홈화면 열기
        File htmlFile = new File("static/html/envelope.html");
        Desktop.getDesktop().browse(htmlFile.toURI());
      
    }
}
