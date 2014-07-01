package io.github.wuzhefang;

import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.WebClient;

import io.github.wuzhefang.spiderImpl.WeiboSpider;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest  extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	//1825082937
    	WeiboSpider weibo = new WeiboSpider("100505","weibo.com","2714073794");
    	WebClient webClient = weibo.getWebClient();
    	ArrayList<String> l = (ArrayList<String>)weibo.getPageUrlList(webClient);
    	for(int i=0;i<l.size();i++){
    		String url = l.get(i);
    		String pageContent = weibo.loadPage(webClient, url, i+1);
    		weibo.writeFile(pageContent,"/Users/signv/page"+(i+1)+".txt");
//    		break;
    	}
//    	weibo.getContent();
//        assertTrue( true );
    }
}
