package practice;

import org.apache.log4j.BasicConfigurator;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class SinaBlogProcessor implements PageProcessor {

    static String articleUrl = "http://blog.sina.com.cn/s/blog_\\w+\\.html";

    static String listUrl = "http://blog.sina.com.cn/s/articlelist_\\w+\\.html";

    static String startUrl = "http://blog.sina.com.cn/s/articlelist_1487828712_0_1.html";

    static String startUrl2 = "http://blog.sina.com.cn/s/blog_58ae76e80100to5q.html";

    private Site site = Site.me().setCharset("utf-8").setRetryTimes(3);

    public void process(Page page) {

        if (page.getUrl().regex(articleUrl).match()) {//url为文章页面时
            //page.putField("key","field");
            page.putField("title", page.getHtml().xpath("//div[@id='articlebody']//div[@class='articalTitle']/h2").get());//记录文章标题
            //.putField("content", page.getHtml().xpath("//div[@class='articalContent']"));//记录文章内容
            page.putField("date", page.getHtml().xpath("//div[@id='articlebody']//div[@class='articalTitle']/span").get());//记录文章发表时间
            page.putField("tag", page.getHtml().xpath("//div[@id='articalTag']//td[@class='blog_tag']/span").get());//记录标签（？？？
            page.putField("category", page.getHtml().xpath("//td[@class='blog_class']/span").get());//记录分类（？？？
        } else {//url为文章列表页面时
            page.addTargetRequests(page.getHtml().links().regex(listUrl).all());//添加文章列表页面到待爬列表
            page.addTargetRequests(page.getHtml().links().regex(articleUrl).all());//添加文章页面到待爬列表
        }
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();

        Spider.create(new SinaBlogProcessor()).addUrl(startUrl2).addPipeline(new ConsolePipeline()).addPipeline(new FilePipeline()).run();


//        Spider spider = new Spider(new SinaBlogProcessor());
//        spider.addPipeline(new FilePipeline());
//        spider.addUrl(startUrl).run();


    }
}
