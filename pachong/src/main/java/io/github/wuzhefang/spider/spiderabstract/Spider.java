package io.github.wuzhefang.spider.spiderabstract;

import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;

import io.github.wuzhefang.spider.SpiderInterface;

public abstract class Spider implements SpiderInterface {
	
	private String domainId="";
	
	private String domainName="";
	
	private String userId=""; 
	
	private String domainAndUserId="";
	

	abstract public WebClient getWebClient();
	
	abstract public List<String> getPageUrlList(WebClient webClient);
	
	abstract public String loadPage(WebClient webClient,String pageUrl,int pageId);

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDomainAndUserId() {
		return domainAndUserId;
	}

	public void setDomainAndUserId(String domainAnduserId) {
		this.domainAndUserId = domainAnduserId;
	}

}
