package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import exception.LoginException;
import logic.Mail;
import logic.ShopService;
import logic.User;

/*
 * AdminController Ŭ������ ��� �޼���� admin ���� �α��� �� ��츸 ó�� ������.
 * 1.�α��� �ȵ� ���: �α��� �� �����մϴ�. login.shop ������ �̵�
 * 2.������ �α����� �ƴ� ��� : �����ڸ� ������ �ŷ��Դϴ�. main.shop ������ �̵�   => AdminAspect Ŭ���� �����ϱ�
 * 
 */




@RequestMapping("admin") 
@Controller
public class AdminController {
	@Autowired
	private ShopService service;
	
	@GetMapping("list")
	public ModelAndView list(HttpSession session) {
		ModelAndView mav= new ModelAndView();
		List<User> list = service.getlistAll();
		System.out.println(list);
		mav.addObject("list", list); //    "list"   :     list.jsp ��  forEach items �� "${list}"
		
		return mav;
		
	}
	
	@RequestMapping("mailForm")
	public ModelAndView mailForm(String[] idchks, HttpSession session) {
		ModelAndView mav= new ModelAndView("admin/mail");
		if(idchks==null || idchks.length==0) {
			throw new LoginException("������ ���� ����ڸ� �����ϼ���","list.shop");
		}
		//list : ���õ� userid�� �ش��ϴ� User ��ü��
		List<User> list=service.userList(idchks);
		mav.addObject("list",list);
		return mav;
	}
	
	@RequestMapping("mail")
	public ModelAndView mail(Mail mail, HttpSession session) {
		ModelAndView mav= new ModelAndView("/alert");
		mailSend(mail);
		mav.addObject("msg","���� ������ �Ϸ� �Ǿ����ϴ�.");
		mav.addObject("url","list.shop"); 
		return mav;
	}
	
	private final class MyAuthenticator extends Authenticator{
		private String id;
		private String pw;
		public MyAuthenticator(String id, String pw) {
			this.id= id;
			this.pw = pw;
		}
		
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(id,pw);
		}
	}
	
	private void mailSend(Mail mail) {
		//���̹� ���� ������ ���� ���� ��ü
		MyAuthenticator auth=new MyAuthenticator(mail.getNaverid(),mail.getNaverpw());
		//���� ������ ���� ȯ�� ���� ����
		Properties prop= new Properties();
		
		try {
			FileInputStream fis= new FileInputStream("C:\\Users\\GDJ24\\git\\shop\\shop3\\src\\main\\resources\\mail.properties");
			prop.load(fis);	//mail.properties  �� ������ Properties(Map) ��ü��  
			prop.put("mail.smtp.user", mail.getNaverid());
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		//���� ������ ���� ��ü
		Session session=Session.getInstance(prop,auth);
		//������ ������ �����ϱ� ���� ��ü
		MimeMessage mimeMsg = new MimeMessage(session);
		try {
			//������ ��� ����
			mimeMsg.setFrom(new InternetAddress(mail.getNaverid()+"@naver.com"));
			List<InternetAddress> addrs=new ArrayList<InternetAddress>();
			//ȫ�浿<hong@aaa.bbb>, ���<kim@aaa.bbb>,..
			String[] emails = mail.getRecipient().split(",");
			/*
			 * new String(email.getBytes("utf-8"),"8859_1")
			 * email.getBytes("utf-8"): email ���ڿ��� byte[] ���·� ����.  utf -8 ���ڷ� �ν�
			 * 
			 * 8859_1: byte [] �迭�� 8859_1�� �����Ͽ� ���ڿ��� ���� => ���ŵ� ���Ͽ��� �ѱ��̸� �����ǵ��� ����
			 * 
			 */
			
			
			
			for(String email: emails) {
				try {
					addrs.add(new InternetAddress(new String(email.getBytes("utf-8"),"8859_1")));
				}catch(UnsupportedEncodingException ue) {
					ue.printStackTrace();
				}
			}
			InternetAddress[] arr=
					new InternetAddress[emails.length];
			for(int i=0; i<addrs.size();i++) {
				arr[i]= addrs.get(i);
			}
			mimeMsg.setSentDate(new Date());  //���� ����
			mimeMsg.setRecipients(Message.RecipientType.TO, arr);		// �޴»��
			mimeMsg.setSubject(mail.getTitle());		//����
			MimeMultipart multipart = new MimeMultipart();  
			MimeBodyPart message=new MimeBodyPart();
			
			message.setContent(mail.getContents(),mail.getMtype());
			multipart.addBodyPart(message);
			
			//÷������ �κ�
			for(MultipartFile mf: mail.getFile1()) {
				if((mf !=null) && (!mf.isEmpty())) {
					multipart.addBodyPart(bodyPart(mf));
				}
			}
			mimeMsg.setContent(multipart);
			Transport.send(mimeMsg);
			
		} catch(MessagingException me) {
			me.printStackTrace();
		}
		
	}

	private BodyPart bodyPart(MultipartFile mf) {
		MimeBodyPart body= new MimeBodyPart();
		
		//���ε�� ������ �̸�
		String orgFile= mf.getOriginalFilename();
		String path="d:/20200224/spring/mailupload/";
		File f = new File(path);
		if(!f.exists()) f.mkdirs();
		File f1=new File(path+orgFile);		//���ε�� ������ �����ϴ� ����
		try {
			mf.transferTo(f1);	//���ε� �ϼ�
			body.attachFile(f1);	//���Ͽ� ÷��
			body.setFileName(new String(orgFile.getBytes("UTF-8"),"8859_1"));	//÷�������̸�����
		}catch(Exception e) {
			e.printStackTrace();
		}
		return body;
	}
	
	@RequestMapping("graph*")
	public ModelAndView graph(HttpSession session) {
		ModelAndView mav= new ModelAndView();
		Map<String, Object> map =service.graph1();
		mav.addObject("map",map);
		return mav;
	}
	
}
