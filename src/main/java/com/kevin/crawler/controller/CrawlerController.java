package com.kevin.crawler.controller;

import com.kevin.common.ret.RetJson;
import com.kevin.crawler.exception.BizException;
import com.kevin.crawler.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.kevin.crawler.constant.BizStatusCode.SUCCESS;
import static com.kevin.crawler.constant.BizStatusCode.bizStatusCodeMap;
import static com.kevin.crawler.util.CrawlerUtil.generateSheetPath;
import static com.kevin.crawler.util.CrawlerUtil.parseDateTime;

/**
 * 类名: CrawlerController<br/>
 * 包名：com.kevin.crawler.controller<br/>
 * 作者：kevin<br/>
 * 时间：2019/3/31 9:50<br/>
 * 版本：1.0<br/>
 * 描述：
 */
@RestController
@RequestMapping("/api/v1/crawler")
public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;

    @GetMapping("/weibo")
    public RetJson weiboCrawl(@RequestParam("keywords") String keywords,
                              @RequestParam("startDateTime") String startDateTime,
                              @RequestParam("endDateTime") String endDateTime,
                              @RequestParam(value = "sheetPath", required = false) String sheetPath)
            throws BizException, InterruptedException {
        if (StringUtils.isEmpty(sheetPath)) {
            sheetPath = generateSheetPath(keywords, startDateTime, endDateTime);
        }
        LocalDateTime start = parseDateTime(startDateTime);
        LocalDateTime end = parseDateTime(endDateTime);
        crawlerService.weiboCrawl(keywords, start, end, sheetPath);
        return new RetJson(SUCCESS, bizStatusCodeMap.get(SUCCESS));
    }
}
