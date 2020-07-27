package controller;

import java.util.List;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import dao.UserDao;
import exception.LoginException;
import logic.Item;
import logic.Sale;
import logic.SaleItem;
import logic.ShopService;
import logic.User;

@Controller
@RequestMapping("user")
public class UserController {
	@Autowired
	
	private ShopService service;

	@Autowired
	private UserDao userDao;
	
	

	@GetMapping("*")
	public String form(Model model) {
		model.addAttribute(new User());
		return null;
	}

	@PostMapping("userEntry")
	public ModelAndView join(@Valid User user, BindingResult bresult) {
		ModelAndView mav = new ModelAndView();
		if (bresult.hasErrors()) {
			bresult.reject("error.input.user");
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		try {
			service.userInsert(user);
			mav.setViewName("redirect:login.shop");
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			bresult.reject("error.duplicate.user");
			mav.getModel().putAll(bresult.getModel());
		}
		return mav;
	}

	@PostMapping("login")
	public ModelAndView login(@Valid User user, BindingResult bresult, HttpSession session) {
		System.out.println("================================================================");
		ModelAndView mav = new ModelAndView();
		System.out.println(bresult);
		if (bresult.hasErrors()) {
			bresult.reject("error.input.user");
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		// 1.db ������ id, password ��
		// 2.��ġ: session�� loginUser ���� ����
		// 3. ����ġ: ��й�ȣ Ȯ�� ���� ���.
		// 4.db�� �ش� id ������ ���� ���
		try {
			User userinfo=service.getUser(user.getUserid());
			if(user.getPassword().contentEquals(userinfo.getPassword())) {
				session.setAttribute("loginUser", userinfo);
				mav.setViewName("redirect:main.shop");
			}else bresult.reject("error.login.password");
		}catch(Exception e) {
			e.printStackTrace();
			bresult.reject("error.login.id");
		}
		return mav;
	}
	
	@RequestMapping("logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:login.shop";
	}
	
	@RequestMapping("main")	//login   �Ǿ�� ���డ��. �޼����̸��� loginxxx �� ����
	public String loginCheck(HttpSession session) {
		return null;
	}
	
	
	@RequestMapping("mypage")
	public ModelAndView checkmypage(String id, HttpSession session) {
		ModelAndView mav= new ModelAndView();
		User user = service.getUser(id);
		//sale ���̺��� saleid,userid,saledate �÷����� ����� Sale ��ü�� List �� ����
		List<Sale> salelist = service.salelist(id); 
		for(Sale sa: salelist) {
			int sum = 0;
			List<SaleItem> saleitemlist = service.saleItemList(sa.getSaleid());
			for(SaleItem si : saleitemlist) {
				Item item= service.getItem(Integer.parseInt(si.getItemid()));
				si.setItem(item);
				sum += (si.getQuantity() * si.getItem().getPrice());
			}
			sa.setItemList(saleitemlist);
			sa.setTotal(sum);
		}
		System.out.println(salelist);
		mav.addObject("user",user);
		mav.addObject("salelist",salelist);
		return mav;
	}
	
	@GetMapping(value={"update","delete"})
	public ModelAndView checkview(String id,HttpSession session) {
		ModelAndView mav=new ModelAndView();
		User user = service.getUser(id);
		mav.addObject("user",user);
		return mav;
	}
	/*
	 * 1.��ȿ������
	 * 2.��й�ȣ ����: ����ġ
	 * 		��ȿ�� ������� error.login.password �ڵ�� ����
	 * 3.��й�ȣ ��ġ
	 * 		update ����.
	 * 		�α������� ����. �� admin�� �ٸ������ ���� �����ô� �α��� ���� �����ȵ�.
	 */
	@PostMapping("update")
	public ModelAndView update(@Valid User user, BindingResult bresult, HttpSession session) {
		System.out.println("================================================================");
		ModelAndView mav = new ModelAndView();
		System.out.println(bresult);
		//��ȿ�� ����
		if (bresult.hasErrors()) {
			bresult.reject("error.input.user");
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		// 1.db ������ id, password ��
		// 2.��ġ: session�� loginUser ���� ����
		// 3. ����ġ: ��й�ȣ Ȯ�� ���� ���.
		// 4.db�� �ش� id ������ ���� ���
		
		
		//��й�ȣ ����
		User loginUser  = (User)session.getAttribute("loginUser");
		//�α����� ������ ��й�ȣ�� �Էµ� ��й�ȣ ����
		if(!user.getPassword().contentEquals(loginUser.getPassword())) {
			bresult.reject("error.login.password");
			return mav;
		}
		//��й�ȣ ��ġ: ������ ����
		try {
			service.userUpdate(user);
			mav.setViewName("redirect:mypage.shop?id="+user.getUserid());
			if(loginUser.getUserid().equals(user.getUserid())) {
				session.setAttribute("loginUser", user);
			}
		}catch(IndexOutOfBoundsException e) {
			e.printStackTrace();
			bresult.reject("error.user.update");
		}
		return mav;
	}
	
	/*
	 * ȸ�� Ż��
	 * 1.��й�ȣ ���� ����ġ: "��й�ȣ ����" �޼��� ���. delete.shop �̵�
	 * 2.��й�ȣ ���� ��ġ:	ȸ�� db����  delete
	 * 				������ ��� : logout �ϰ� login.shop ������ ��û
	 * 				�������� ���: main.shop���� ������ �̵�
	 */
	
	@PostMapping("delete")
	public ModelAndView delete(String userid,String password ,HttpSession session) {
		ModelAndView mav= new ModelAndView();
		User loginUser  = (User)session.getAttribute("loginUser");
		if(userid.equals("admin")) {
			throw new LoginException("������ Ż��� �Ұ����մϴ�.","main.shop?id="+userid);
		}
		//������ �α��� : ������ ��й�ȣ ����
		//����� �α���: ���κ�й�ȣ ����
		
		if(!password.equals(loginUser.getPassword())) {
			throw new LoginException("ȸ��Ż��� ��й�ȣ�� Ʋ���ϴ�.","delete.shop?id="+userid);
		}
		
		System.out.println("===============================");
		System.out.println("userid: "+ userid);
		try {
			service.userdelete(userid);		//type�� Ȯ����. �̸��� Ȯ����������
		}catch(Exception e) {
			e.printStackTrace();
			throw new LoginException("ȸ�� Ż��� ������ �߻��߽��ϴ�. ����ο� �����ٶ��ϴ�.","delete.shop?id="+userid);
		}
		
		if(loginUser.getUserid().equals("admin")) {
			mav.setViewName("redirect:main.shop");
		}else {
		//session.invalidate();  //�α׾ƿ�
		throw new LoginException(userid+"ȸ���� Ż�� �Ǿ����ϴ�.","logout.shop");
		}
		return mav;
}
}
