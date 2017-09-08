package seassoon.court;

import org.apache.log4j.BasicConfigurator;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

import java.util.ArrayList;
import java.util.List;

public class RegulationTWOPageProcessor implements PageProcessor {

    private String regex_documentUrl = "http://www\\.chinacourt\\.org/law/detail/\\d+/\\d+/id/\\d+\\.shtml";
    private String regex_documentLink = "/law/detail/\\d+/\\d+/id/\\d+\\.shtml";

    private Site site = Site.me().setRetryTimes(1).setTimeOut(7000).setDomain("www.chinacourt.org").setSleepTime(12000).setRetrySleepTime(5000);

    @Override
    public void process(Page page) {
        if (page.getUrl().regex(regex_documentUrl).match()) {
            //如果是文档页面，则存入数据
            page.putField("url", page.getUrl().toString());
            page.putField("content", page.getHtml().toString());
        } else {
            //取得链接中符合文档页面规则的加入爬取列表
            page.addTargetRequests(page.getHtml().links().regex(regex_documentLink).all());
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        BasicConfigurator.configure();

        String nationMainpage = "http://www.chinacourt.org/law/more/law_type_id/MzAwNEAFAA%3D%3D.shtml";
        String localMainpage = "http://www.chinacourt.org/law/more/law_type_id/MzAwMkAFAA%3D%3D.shtml";
        String explanationMainpage = "http://www.chinacourt.org/law/more/law_type_id/MzAwM0AFAA%3D%3D.shtml";

        List<String> urlList = new ArrayList<>();
        int page;
        //添加国家法律法规的列表页
        for (page = 151; page <= 200; page++) {
            urlList.add("http://www.chinacourt.org/law/more/law_type_id/MzAwNEAFAA%3D%3D/page/" + page + ".shtml");
        }

        //添加司法解释的列表页
//        for (page = 1; page <= 4; page++) {
//            urlList.add("http://www.chinacourt.org/law/more/law_type_id/MzAwM0AFAA%3D%3D/page/" + page + ".shtml");
//        }

        //添加地方法规的列表页
//        for (page = 1; page <= 1175; page++) {
//            urlList.add("http://www.chinacourt.org/law/more/law_type_id/MzAwMkAFAA%3D%3D/page/" + page + ".shtml");
//        }
        String[] urls = urlList.toArray(new String[urlList.size()]);


        Spider.create(new RegulationTWOPageProcessor()).addUrl(urls)
                .addPipeline(new MySQLPipeline("regulations_html_zhongguofayuanwang"))
                .setScheduler(new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover()))
                .thread(3).run();

    }
}
