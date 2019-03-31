package com.kevin.crawler.controller;

import com.kevin.common.ret.RetJson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名: CrawlerController<br/>
 * 包名：com.kevin.crawler.controller<br/>
 * 作者：kevin<br/>
 * 时间：2019/3/31 9:50<br/>
 * 版本：1.0<br/>
 * 描述：
 */
@RestController
@RequestMapping("/crawler")
public class CrawlerController {

    @GetMapping("/weibo")
    public RetJson weiboCrawl() {
        return null;
    }
}
