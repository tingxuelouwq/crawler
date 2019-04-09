package com.kevin.crawler.service.impl;

import com.kevin.crawler.exception.BizException;
import com.kevin.crawler.service.CrawlerService;
import com.kevin.crawler.task.OneHourCrawlerTask;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kevin.crawler.util.CrawlerUtil.wrapperTitleAndHead;
import static com.kevin.crawler.util.CrawlerUtil.writeToExcel;

/**
 * 类名: CrawlerServiceImpl<br/>
 * 包名：com.kevin.crawler.service.impl<br/>
 * 作者：kevin<br/>
 * 时间：2019/3/31 15:54<br/>
 * 版本：1.0<br/>
 * 描述：
 */
@Service
public class CrawlerServiceImpl implements CrawlerService {
    @Override
    public void weiboCrawl(String keywords,
                           LocalDateTime startDateTime,
                           LocalDateTime endDateTime,
                           String sheetPath) throws BizException, InterruptedException {
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

        ExecutorService pool = Executors.newFixedThreadPool(100);
        List<OneHourCrawlerTask> tasks = new ArrayList<>();
        AtomicInteger rowNum = new AtomicInteger(2);
        LocalDateTime tmpDateTime;
        while (startDateTime.compareTo(endDateTime) < 0) {
            tmpDateTime = startDateTime.plusHours(1);
            if (tmpDateTime.compareTo(endDateTime) > 0) {
                tmpDateTime = endDateTime;
            }
            tasks.add(new OneHourCrawlerTask(keywords, startDateTime, tmpDateTime, sheet, rowNum));
            startDateTime = startDateTime.plusHours(1);
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
}
