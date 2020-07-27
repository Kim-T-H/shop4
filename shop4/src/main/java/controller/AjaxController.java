package controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import logic.ShopService;

@RestController //@Controller + @ResponseBody ���
				//@ResponseBody : View ���� ���� �����͸� Ŭ���̾�Ʈ�� ����

@RequestMapping("ajax")  
public class AjaxController {
	@Autowired
	ShopService service;
	@RequestMapping(value="graph1",
				produces="text/plain; charset=UTF8")
	public String graph1() {
		//map : ("ȫ�浿", 10), ("���", 8)...
		System.out.println("=====================================================");
		Map<String, Object> map = service.graph1();
		
		/*
		 * json:[ {"name":"ȫ�浿","cnt":"10"},
		 * 		{"name":"���","cnt":"8"}]
		 * 
		 */
		
		StringBuilder json = new StringBuilder("[");
		int i=0;
		//json ������ ���ڿ��� �����ϱ�
		for(Map.Entry<String, Object> me: map.entrySet()) {
			json.append("{\"name\":\""+me.getKey() + "\","
					+ "\"cnt\":\""+me.getValue()+"\"}");
			i++;
			if(i<map.size())
				json.append(",");
		}
		json.append("]");
		return json.toString();	//Ŭ���̾�Ʈ�� ���޵� ������
	}
	
	@RequestMapping(value="graph2",
			produces="text/plain; charset=UTF8")
	public String graph2() {
		Map<String,Object> map=service.graph2();
		System.out.println(map);
		StringBuilder json =  new StringBuilder("[");
		int i=0;
		for(Map.Entry<String, Object> me: map.entrySet()) {
			json.append("{\"regdate\":\""+me.getKey() + "\","
					+ "\"cnt\":\""+me.getValue()+"\"}");
			i++;
			if(i<map.size())
				json.append(",");
		}
		json.append("]");
		System.out.println(json);
		return json.toString();	//Ŭ���̾�Ʈ�� ���޵� ������
	}
	
	@RequestMapping(value="exchange1",produces="text/html; charset=UTF8")
	public String exchange1() {
		String url="https://www.koreaexim.go.kr/site/program/financial/exchange?menuid=001001004002001";
		Document doc=null;
		List<String> list = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		try {
			doc = Jsoup.connect(url).get();
			Elements e1 = doc.select(".tc");// �����ڵ�,ȯ���� �±� ����
			Elements e2 = doc.select(".tl2.bdl"); //�����̸�
			for(int i=0; i<e1.size(); i++) {
				if(e1.get(i).html().equals("USD")) {
					list.add(e1.get(i).html()); //USD ��������
					for(int j=1; j<=6; j++) {
						list.add(e1.get(i+j).html());
					}
					break;
				}
			}
			for(Element ele : e2) {
				if(ele.html().contains("�̱�")) {
					list2.add(ele.html());
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		String today= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		StringBuilder html=new StringBuilder();
		html.append("<table class='w3-table-all'>");
		html.append("<caption>����������:" +today + "</caption>");
		html.append("<tr><td colspan='3' class='w3-center'>"+
					list2.get(0)+":" + list.get(0)+"</td></tr>");
		html.append("<tr><th>������</th><th>������</th><th>�ĽǶ�</th></tr>");
		html.append("<tr><td>"+list.get(3)+"</td>");
		html.append("<td>"+list.get(1)+"</td>");
		html.append("<td>"+list.get(2)+"</td></tr></table>");
		return html.toString();
		
	} 
	
	@RequestMapping(value="exchange2",produces="text/html; charset=UTF8")
	public String exchange2() {
		Map<String,List<String>> map = new HashMap<>();
		StringBuilder html=new StringBuilder();
		try {
		String kebhana=Jsoup.connect("http://fx.kebhana.com/FER1101M.web").get().text();
		String strJson= kebhana.substring(kebhana.indexOf("{"));		//indexof ���� �� �ٰ�����
		
		System.out.println(strJson);
		JSONParser jsonParser = new JSONParser();	//JSON ������
		JSONObject json = (JSONObject)jsonParser.parse(strJson.trim());	//Json ��ü ����
		JSONArray array = (JSONArray)json.get("����Ʈ");
		for(int i=0; i<array.size(); i++){
			JSONObject obj=(JSONObject)array.get(i);
			if(obj.get("��ȭ��").toString().contains("�̱�")||
				obj.get("��ȭ��").toString().contains("�Ϻ�")||
				obj.get("��ȭ��").toString().contains("����")||
				obj.get("��ȭ��").toString().contains("�߱�")){
				String str=obj.get("��ȭ��").toString();
				String[] sarr=str.split(" ");
				String key=sarr[0];
				String code=sarr[1];
				List<String> list=new ArrayList<String>();
				list.add(code);
				list.add(obj.get("�Ÿű�����").toString());
				list.add(obj.get("�����ĽǶ�").toString());
				list.add(obj.get("������Ƕ�").toString());
				map.put(key,list);
		}
	}
		html.append("<table class='w3-table-all'>");
		html.append("<caption>KEB�ϳ�����("+json.get("��¥").toString()+")</caption>");
		html.append("<tr><th rowspan='2'"
					+"style='vertical-align: middle;'>�ڵ�</th>");
		html.append("<th rowspan='2' style='vertical-align:middle;'>������</th>");
		html.append("<tr><th>�Ľ� ��</th><th>��� ��</th></tr>");
		for(Map.Entry<String, List<String>> m:map.entrySet()) {
			html.append("<tr><td class='w3-center'>"
					+m.getKey()+"("+m.getValue().get(0)+")</td>");
			html.append("<td>"+m.getValue().get(1)+"</td><td>"+m.getValue().get(2)+"</td><td>"
					+m.getValue().get(3)+"</td><tr>");
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return html.toString();
	
	}
}
