package com.kevin.crawler;

import com.kevin.cookie.fetcher.WeiboCookieFetcher;
import com.kevin.crawler.exception.BizException;
import com.kevin.crawler.service.CrawlerService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kevin.crawler.util.CrawlerUtil.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerApplicationTests {

    @Autowired
    private CrawlerService crawlerService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testCrawlerForWeibo() throws InterruptedException, BizException {
        final String keywords = "泉港 碳九";
        final String startDateTime = "2018-11-04-0";
        final String endDateTime = "2018-12-01-0";
        String sheetPath = "";

        if (StringUtils.isEmpty(sheetPath)) {
            sheetPath = generateSheetPath(keywords, startDateTime, endDateTime);
        }
        LocalDateTime start = parseDateTime(startDateTime);
        LocalDateTime end = parseDateTime(endDateTime);
        crawlerService.weiboCrawl(keywords, start, end, sheetPath);
    }

    @Test
    public void markup() throws IOException, BizException {
        String filepath = "C:\\Users\\kevin\\Desktop\\泉港碳九事件数据待补采样本2019.4.10.xlsx";
        String outputPath = "C:\\Users\\kevin\\Desktop\\[补充完毕]泉港碳九事件数据待补采样本2019.4.10.xlsx";
        String forwardReg = "<span><em class=\\\\\\\"W_ficon ficon_forward S_ficon\\\\\\\">&#xe607;<\\\\/em><em>\\s*(\\d+)\\s*<\\\\/em>";
        String commentReg = "<span><em class=\\\\\\\"W_ficon ficon_repeat S_ficon\\\\\\\">&#xe608;<\\\\/em><em>\\s*(\\d+)\\s*<\\\\/em>";
        String likeReg = "<span node-type=\\\\\\\"like_status\\\\\\\" class=\\\\\\\"\\\\\\\"><em class=\\\\\\\"W_ficon ficon_praised S_txt2\\\\\\\">ñ<\\\\/em><em>\\s*(\\d+)\\s*<\\\\/em>";
        Map<String, String> cookies = WeiboCookieFetcher.getCookies();
        Workbook workbook = new XSSFWorkbook(filepath);
        Sheet sheet = workbook.getSheetAt(0);
        int totalRowNum = sheet.getLastRowNum();
        int currentRowNum = 2;
        for (; currentRowNum <= totalRowNum; currentRowNum++) {
            Row row = sheet.getRow(currentRowNum);
            String blogLink = row.getCell(5).getStringCellValue();
            Cell forwardCell = row.getCell(15);
            Document doc = Jsoup.connect(blogLink).cookies(cookies).get();
            String html = doc.html();

            if (forwardCell == null) {
                String forwardNum = parse(html, forwardReg, 1);
                String commentNum = parse(html, commentReg, 1);
                String likeNum = parse(html, likeReg, 1);
                row.getCell(7).setCellValue(forwardNum);
                row.getCell(8).setCellValue(commentNum);
                row.getCell(9).setCellValue(likeNum);
            } else {
                String forwardForwardNum = parse(html, forwardReg, 1);
                String forwardCommentNum = parse(html, commentReg, 1);
                String forwardLikeNum = parse(html, likeReg, 1);
                row.getCell(17).setCellValue(forwardForwardNum);
                row.getCell(18).setCellValue(forwardCommentNum);
                row.getCell(19).setCellValue(forwardLikeNum);

                String forwardNum = parse(html, forwardReg, 2);
                String commentNum = parse(html, commentReg, 2);
                String likeNum = parse(html, likeReg, 2);
                row.getCell(7).setCellValue(forwardNum);
                row.getCell(8).setCellValue(commentNum);
                row.getCell(9).setCellValue(likeNum);
            }

            System.out.println(currentRowNum);
        }
        writeToExcel(workbook, outputPath);
    }

    private String extractActInfo(Element options, String attrName, String attrValue) {
        Element act = options.getElementsByAttributeValue(attrName, attrValue).first();
        String actNum = act.getElementsByTag("em").last().text().trim();
        try {
            Integer.valueOf(actNum);
        } catch (NumberFormatException e) {
            actNum = "0";
        }
        return actNum;
    }

    private String extractForwardInfo(Element forwardBlog, String className) {
        String actNum = forwardBlog.getElementsByClass(className).first().nextElementSibling().text().trim();
        try {
            Integer.valueOf(actNum);
        } catch (NumberFormatException e) {
            actNum = "0";
        }
        return actNum;
    }

    public String parse(String input, String regex, int count) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        for (int i = 0; i < count - 1; i++) {
            matcher.find();
        }
        matcher.find();
        try {
            return matcher.group(1);
        } catch (IllegalStateException e) {
            return "0";
        }
    }

    @Test
    public void test() {
        String forwardReg = "<span><em class=\\\\\\\"W_ficon ficon_forward S_ficon\\\\\\\">&#xe607;<\\\\/em><em>\\s*(\\d+)\\s*<\\\\/em>";
        String commentReg = "<span><em class=\\\\\\\"W_ficon ficon_repeat S_ficon\\\\\\\">&#xe608;<\\\\/em><em>\\s*(\\d+)\\s*<\\\\/em>";
        String likeReg = "<span node-type=\\\\\\\"like_status\\\\\\\" class=\\\\\\\"\\\\\\\"><em class=\\\\\\\"W_ficon ficon_praised S_txt2\\\\\\\">ñ<\\\\/em><em>\\s*(\\d+)\\s*<\\\\/em>";

        String input = "<span node-type=\\\"like_status\\\" class=\\\"\\\"><em class=\\\"W_ficon ficon_praised S_txt2\\\">ñ<\\/em><em>  1233434  <\\/em>" +
                "<span node-type=\\\"like_status\\\" class=\\\"\\\"><em class=\\\"W_ficon ficon_praised S_txt2\\\">ñ<\\/em><em>  23  <\\/em>";
        System.out.println(parse(input, likeReg, 1));
    }
}
