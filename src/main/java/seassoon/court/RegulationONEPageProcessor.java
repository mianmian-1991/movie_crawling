package seassoon.court;

import org.apache.log4j.BasicConfigurator;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegulationONEPageProcessor implements PageProcessor {

    final static String DB_URL = "jdbc:mysql://localhost:3306/court?characterEncoding=utf8&useSSL=false";
    final static String USER = "root";
    final static String PASS = "123456";

    private Site site = Site.me().setRetryTimes(2).setDomain("law.npc.gov.cn");

    private String regex_documentUrl = "http://law\\.npc\\.gov\\.cn.+/FLFG/flfgByID\\.action\\?flfgID=\\d+.+";
    private String regex_listUrl = "";
    String regex_showLocation = "javascript:showLocation\\(\\'(\\d*)\\',\\'(\\d*)\\',\\'(\\d*)\\'\\)";
    String regex_toUpDownPage = "";

    public void process(Page page) {

        if (page.getUrl().regex(regex_documentUrl).match()) {
            //如果是文档页面，则存入数据库
            page.putField("url", page.getUrl().toString());
            page.putField("content", page.getHtml().toString());
        } else {
            //取得链接中符合文档页面或列表页面规则的加入爬取列表
//            List<String> locationlist = page.getHtml().links().regex(regex_showLocation).all();

            List<String> locationlist = new LinkedList<>();
            Pattern pattern = Pattern.compile(regex_showLocation);
            String content = page.getHtml().toString();
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                locationlist.add(matcher.group());
                String documentUrl = showlocation(matcher.group(1), matcher.group(2), matcher.group(3));
                System.out.println(documentUrl);
                page.addTargetRequest(documentUrl);
            }

            page.setSkip(true);
        }


//        for (String location : locationlist) {
//            Pattern pattern2 = Pattern.compile(regex_showLocation);
//            Matcher matcher2 = pattern2.matcher(location);
//            while (matcher.find()) {
//                String temp = matcher2.group();
//                String documentUrl = showlocation(matcher.group(1), matcher.group(2), matcher.group(3));
//                page.addTargetRequest(documentUrl);
//            }
//        }


    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
//        String mainpage = "http://law.npc.gov.cn/FLFG/index.jsp";
//        String mainsearchpage = "http://law.npc.gov.cn/FLFG/ksjsCateGroup.action";
        String mainlist = "http://law.npc.gov.cn/FLFG/getAllList.action";

//        String document_1 = "http://law.npc.gov.cn/FLFG/flfgByID.action?flfgID=34964926&keyword=&zlsxid=10";

        Request[] requests = new Request[200];

        for (int curPage = 1; curPage <= 200; curPage++) {

            try {
//            int curPage = 5;
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("pagesize", 50);
                params.put("curPage", curPage);
                Request request = new Request();
                request.setUrl(mainlist);
                request.setMethod(HttpConstant.Method.POST);
                request.setRequestBody(HttpRequestBody.form(params, "utf-8"));
                requests[curPage - 1] = request;


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        BasicConfigurator.configure();
        Spider.create(new RegulationONEPageProcessor()).addRequest(requests)
                .addPipeline(new MySQLPipeline(DB_URL, USER, PASS, "regulations_html_zhongguofalvfaguixinxiku"))
                .thread(5).run();


    }

    private String showlocation(String param1, String param2, String param3) {
        return "http://law.npc.gov.cn:80/FLFG/flfgByID.action?flfgID=" + param1 + "&keyword=" + encodeURI(param2) + "&zlsxid=" + param3;
    }

    //encodeURI可能还存在一些问题，不过目前网页上看到的参数一般为空
    private static String encodeURI(String param) {
        try {
            return URLEncoder.encode(param, "utf-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            return param;
        }
    }

}
