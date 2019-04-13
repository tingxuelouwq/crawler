package com.kevin.crawler.service.impl;

import com.kevin.common.util.DateTimeUtil;
import com.kevin.cookie.fetcher.WeiboCookieFetcher;
import com.kevin.crawler.dto.*;
import com.kevin.crawler.exception.BizException;
import com.kevin.crawler.service.CrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kevin.common.util.DateTimeUtil.DateTimeFormat.LONG_DATE_TIME_PATTERN_WITH_LINE_TO_HOUR;
import static com.kevin.crawler.constant.BizStatusCode.*;
import static com.kevin.crawler.util.CrawlerUtil.*;

/**
 * 类名: CrawlerServiceImpl<br/>
 * 包名：com.kevin.crawler.service.impl<br/>
 * 作者：kevin<br/>
 * 时间：2019/3/31 15:54<br/>
 * 版本：1.0<br/>
 * 描述：
 */
@Service
@Slf4j
public class CrawlerServiceImpl implements CrawlerService {

    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36";

    @Override
    public void weiboCrawl(String keywords,
                           LocalDateTime startDateTime,
                           LocalDateTime endDateTime,
                           String sheetPath) throws BizException {
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
        LocalDateTime tmpDateTime;
        while (startDateTime.compareTo(endDateTime) < 0) {
            tmpDateTime = startDateTime.plusHours(1);
            if (tmpDateTime.compareTo(endDateTime) > 0) {
                tmpDateTime = endDateTime;
            }
            List<BlogDto> blogs = crawl(keywords, startDateTime, tmpDateTime);
            wrapperBody(sheet, rowNum, blogs);
            startDateTime = startDateTime.plusHours(1);
        }
        writeToExcel(workbook, sheetPath);
    }

    private List<BlogDto> crawl(String keywords, LocalDateTime start, LocalDateTime end)
            throws BizException {
        List<BlogDto> result = new ArrayList<>();
        int pageNum = 1;
        int totalPageNum = Integer.MAX_VALUE;
        while (pageNum <= totalPageNum) {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("https://s.weibo.com/weibo?q=")
                    .append(keywords).append("&typeall=1&suball=1&timescope=custom:")
                    .append(DateTimeUtil.formatDateTime(start, LONG_DATE_TIME_PATTERN_WITH_LINE_TO_HOUR))
                    .append(":")
                    .append(DateTimeUtil.formatDateTime(end, LONG_DATE_TIME_PATTERN_WITH_LINE_TO_HOUR))
                    .append("&Refer=g&page=").append(pageNum);
            String url = urlBuilder.toString();
            Document doc = null;
            try {
                doc = Jsoup.connect(url).userAgent(userAgent).timeout(20000).get();
                Element feedList = doc.getElementById("pl_feedlist_index");
                Elements noResultCard = feedList.getElementsByClass("card-no-result");
                if (noResultCard != null) { // // 判断是否有结果
                    break;
                }
                if (totalPageNum == Integer.MAX_VALUE) {    // 第一次访问获取总页数
                    Element pageCard = feedList.getElementsByClass("s-scroll").first();
                    if (pageCard == null) {
                        totalPageNum = Integer.MIN_VALUE;
                    } else {
                        String pageInfo = pageCard.getElementsByTag("li").last()
                                .getElementsByTag("a").text();
                        totalPageNum = Integer.parseInt(pageInfo.substring(pageInfo.indexOf("第") + 1, pageInfo.indexOf("页")));
                    }
                }
                Elements cards = feedList.getElementsByAttribute("mid");
                for (Element card : cards) {
                    BlogDto blog = extractBlog(card);
                    result.add(blog);
                }
                pageNum++;
                TimeUnit.SECONDS.sleep((long) (Math.random() * 30));
            } catch (Exception e) {
                log.error("url: " + url + ", doc: " + doc.html(), e);
                throw new BizException(CRAWL_TASK_FAILUER, bizStatusCodeMap.get(CRAWL_TASK_FAILUER));
            }
        }
        return result;
    }

