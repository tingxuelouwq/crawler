package com.kevin.crawler.service;

import com.kevin.crawler.exception.BizException;

import java.time.LocalDateTime;

/**
 * 类名: CrawlerService<br/>
 * 包名：com.kevin.crawler.service<br/>
 * 作者：kevin<br/>
 * 时间：2019/3/31 15:53<br/>
 * 版本：1.0<br/>
 * 描述：
 */
public interface CrawlerService {

    void weiboCrawl(String keywords,
                    LocalDateTime startDateTime,
                    LocalDateTime endDateTime,
                    String sheetPath) throws BizException, InterruptedException;
}
