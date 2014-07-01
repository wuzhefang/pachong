package io.github.wuzhefang.utils;

public class SpiderStringUtils {
	
	public static String replaceJsContent(String content){
		
		String[] strstest = {"<script>","<script type=\"text/javascript\">","<!\\[CDATA\\[","]]>","</script>","\\\\t","\\\\n","\\\\r","\\\\","<script src="};
		for(String a : strstest){
			content = content.replaceAll(a, "");
		}
		return content;
	}
}