    /**
     * 提取博文
     * @param card
     * @return
     */
    private BlogDto extractBlog(Element card) {
        Element cardFeed = card.getElementsByClass("card-feed").first();
        // 博主信息
        BloggerInfoDto bloggerInfo = extractBloggerInfo(cardFeed);
        // 微博正文
        BlogTextDto blogText = extractBlogText(cardFeed);
        // 博文独立网址、发布时间
        BlogPublishInfoDto blogPublishInfo = extractPublishInfo(cardFeed);
        // 转发、评论、点赞
        Element cardAct = card.getElementsByClass("card-act").first();
        Element collectInfo = cardAct.getElementsByTag("li").first();
        Element forwardInfo = collectInfo.nextElementSibling();
        ActInfoDto actInfo = extractActInfo(forwardInfo);
        // 转发的微博
        Element cardComment = cardFeed.getElementsByClass("card-comment").first();
        BloggerInfoDto forwardedBloggerInfo = null;
        BlogTextDto forwardedBlogText = null;
        BlogPublishInfoDto forwardedBlogPublishInfo = null;
        ActInfoDto forwardedActInfo = null;
        ForwardedBlogDto forwardedBlog = null;
        if (cardComment != null) {
            forwardedBloggerInfo = extractBloggerInfo(cardComment);
            if (forwardedBloggerInfo != null) {
                forwardedBlogText = extractBlogText(cardComment);
                forwardedBlogPublishInfo = extractPublishInfo(cardComment);
                Element cardCommentAct = cardComment.getElementsByClass("act s-fr").first();
                Element forwardedForwardInfo = cardCommentAct.getElementsByTag("li").first();
                forwardedActInfo = extractActInfo(forwardedForwardInfo);
                forwardedBlog = new ForwardedBlogDto(forwardedBloggerInfo,
                        forwardedBlogText, forwardedBlogPublishInfo, forwardedActInfo);
            }
        }
        // 构建微博实体
        return new BlogDto(bloggerInfo, blogText, blogPublishInfo,
                actInfo, forwardedBlog);
    }

    /**
     * 提取互动信息
     * @param forwardInfo
     * @return
     */
    private ActInfoDto extractActInfo(Element forwardInfo) {
        String[] forwardInfoArr = forwardInfo.text().trim().split(" ");
        String forwardNum = "0";
        if (forwardInfoArr.length > 1) {
            forwardNum = forwardInfoArr[forwardInfoArr.length - 1];
        }
        Element commentInfo = forwardInfo.nextElementSibling();
        String[] commentInfoArr = commentInfo.text().trim().split(" ");
        String commentNum = "0";
        if (commentInfoArr.length > 1) {
            commentNum = commentInfoArr[commentInfoArr.length - 1];
        }
        Element likeInfo = commentInfo.nextElementSibling().getElementsByTag("em").first();
        String likeNum = likeInfo.text().trim();
        if (StringUtils.isEmpty(likeNum)) {
            likeNum = "0";
        }
        return new ActInfoDto(forwardNum, commentNum, likeNum);
    }

