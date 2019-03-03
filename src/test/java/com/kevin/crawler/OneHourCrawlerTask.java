package com.kevin.crawler;

import com.kevin.common.util.DateTimeUtil;
import com.kevin.crawler.dto.*;
import com.kevin.crawler.exception.BizException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kevin.common.util.DateTimeUtil.DateTimeFormat.LONG_DATE_TIME_PATTERN_WITH_LINE_TO_HOUR;
import static com.kevin.crawler.constant.BizStatusCode.READ_OR_CONNECT_ERROR;
import static com.kevin.crawler.constant.BizStatusCode.codeMsgMap;

/**
 * @类名: OneHourCrawlerTask<br />
 * @包名：com.kevin.crawler<br/>
 * @作者：kevin<br/>
 * @时间：2019/3/3 10:54<br/>
 * @版本：1.0<br/>
 * @描述：一小时爬虫任务<br/>
 */
public class OneHourCrawlerTask implements Runnable {

    private String keywords;
    private LocalDateTime start;
    private LocalDateTime end;
    private String userAgent;
    private Sheet sheet;
    private AtomicInteger rowNum;
    private CountDownLatch latch;

    public OneHourCrawlerTask(String keywords, LocalDateTime start, LocalDateTime end, String userAgent, Sheet sheet, AtomicInteger rowNum) {
        this.keywords = keywords;
        this.start = start;
        this.end = end;
        this.userAgent = userAgent;
        this.sheet = sheet;
        this.rowNum = rowNum;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            crawl(keywords, start, end, userAgent, sheet, rowNum);
        } catch (BizException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    private void crawl(String keywords, LocalDateTime start, LocalDateTime end, String userAgent, Sheet sheet, AtomicInteger rowNum) throws BizException {
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
            Document doc;
            try {
                doc = Jsoup.connect(url).userAgent(userAgent).timeout(20000).get();
            } catch (IOException e) {
                throw new BizException(e, READ_OR_CONNECT_ERROR, codeMsgMap.get(READ_OR_CONNECT_ERROR));
            }
            Element feedList = doc.getElementById("pl_feedlist_index");
            Element noResultCard = feedList.getElementsByClass("card-no-result").first();
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
                wrapperBody(sheet, rowNum.getAndIncrement(), blog);
            }
            pageNum++;
        }
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
            forwardedBlogText = extractBlogText(cardComment);
            forwardedBlogPublishInfo = extractPublishInfo(cardComment);
            Element cardCommentAct = cardComment.getElementsByClass("act s-fr").first();
            Element forwardedForwardInfo = cardCommentAct.getElementsByTag("li").first();
            forwardedActInfo = extractActInfo(forwardedForwardInfo);
            forwardedBlog = new ForwardedBlogDto(forwardedBloggerInfo,
                    forwardedBlogText, forwardedBlogPublishInfo, forwardedActInfo);
        }
        // 构建微博实体
        return new BlogDto(bloggerInfo, blogText, blogPublishInfo,
                actInfo, forwardedBlog);
    }

    /**
     * 填充表格
     * @param sheet
     * @param rowNum
     * @param blog
     */
    private void wrapperBody(Sheet sheet, int rowNum, BlogDto blog) {
        System.out.println(rowNum);
        synchronized (sheet) {  // 非线程安全，加同步
            Row row = sheet.createRow(rowNum);
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
        if (!StringUtils.isEmpty(likeNum)) {
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
