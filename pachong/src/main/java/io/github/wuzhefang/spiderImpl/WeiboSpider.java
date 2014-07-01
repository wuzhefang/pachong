package io.github.wuzhefang.spiderImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.UrlUtils;

import io.github.wuzhefang.spider.SpiderInterface;
import io.github.wuzhefang.spider.spiderabstract.Spider;
import io.github.wuzhefang.utils.SpiderStringUtils;
import io.github.wuzhefang.utils.SpiderUrlUtils;

public class WeiboSpider extends Spider {
	
	
	//构造方法
	public WeiboSpider(String domainId, String domainName, String userId){
		this.setDomainId(domainId);
		this.setDomainName(domainName);
		this.setUserId(userId);
		this.setDomainAndUserId(domainId+userId);
	}
	
	
	
	
	public void getContent(){
		
				WebClient webClient = this.getWebClient();// new WebClient(BrowserVersion.CHROME);
				
				try {
					HtmlPage page = webClient.getPage("http://weibo.com/u/"+this.getUserId()+"?from=profile&wvr=5&loc=infdomain");
//					HtmlPage page = webClient.getPage("http://weibo.com/login");
//					List l = page.getByXPath("//div[@class='WB_text']");
//					HtmlDivision hd=null;
//					if(l.size()>0){
//					 hd = (HtmlDivision)l.get(1);
//					 System.out.println(hd.asXml());
//					}else{
//						System.out.println("nothing");
//					}
					HtmlCleaner cleaner = new HtmlCleaner();
					CleanerProperties props = cleaner.getProperties();
			        props.setUseCdataForScriptAndStyle(true);
			        props.setRecognizeUnicodeChars(true);
			        props.setUseEmptyElementTags(true);
			        props.setAdvancedXmlEscape(true);
			        props.setTranslateSpecialEntities(true);
			        props.setBooleanAttributeValues("empty");
			        
			        String strs = SpiderStringUtils.replaceJsContent(page.getDocumentElement().asXml());//page.getWebResponse().getContentAsString().replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");
					
			        TagNode contentNode = cleaner.clean(strs);
			        
			        String moreContentUrl = SpiderUrlUtils.getMoreButtonHref(contentNode);
			        
			        //新页面
			        HtmlPage firstPage = webClient.getPage("http://weibo.com/u/"+moreContentUrl);
			        
			        String firstPageStrs = SpiderStringUtils.replaceJsContent(firstPage.getDocumentElement().asXml());//page.getWebResponse().getContentAsString().replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");
					
			        TagNode firstContentNode = cleaner.clean(firstPageStrs);
			        
			        Object[] nodes = firstContentNode.evaluateXPath("//div[@class='WB_text']");
			        Object[] nodesForTime = firstContentNode.evaluateXPath("//div[@class='WB_from']");
			        PrettyXmlSerializer pxs= new PrettyXmlSerializer(props);
			        StringBuffer sb = new StringBuffer();
			        for(int i=1;i<nodes.length;i++){
			        	TagNode n = (TagNode)nodes[i];
			        	TagNode n1 = (TagNode)nodesForTime[i];
//			        	pxs.writeXmlToFile(n, "/Users/signv/ttClean.txt");
//			        	writeFile(n.getText().toString(),"/Users/signv/ttClean.txt");
			        	
//			        	TagNode[] tn = n1.getElementsByName("a", false);
			        	TagNode time = ((n1.getElementsByName("a", false))[0]);
			        	int timeStart = time.getOriginalSource().indexOf("title")+7;
			        	String timeStr = time.getOriginalSource().substring(timeStart, timeStart+16);
//			        	System.out.println();
			        
			        	sb.append(timeStr);
			        	sb.append(n.getText().toString().replaceAll(" ", ""));
			        	sb.append("\r\n");
			        	
			        	
//			        	HtmlElement he = (page.getDocumentElement().getElementsByTagName("")).get(0);
//			        	he.blur();
			        	
//			        	writeFile(timeStr,"/Users/signv/ttClean.txt");
			        }
			        //第一次滚动
			        String rollingUrlOne = SpiderUrlUtils.createRollingOneAddContentUrl(this.getDomainId(), 1, this.getDomainAndUserId());
			        WebRequest requestOne = new WebRequest(new URL(rollingUrlOne), HttpMethod.GET);

			        WebResponse jsonOne = webClient.loadWebResponse(requestOne);
			        JSONObject jsonObj = JSONObject.fromObject(jsonOne.getContentAsString());
			        String data = (String)jsonObj.get("data");

			        Document docOne = Jsoup.parse(data);
			        Elements elementsOne = docOne.select("div.WB_text");
			        Elements elementsOneTime = docOne.select("div.WB_from");
			        
			        for(int i=0;i<elementsOneTime.size();i++){
			        	Element text = (Element)elementsOne.get(i);
			        	Element time = (Element)((Element)elementsOneTime.get(i)).getElementsByTag("a").get(0);
			        	
			        	sb.append(time.attr("title"));
				        sb.append(text.text());
				        sb.append("\r\n");
			        }
			        
//			        Thread.sleep(2000);
			        
			        //第二次滚动
			        String rollingUrlTwo = SpiderUrlUtils.createRollingTwoAddContentUrl(this.getDomainId(), 1, this.getDomainAndUserId());
			        WebRequest requestTwo = new WebRequest(new URL(rollingUrlTwo), HttpMethod.GET);

			        WebResponse jsonTwo = webClient.loadWebResponse(requestTwo);
			        JSONObject jsonObj2 = JSONObject.fromObject(jsonTwo.getContentAsString());
			        String data2 = (String)jsonObj2.get("data");

			        Document docTwo = Jsoup.parse(data2);
			        Elements elementsTwo = docTwo.select("div.WB_text");
			        
			        Elements elementsTwoTime = docTwo.select("div.WB_from");
			        
			        for(int i=0;i<elementsTwoTime.size();i++){
			        	Element text = (Element)elementsTwo.get(i);
			        	Element time = (Element)((Element)elementsTwoTime.get(i)).getElementsByTag("a").get(0);
			        	
			        	sb.append(time.attr("title"));
				        sb.append(text.text());
				        sb.append("\r\n");
			        }
//			        
//			        HtmlInput hi = (HtmlInput)list.get(3);
//			        hi.focus();
//			        hi.blur();
					
			        writeFile(sb.toString(),"/Users/signv/ttClean.txt");
			        
//					List<DomNode> list = (List<DomNode>) ;
					writeFile(pxs.getXmlAsString(firstContentNode),"/Users/signv/tt.txt");
//					writeFile(contentNode.getText().toString(),"/Users/signv/ttClean.txt");
//					machFile(page.getDocumentElement().asXml());
					
//					String orStr = page.getDocumentElement().asXml().replaceAll(" ", "");
//					int b = orStr.indexOf("WB_text");
//					
//					String[] strs = orStr.split("WB_text");
//					
//					for(String a : strs){
//						System.out.println(a);
//						
//					}
					
//					System.out.println(orStr.indexOf("WB_text"));
//					System.out.println(orStr.substring(b, b+400));
					
					
					//取分页列表
					Elements elementsPage = docTwo.select("div.W_pages > span.list > div > a");
					if(elementsPage!=null){
					//去掉分页列表中的最后一条，最后一条为第一页
					for(int i=elementsPage.size()-2;i>=0;i--){
					Element element = elementsPage.get(i);
					String pageHref = element.attr("href"); 
					System.out.println(element.text()+pageHref);
//					loadPageData(client,pageHref,elementsPage.size()-i,domainId,pageId);
					// if(i>=50){
					// System.out.println("微博内容太多，只取了前50页的微博内容。。。");
					// break;
					// }
					}
					}
					
				} catch (FailingHttpStatusCodeException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XPatherException e) {
					e.printStackTrace();
				} 
		
	}
	