    /**
     * 提取发布信息
     * @param publishInfoContainer
     * @return
     */
    private BlogPublishInfoDto extractPublishInfo(Element publishInfoContainer) {
        Element publishInfo = publishInfoContainer.getElementsByClass("from").last();
        Element blogLinkInfo = publishInfo.getElementsByTag("a").first();
        String blogLink = "https:" + blogLinkInfo.attr("href");
        String sourceTime = blogLinkInfo.text().trim();
        LocalDateTime publishDateTime;
        LocalDateTime now = LocalDateTime.now();
        if (sourceTime.contains("秒前")) {
            String seconds = sourceTime.substring(0, sourceTime.indexOf("秒前"));
            publishDateTime = now.minusSeconds(Long.parseLong(seconds));
        } else if (sourceTime.contains("分钟前")) {
            String minutes = sourceTime.substring(0, sourceTime.indexOf("分钟前"));
            publishDateTime = now.minusMinutes(Long.parseLong(minutes));
        } else if (sourceTime.contains("今天")) {
            String hourPart = sourceTime.substring(sourceTime.indexOf(":") - 2, sourceTime.indexOf(":") - 1);
            String minutePart = sourceTime.substring(sourceTime.indexOf(":") + 1, sourceTime.indexOf(":") + 3);
            publishDateTime = now.withHour(Integer.parseInt(hourPart))
                    .withMinute(Integer.parseInt(minutePart))
                    .withSecond(0);
        } else {
            if (sourceTime.indexOf("年") != -1) {
                String yearPart = sourceTime.substring(0, sourceTime.indexOf("年"));
                String monthPart = sourceTime.substring(sourceTime.indexOf("年") + 1, sourceTime.indexOf("月"));
                String dayOfMonthPart = sourceTime.substring(sourceTime.indexOf("月") + 1, sourceTime.indexOf("日"));
                String hourPart = sourceTime.substring(sourceTime.indexOf(" ") + 1, sourceTime.indexOf(":"));
                String minutePart = sourceTime.substring(sourceTime.indexOf(":") + 1, sourceTime.indexOf(":") + 3);
                publishDateTime = LocalDateTime.of(Integer.parseInt(yearPart),
                        Integer.parseInt(monthPart), Integer.parseInt(dayOfMonthPart),
                        Integer.parseInt(hourPart), Integer.parseInt(minutePart));
            } else {
                String monthPart = sourceTime.substring(0, sourceTime.indexOf("月"));
                String dayOfMonthPart = sourceTime.substring(sourceTime.indexOf("月") + 1, sourceTime.indexOf("日"));
                String hourPart = sourceTime.substring(sourceTime.indexOf(" ") + 1, sourceTime.indexOf(":"));
                String minutePart = sourceTime.substring(sourceTime.indexOf(":") + 1, sourceTime.indexOf(":") + 3);
                publishDateTime = now.withMonth(Integer.parseInt(monthPart))
                        .withDayOfMonth(Integer.parseInt(dayOfMonthPart))
                        .withHour(Integer.parseInt(hourPart))
                        .withMinute(Integer.parseInt(minutePart))
                        .withSecond(0);
            }
        }
        return new BlogPublishInfoDto(blogLink, publishDateTime);
    }

    /**
     * 提取出微博正文
     * @param textContainer
     */
    private BlogTextDto extractBlogText(Element textContainer) {
        Element text = textContainer.getElementsByClass("txt").first();
        Element isTextFold = text.getElementsByTag("a").last();
        if (isTextFold != null && isTextFold.text().trim().contains("展开全文")) {
            text = text.nextElementSibling();
        }
        String imgText = text.imgText();
        return new BlogTextDto(imgText);
    }

    /**
     * 提取出博主id、博主名称、博主主页、博主认证信息
     * @param bloggerInfo
     */
    private BloggerInfoDto extractBloggerInfo(Element bloggerInfo) {
        Element identityInfo = bloggerInfo.getElementsByClass("name").first();
        String href = identityInfo.attr("href");
        if (StringUtils.isEmpty(href)) {    // 作者设置了访问权限不允许其他人查看
            return null;
        }
        String bloggerHomePage = "https:" + href;
        String bloggerId = "";
        Pattern pattern = Pattern.compile("(\\d{10})");
        Matcher matcher = pattern.matcher(href);
        if (matcher.find()) {
            bloggerId = matcher.group(1);
        }
        String bloggerNickname = identityInfo.attr("nick-name");
        Element authInfo = identityInfo.nextElementSibling();
        String bloggerAuthInfo = "";
        if (authInfo != null && authInfo.is("a") ) {
            bloggerAuthInfo = authInfo.attr("title");
        }

        return new BloggerInfoDto(bloggerId, bloggerNickname, bloggerHomePage, bloggerAuthInfo);
    }
}
