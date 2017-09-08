package seassoon.court;

import org.apache.log4j.BasicConfigurator;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegulationTHREEPageProcessor implements PageProcessor {

    String cookie = "nLoginId=4589763; nLoginTime=2017/9/7 19:48:35; " +
            "nLoginName=chim_0; ASP.NET_SessionId=5jnq14jciicc3mw5zsmyddqu; " +
            "tool=; toolsure=; " +
            "Hm_lvt_3f2eaec16fb0951177798309dd3127b7=1504780457,1504784925,1504853373; " +
            "Hm_lpvt_3f2eaec16fb0951177798309dd3127b7=1504860641";

    private Site site = Site.me().addCookie("www.lawxp.com", cookie).setSleepTime(8000).setRetryTimes(1).setRetrySleepTime(8000).setTimeOut(8000);

    private BufferedWriter bw;
//            = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("three.txt"),true)));

    public RegulationTHREEPageProcessor(){
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("three.txt"),true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    String regex_documentUrl = "https://www\\.lawxp\\.com/statute/s\\d+\\.html";
    String regex_documentLink = "/statute/s\\d+\\/.html";
//    String regex_listLink = "\\?pg=\\d+&(amp;)*CourtId=(9759|10000004)";
    String regex_listLink = "\\?pg=\\d+&(amp;)*CourtId=32370";
    String preLink = "https://www.lawxp.com/statute/";

    @Override
    public void process(Page page) {
        if (page.getUrl().regex(regex_documentUrl).match()) {
            //如果是文档页面，则存入数据库
            page.putField("url", page.getUrl().toString());
            page.putField("content", page.getHtml().toString());
        } else {
            //取得链接中符合文档页面或列表页面规则的加入爬取列表
//            List<String> pageUrls = page.getHtml().links().regex(regex_documentUrl).all();

//            List<String> listUrls= new ArrayList<>();
//            String content = page.getHtml().toString();
//            Pattern pattern = Pattern.compile(regex_listLink);
//            Matcher matcher = pattern.matcher(content);
//            while (matcher.find()){
//                listUrls.add(preLink+matcher.group());
//            }
            page.addTargetRequests(page.getHtml().links().regex(regex_documentUrl).all()); //通过测试
//            page.addTargetRequests(listUrls); //通过测试
            page.setSkip(true);
            try {
                bw.write("list page resolved: "+page.getUrl().toString()+"\n");
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }


    public static void main(String[] args) {
        BasicConfigurator.configure();

        String url_guowuyuan = "https://www.lawxp.com/statute/?CourtId=9759";
        String url_renda = "https://www.lawxp.com/statute/?CourtId=10000004";
        String url_quanguorenda = "https://www.lawxp.com/statute/?pg=11&CourtId=32370";

        List<String> menuList = new ArrayList<>();
        for(int page = 11; page<=226; page++){
            menuList.add("https://www.lawxp.com/statute/?pg="+page+"&CourtId=32370");
        }
        String[] menus = menuList.toArray(new String[menuList.size()]);

//        String url_renda_2 = "https://www.lawxp.com/statute/?pg=2&CourtId=10000004";
        String url_exam618 = "https://www.lawxp.com/statute/s1784618.html";

        Spider.create(new RegulationTHREEPageProcessor())
                .addUrl(menus)
                .addPipeline(new MySQLPipeline("regulations_html_huifawang"))
                .setScheduler(new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover()))
                .thread(4).run();

    }
}