	public void writeFile(String str, String filePath){
		try {
			FileOutputStream fo = new FileOutputStream(new File(filePath),false);
			fo.write(str.getBytes());
			fo.flush();
			fo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public WebClient getWebClient() {
		
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		//需使用自己的cookie
		String k = "";
		
		Cookie cookie = new Cookie(this.getDomainName(), "", k);
		
		CookieManager cookieManager = new CookieManager();
		
		cookieManager.addCookie(cookie);
		
		webClient.setCookieManager(cookieManager);
		
		return webClient;
	}




	@Override
	public List<String> getPageUrlList(WebClient webClient) {
		
		ArrayList<String> returnList = new ArrayList<String>();
		
		try {
	        //第二次滚动
	        String rollingUrlTwo = SpiderUrlUtils.createRollingTwoAddContentUrl(this.getDomainId(), 1, this.getDomainAndUserId());
	        WebRequest requestTwo = new WebRequest(new URL(rollingUrlTwo), HttpMethod.GET);

	        WebResponse jsonTwo = webClient.loadWebResponse(requestTwo);
	        JSONObject jsonObj2 = JSONObject.fromObject(jsonTwo.getContentAsString());
	        String data2 = (String)jsonObj2.get("data");

	        Document docTwo = Jsoup.parse(data2);
	        
	        //取分页列表
			Elements elementsPage = docTwo.select("div.W_pages > span.list > div > a");
			if (elementsPage != null) {
				// 去掉分页列表中的最后一条，最后一条为第一页
				for (int i = elementsPage.size() - 1; i >= 0; i--) {
					Element element = elementsPage.get(i);
					String pageHref = element.attr("href");
					returnList.add("http://"+this.getDomainName() + pageHref);
					System.out.println("http://"+this.getDomainName() + pageHref);
				}
			}
		}catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return returnList;
	}




	@Override
	public String loadPage(WebClient webClient, String pageUrl, int pageId) {
		
		StringBuffer sb = new StringBuffer();
		long sleep = (int)(Math.random()*6+5);
		//通过Client获取一个页面
		HtmlPage page;
		try {
			Thread.sleep(1000);
			page = webClient.getPage(pageUrl);
		
			//新建cleaner
			HtmlCleaner cleaner = new HtmlCleaner();
	        //处理原始的返回文档，替换掉javascript元素，以便下面的clean工作
	        String strs = SpiderStringUtils.replaceJsContent(page.getDocumentElement().asXml());//page.getWebResponse().getContentAsString().replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");
			//clean成标准的html文档
	        TagNode contentNode = cleaner.clean(strs);
	        
	        Object[] nodes = contentNode.evaluateXPath("//div[@class='WB_text']");
	        Object[] nodesForTime = contentNode.evaluateXPath("//div[@class='WB_from']");
	        for(int i=1;i<nodes.length;i++){
	        	//WB_text 的 html 代码
	        	TagNode n = (TagNode)nodes[i];
	        	//WB_from 的 html 代码
	        	TagNode n1 = (TagNode)nodesForTime[i];
	        	TagNode time = ((n1.getElementsByName("a", false))[0]);
	        	String timeStr = time.getAttributeByName("title");
	        	
	        	String nodeType = n.getAttributeByName("node-type");
	        	if("feed_list_reason".equals(nodeType)){
	        		sb.append("     ↳");
	        	}
	        	
	        	sb.append("时间："+ timeStr);
	        	sb.append(" 内容："+ n.getText().toString().replaceAll(" ", ""));
	        	sb.append("\r\n");
	        	
	        }
	        
	        Thread.sleep(sleep*1000);
	        
	        //第一次滚动
	        String rollingUrlOne = SpiderUrlUtils.createRollingOneAddContentUrl(this.getDomainId(), pageId, this.getDomainAndUserId());
	        WebRequest requestOne = new WebRequest(new URL(rollingUrlOne), HttpMethod.GET);
	
	        WebResponse jsonOne = webClient.loadWebResponse(requestOne);
	        JSONObject jsonObj = JSONObject.fromObject(jsonOne.getContentAsString());
	        String data = (String)jsonObj.get("data");
	
	        Document docOne = Jsoup.parse(data);
	        Elements elementsOne = docOne.select("div.WB_text");
	        Elements elementsOneTime = docOne.select("div.WB_from");
	        
	        for(int i=0;i<elementsOneTime.size();i++){
	        	Element text = (Element)elementsOne.get(i);
	        	Element time = (Element)((Element)elementsOneTime.get(i)).getElementsByTag("a").get(0);
	        	String nodeType = text.attr("node-type");
	        	if("feed_list_reason".equals(nodeType)){
	        		sb.append("     ↳");
	        	}
	        	sb.append("时间："+time.attr("title"));
		        sb.append(" 内容："+text.text());
		        sb.append("\r\n");
	        }
	        
	        Thread.sleep(sleep*1000);
	        
	      //第二次滚动
	        String rollingUrlTwo = SpiderUrlUtils.createRollingTwoAddContentUrl(this.getDomainId(), pageId, this.getDomainAndUserId());
	        WebRequest requestTwo = new WebRequest(new URL(rollingUrlTwo), HttpMethod.GET);

	        WebResponse jsonTwo = webClient.loadWebResponse(requestTwo);
	        JSONObject jsonObj2 = JSONObject.fromObject(jsonTwo.getContentAsString());
	        String data2 = (String)jsonObj2.get("data");

	        Document docTwo = Jsoup.parse(data2);
	        Elements elementsTwo = docTwo.select("div.WB_text");
	        
	        Elements elementsTwoTime = docTwo.select("div.WB_from");
	        
	        for(int i=0;i<elementsTwoTime.size();i++){
	        	Element text = (Element)elementsTwo.get(i);
	        	Element time = (Element)((Element)elementsTwoTime.get(i)).getElementsByTag("a").get(0);
	        	
	        	String nodeType = text.attr("node-type");
	        	if("feed_list_reason".equals(nodeType)){
	        		sb.append("     ↳");
	        	}
	        	
	        	sb.append("时间："+time.attr("title"));
		        sb.append(" 内容："+text.text());
		        sb.append("\r\n");
	        }
	        
	        return sb.toString();
	        
			} catch (FailingHttpStatusCodeException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XPatherException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}
	
	
	
	
	

}
