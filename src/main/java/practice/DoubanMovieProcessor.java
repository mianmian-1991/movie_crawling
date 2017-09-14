package practice;

import org.apache.log4j.BasicConfigurator;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;
import us.codecraft.webmagic.selector.Selectable;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 爬取电影列表 v0.1
 * 更多影片信息可以去电影页面补充
 */
public class DoubanMovieProcessor implements PageProcessor {

    private Site site = Site.me().setCharset("utf-8").setRetryTimes(1).setDomain("www.douban.com")
            .setSleepTime(10000).setRetrySleepTime(10000);

    private String regex_page = "https://movie\\.douban\\.com/people/BBmachtalles/collect\\?start=\\d+.+";

    private String DB_URL = "jdbc:mysql://localhost:3306/chimney?characterEncoding=utf8&useSSL=false";
    private String USER = "root";
    private String PASS = "123456";
    private String tableName = "movie_collect";

//    private String outputPath = "/Users/chimney/Coding Practice/movieresulttest.txt";

    private BufferedWriter bw;



    public DoubanMovieProcessor(){
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("movie.txt"),true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ConnectionDB connectionDB = new ConnectionDB(DB_URL, USER, PASS);//用于写入数据库

    public void process(Page page) {

        List<Selectable> items = page.getHtml().xpath("//div[@class='grid-view']/div[@class='item']").nodes();
        for (Selectable item : items) {
            Map<String, String> results = new LinkedHashMap<>();
            results.put("movie_url", item.$(".info li[class=title] a").links().toString());
            results.put("movie_name", item.$(".info li[class=title] em").toString());
            results.put("movie_nameall", item.$(".info li[class=title] a").toString());
            results.put("info", item.$(".info li[class=intro]").toString());
            results.put("mark_date", item.$(".info li .date").toString());
            results.put("mark_tags", item.$(".info li .tags").toString());
            results.put("mark_comment", item.$(".info li .comment").toString());

//            page.putField("movie_url", item.$(".info li[class=title] a").links().toString());
//            page.putField("movie_name", item.$(".info li[class=title] em").toString());
//            page.putField("movie_nameall", item.$(".info li[class=title] a").toString());
//            page.putField("info", item.$(".info li[class=intro]").toString());
//            page.putField("mark_date", item.$(".info li .date").toString());
//            page.putField("mark_tags", item.$(".info li .tags").toString());
//            page.putField("mark_comment", item.$(".info li .comment").toString());

//            Map<String, Object> results = page.getResultItems().getAll();
//            for(Map.Entry<String,Object> entry:results.entrySet()){
//
//            }

            String[] columns = {"url", "name", "name_all", "info", "mark_date", "mark_tags", "mark_comment"};
            String tags = null;
            if (results.get("mark_tags") != null) {
                tags = removeHTMLLabel(results.get("mark_tags")).substring(3);
            }
//            if (results.get("mark_comment") != null) {
//                comments = removeHTMLLabel(results.get("mark_comment"));
//            }
            String[] paras = {results.get("movie_url")
                    , removeHTMLLabel(results.get("movie_name"))
                    , removeHTMLLabel(results.get("movie_nameall"))
                    , removeHTMLLabel(results.get("info"))
                    , removeHTMLLabel(results.get("mark_date"))
                    , tags
                    , removeHTMLLabel(results.get("mark_comment"))};

            connectionDB.insert(columns, paras, tableName);
            try{
            bw.write("complete movie: "+ removeHTMLLabel(results.get("movie_name")) + ", url: "+ results.get("movie_url")+"\n");}
            catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            bw.write("---------------\ncomplete page: "+page.getUrl().toString()+"\n-------------\n");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        page.addTargetRequests(page.getHtml().xpath("//div[@class=paginator]").links().regex(regex_page).all());
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String mainUrl = "https://movie.douban.com/people/BBmachtalles/collect";
        String mainUrl2 = "https://movie.douban.com/people/BBmachtalles/collect?start=0&amp;sort=time&amp;rating=all&amp;filter=all&amp;mode=grid";

        BasicConfigurator.configure();
        Spider.create(new DoubanMovieProcessor()).addUrl(mainUrl2)
//                .addPipeline(new FilePipeline("/Users/chimney/Coding Practice/movieresulttest.txt"))
                .setScheduler(new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover()))
                .thread(3).run();
    }

    public static String removeHTMLLabel(String text) {
        if (text == null) return null;
        StringBuilder textBuilder = new StringBuilder(text);
        removeLabel(textBuilder);
        return textBuilder.toString().trim();
    }

    private static void removeLabel(StringBuilder contextDetail) {
        Pattern p = Pattern.compile("<.+?>");
        Matcher m = p.matcher(contextDetail);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            contextDetail.delete(start, end);
            m = p.matcher(contextDetail);
        }
    }

}
