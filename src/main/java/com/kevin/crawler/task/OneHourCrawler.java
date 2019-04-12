package com.kevin.crawler.task;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @类名: OneHourCrawler<br />
 * @包名：com.kevin.crawler<br/>
 * @作者：kevin<br/>
 * @时间：2019/3/3 10:54<br/>
 * @版本：1.0<br/>
 * @描述：一小时爬虫<br/>
 */
@Slf4j
public class OneHourCrawler {

    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36";
    private String keywords;
    private LocalDateTime start;
    private LocalDateTime end;

    public OneHourCrawler(String keywords, LocalDateTime start, LocalDateTime end) {
        this.keywords = keywords;
        this.start = start;
        this.end = end;
    }
}
