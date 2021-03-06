package com.kevin.crawler.util;

import com.kevin.common.util.DateTimeUtil;
import com.kevin.crawler.dto.BlogDto;
import com.kevin.crawler.dto.ForwardedBlogDto;
import com.kevin.crawler.exception.BizException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.CollectionUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kevin.crawler.constant.BizStatusCode.*;
import static com.kevin.crawler.constant.BizStatusCode.OUTPUTSTREAM_CLOSED_ERROR;

/**
 * 类名: CrawlerUtil<br/>
 * 包名：com.kevin.crawler.util<br/>
 * 作者：kevin<br/>
 * 时间：2019/3/31 15:57<br/>
 * 版本：1.0<br/>
 * 描述：
 */
public class CrawlerUtil {

    /**
     * 解析日期时间
     * @param dateTime
     * @return
     * @throws BizException
     */
    public static LocalDateTime parseDateTime(String dateTime) throws BizException {
        String[] dateTimePart = dateTime.split("-");
        try {
            return LocalDateTime.of(Integer.parseInt(dateTimePart[0]),
                    Integer.parseInt(dateTimePart[1]), Integer.parseInt(dateTimePart[2]),
                    Integer.parseInt(dateTimePart[3]), 0);
        } catch (Exception e) {
            throw new BizException(DATE_FORMAT_ERROR, bizStatusCodeMap.get(DATE_FORMAT_ERROR));
        }
    }

    /**
     * 自动生成文件路径
     * @param keywords
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static String generateSheetPath( String keywords,
                                            String startDateTime,
                                            String endDateTime) {
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
     * 填充标题和表头
     * @param workbook
     * @param sheet
     * @param title
     * @param heads
     */
    public static void wrapperTitleAndHead(Workbook workbook, Sheet sheet,
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

    /**
     * 填充表格
     * @param sheet
     * @param rowNum
     * @param blogs
     */
    public static void wrapperBody(Sheet sheet, AtomicInteger rowNum, List<BlogDto> blogs) {
        if (CollectionUtils.isEmpty(blogs)) {
            return;
        }
        for (BlogDto blog : blogs) {
            System.out.println(rowNum);
            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.createCell(0).setCellValue(blog.getBloggerInfo().getId());
            row.createCell(1).setCellValue(blog.getBloggerInfo().getNickName());
            row.createCell(2).setCellValue(blog.getBloggerInfo().getHomePage());
            row.createCell(3).setCellValue(blog.getBloggerInfo().getAuthInfo());
            row.createCell(4).setCellValue(blog.getBlogText().getImgText());
            row.createCell(5).setCellValue(blog.getBlogPublishInfo().getBlogLink());
            row.createCell(6).setCellValue(DateTimeUtil.formatDateTime(blog.getBlogPublishInfo().getPublishDateTime()));
            row.createCell(7).setCellValue(blog.getActInfo().getForwardNum());
            row.createCell(8).setCellValue(blog.getActInfo().getCommentNum());
            row.createCell(9).setCellValue(blog.getActInfo().getLikeNum());

            ForwardedBlogDto forwardedBlog = blog.getForwardedBlog();
            if (forwardedBlog != null) {
                row.createCell(10).setCellValue(forwardedBlog.getBloggerInfo().getId());
                row.createCell(11).setCellValue(forwardedBlog.getBloggerInfo().getNickName());
                row.createCell(12).setCellValue(forwardedBlog.getBloggerInfo().getHomePage());
                row.createCell(13).setCellValue(forwardedBlog.getBloggerInfo().getAuthInfo());
                row.createCell(14).setCellValue(forwardedBlog.getBlogText().getImgText());
                row.createCell(15).setCellValue(forwardedBlog.getBlogPublishInfo().getBlogLink());
                row.createCell(16).setCellValue(DateTimeUtil.formatDateTime(forwardedBlog.getBlogPublishInfo().getPublishDateTime()));
                row.createCell(17).setCellValue(forwardedBlog.getActInfo().getForwardNum());
                row.createCell(18).setCellValue(forwardedBlog.getActInfo().getCommentNum());
                row.createCell(19).setCellValue(forwardedBlog.getActInfo().getLikeNum());
            }
        }
    }

    /**
     * 将excel写到磁盘
     * @param workbook
     * @param sheetPath
     */
    public static void writeToExcel(Workbook workbook, String sheetPath) throws BizException {
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
            throw new BizException(e, SAVE_TO_DISK_FAILED, bizStatusCodeMap.get(SAVE_TO_DISK_FAILED));
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
                throw new BizException(e, OUTPUTSTREAM_CLOSED_ERROR, bizStatusCodeMap.get(OUTPUTSTREAM_CLOSED_ERROR));
            }
        }
    }
}
