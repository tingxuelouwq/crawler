package com.kevin.crawler;

import com.kevin.crawler.exception.BizException;
import com.kevin.crawler.task.OneHourCrawlerTask;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kevin.crawler.constant.BizStatusCode.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testCrawlerForWeibo() throws InterruptedException, BizException {
        final String keywords = "泉港 碳九";
        final String startDateTime = "2018-11-04-0";
        final String endDateTime = "2018-12-01-0";
        String sheetPath = "";

        ExecutorService pool = Executors.newFixedThreadPool(100);
        List<OneHourCrawlerTask> tasks = new ArrayList<>();
        LocalDateTime start = parseDateTime(startDateTime);
        LocalDateTime end = parseDateTime(endDateTime);
        LocalDateTime tmp;
        if (StringUtils.isEmpty(sheetPath)) {
            sheetPath = generateSheetPath(startDateTime, endDateTime, keywords);
        }
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet();
        final String title = startDateTime + "~" + endDateTime + " [" + keywords + "]";
        final String[] heads = {"博主id", "博主名称", "博主主页", "博主认证信息", "微博正文",
                "博文独立网址", "发布时间", "转发数", "评论数", "点赞数",
                "博主id:被转发微博", "博主名称:被转发微博", "博主主页:被转发微博",
                "博主认证信息:被转发微博", "微博正文:被转发微博", "博文独立网址:被转发微博",
                "发布时间:被转发微博", "转发数:被转发微博", "评论数:被转发微博",
                "点赞数:被转发微博"};
        wrapperTitleAndHead(workbook, sheet, title, heads);

        AtomicInteger rowNum = new AtomicInteger(2);
        while (start.compareTo(end) < 0) {
            tmp = start.plusHours(1);
            if (tmp.compareTo(end) > 0) {
                tmp = end;
            }
            tasks.add(new OneHourCrawlerTask(keywords, start, tmp, sheet, rowNum));
            start = start.plusHours(1);
        }

        CountDownLatch latch = new CountDownLatch(tasks.size());
        for (OneHourCrawlerTask task : tasks) {
            task.setLatch(latch);
            pool.submit(task);
        }
        latch.await();
        pool.shutdown();

        writeToExcel(workbook, sheetPath);
    }

    /**
     * 将excel写到磁盘
     * @param workbook
     * @param sheetPath
     */
    private void writeToExcel(Workbook workbook, String sheetPath) throws BizException {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            File file = new File(sheetPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            workbook.write(bos);
        } catch (IOException e) {
            throw new BizException(e, SAVE_TO_DISK_FAILED, codeMsgMap.get(SAVE_TO_DISK_FAILED));
        } finally {
            try {
                workbook.close();
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                throw new BizException(e, OUTPUTSTREAM_CLOSED_ERROR, codeMsgMap.get(OUTPUTSTREAM_CLOSED_ERROR));
            }
        }
    }

    /**
     * 自动生成文件路径
     * @param startDateTime
     * @param endDateTime
     * @param keywords
     * @return
     */
    private String generateSheetPath(String startDateTime, String endDateTime, String keywords) {
        StringBuilder builder = new StringBuilder().append(File.separator);
        builder.append(startDateTime.replace("-", ""))
                .append("-")
                .append(endDateTime.replace("-", ""))
                .append("[")
                .append(keywords)
                .append("].xlsx");
        return builder.toString();
    }

    /**
     * 解析日期时间
     * @param dateTime
     * @return
     * @throws BizException
     */
    private LocalDateTime parseDateTime(String dateTime) throws BizException {
        String[] dateTimePart = dateTime.split("-");
        try {
            return LocalDateTime.of(Integer.parseInt(dateTimePart[0]),
                    Integer.parseInt(dateTimePart[1]), Integer.parseInt(dateTimePart[2]),
                    Integer.parseInt(dateTimePart[3]), 0);
        } catch (Exception e) {
            throw new BizException(DATE_FORMAT_ERROR, codeMsgMap.get(DATE_FORMAT_ERROR));
        }
    }

    /**
     * 填充标题和表头
     * @param workbook
     * @param sheet
     * @param title
     * @param heads
     */
    private void wrapperTitleAndHead(Workbook workbook, Sheet sheet,
                                       String title, String[] heads) {
        // 合并标题单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0,
                0, heads.length - 1));
        // 设置标题样式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        // 填充标题
        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) (15.625 * 40));
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(titleStyle);
        titleCell.setCellValue(title);

        // 设置表头样式
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headFont = workbook.createFont();
        headFont.setBold(true);
        headFont.setFontHeightInPoints((short) 10);
        headStyle.setFont(headFont);
        // 填充表头
        Row headRow = sheet.createRow(1);
        for (int i = 0; i < heads.length; i++) {
            Cell headCell = headRow.createCell(i);
            headCell.setCellStyle(headStyle);
            headCell.setCellValue(heads[i]);
        }
    }
}
