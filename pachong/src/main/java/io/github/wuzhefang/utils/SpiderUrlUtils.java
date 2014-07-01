package io.github.wuzhefang.utils;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

public class SpiderUrlUtils {
	public static String getMoreButtonHref(TagNode contentNode) throws XPatherException{
		Object[] as = contentNode.evaluateXPath("//a[@class='PRF_feed_list_more SW_fun_bg S_line2']");       
//        for(Object ob:as){
//        	TagNode he = (TagNode)ob;
//        	System.out.println(he.getAttributeByName("href"));
//        }
		if(as.length==1){
			TagNode tn = (TagNode)as[0];
			return tn.getAttributeByName("href");
		}else{
			return null;
		}
	}
	
		//微博内容第一次滚动加载请求url
		public static String createRollingOneAddContentUrl(String domainId,int pageNum,String userId){
		return "http://weibo.com/p/aj/mblog/mbloglist?domain="+domainId+"&pre_page="+pageNum+"&page="+pageNum+"&pagebar=0&id="+userId;
		}
		//微博内容第二次滚动加载请求url
		public static String createRollingTwoAddContentUrl(String domainId,int pageNum,String userId){
		return "http://weibo.com/p/aj/mblog/mbloglist?domain="+domainId+"&pre_page="+pageNum+"&page="+pageNum+"&pagebar=1&id="+userId+"&script_uri=/p/"+userId+"/weibo";
		}
	
	
}
