package practice;

import org.apache.log4j.BasicConfigurator;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.PlainText;

import java.util.List;

public class DoubanMovieProcessor implements PageProcessor{

    private Site site = Site.me().setCharset("utf-8").setRetryTimes(3).setDomain("www.douban.com");

    private String regex_page = "https://movie\\.douban\\.com/people/BBmachtalles/collect\\?start=\\d+.+";

    public void process(Page page) {

        int count = 0;
        List<Selectable> items = page.getHtml().xpath("//div[@class='grid-view']/div[@class='item']").nodes();
        for(Selectable item: items){
            page.putField(String.valueOf(count)+"_movie_url",item.$(".info li[class=title] a"));
            page.putField(String.valueOf(count)+"_movie_name",item.$(".info li[class=title] em"));
            page.putField(String.valueOf(count)+"_info",item.$(".info li[class=intro]"));
            page.putField(String.valueOf(count)+"_mark_date",item.$(".info li .date"));
            page.putField(String.valueOf(count)+"_mark_tags",item.$(".info li .tags"));
            page.putField(String.valueOf(count)+"_mark_comment",item.$(".info li .comment"));
            count++;
        }
        page.addTargetRequests(page.getHtml().xpath("//div[@class=paginator]").links().regex(regex_page).all());

    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args){
        String mainUrl = "https://movie.douban.com/people/BBmachtalles/collect";

        BasicConfigurator.configure();
        Spider.create(new DoubanMovieProcessor()).addUrl(mainUrl).addPipeline(new FilePipeline("/Users/chimney/Coding Practice/movieresulttest.txt")).addPipeline(new ConsolePipeline()).run();
    }
}
