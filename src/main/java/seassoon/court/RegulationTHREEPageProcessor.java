package seassoon.court;

import org.apache.log4j.BasicConfigurator;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

public class RegulationTHREEPageProcessor implements PageProcessor{

    String cookie = "ASP.NET_SessionId=fsdakgqxrl12grllmybnxosa; Hm_lvt_3f2eaec16fb0951177798309dd3127b7=1504780457,1504784925; Hm_lpvt_3f2eaec16fb0951177798309dd3127b7=1504784925";

    private Site site = Site.me().addCookie("image.lawxp.com",cookie).setSleepTime(10000).setRetryTimes(1).setRetrySleepTime(10000).setTimeOut(10000);


    String regex_documentUrl = "https://www\\.lawxp\\.com/statute/s\\d+\\.html";
    String regex_documentLink = "/statute/s\\d+\\/.html";
    String regex_listLink = "\\?pg=\\d+&CourtId=(9759|10000004)";
    String preLink = "https://www.lawxp.com/statute/";

    @Override
    public void process(Page page) {
        if (page.getUrl().regex(regex_documentUrl).match()) {
            //如果是文档页面，则存入数据库
            page.putField("url", page.getUrl().toString());
            page.putField("content", page.getHtml().toString());
        } else {
            //取得链接中符合文档页面或列表页面规则的加入爬取列表
            page.addTargetRequests(page.getHtml().links().regex(regex_documentLink).all());
            page.addTargetRequests(page.getHtml().links().regex(regex_listLink).all()); //待测试
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }


    public static void main(String[] args){
        BasicConfigurator.configure();

        String url_guowuyuan = "https://www.lawxp.com/statute/?CourtId=9759";
        String url_renda = "https://www.lawxp.com/statute/?CourtId=10000004";
        String url_renda_2 = "https://www.lawxp.com/statute/?pg=2&CourtId=10000004";

        Spider.create(new RegulationTWOPageProcessor())
                .addUrl(url_renda_2)
                .addPipeline(new MySQLPipeline("regulations_html_test"))
                .setScheduler(new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover()))
                .thread(2).run();

    }
}
