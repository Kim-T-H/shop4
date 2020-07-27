package controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import exception.CartEmptyException;
import logic.Cart;
import logic.Item;
import logic.ItemSet;
import logic.Sale;
import logic.ShopService;
import logic.User;

@Controller
@RequestMapping("cart")
public class CartController {
	@Autowired
	private ShopService service;

	@RequestMapping("cartAdd")
	public ModelAndView add(Integer id, Integer quantity, HttpSession session) {
		ModelAndView mav = new ModelAndView("cart/cart");
		Item item = service.getItem(id); // ���õ� ��ǰ��ü
		Cart cart = (Cart) session.getAttribute("CART");

		if (cart == null) {
			cart = new Cart();
			session.setAttribute("CART", cart);
		}

//		boolean state = true;
//		
//		for(ItemSet i : cart.getItemSetList()) {
//			if(i.getItem().getId().equals(item.getId())) {
//				i.setQuantity(i.getQuantity() + quantity);
//				state = false;
//				break;
//			}
//		}

//		if(state) 
		cart.push(new ItemSet(item, quantity));
		mav.addObject("message", item.getName() + ":" + quantity + "�� ��ٱ��� �߰�");
		mav.addObject("cart", cart);
		return mav;
	}

	@RequestMapping("cartDelete")
	public ModelAndView delete(int index, HttpSession session) {
		ModelAndView mav = new ModelAndView("cart/cart");
		Cart cart = (Cart) session.getAttribute("CART");
		ItemSet itemset = null;
		System.out.println(cart);
		try {
			// List.remove(int) : index�� �ش��ϴ� ��ü�� ����
			// List.remove(Integer(Object)) : Object�� �ν��Ͽ� Object ��ü ����
			// itemset: List���� ���ŵ� ��ü ����
			itemset = cart.getItemSetList().remove(index);
			mav.addObject("message", itemset.getItem().getName() + " ��ǰ�� �����Ͽ����ϴ�.");
		} catch (Exception e) {
			mav.addObject("message", "��ٱ��� ��ǰ�� �������� �ʾҽ��ϴ�.");
		}

		mav.addObject("cart", cart);
		return mav;
	}

	@RequestMapping("cartView")
	public ModelAndView view(Integer id, HttpSession session) {
		ModelAndView mav = new ModelAndView("cart/cart");
		Cart cart = (Cart) session.getAttribute("CART");
		if (cart == null || cart.getItemSetList().size() == 0) {
			throw new CartEmptyException("��ٱ��Ͽ� ��ǰ�� �����ϴ�.", "../item/list.shop");
		}

		mav.addObject("cart", cart);
		return mav;
	}

	@RequestMapping("checkout")	//AOP Ŭ�������� ù��° �Ű������� ����ϹǷ� ù��° �Ű�������  HttpSession �̾����
	public ModelAndView checkout(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		Cart cart=(Cart) session.getAttribute("Cart");
		return mav;

	}
	
	@RequestMapping("end")
	public ModelAndView checkend(HttpSession session) {	//CartAspect ����
		ModelAndView mav = new ModelAndView();
		Cart cart = (Cart)session.getAttribute("CART");
		User loginUser = (User)session.getAttribute("loginUser");
		Sale sale=service.checkend(loginUser,cart);
		long total = cart.getTotal();
		session.removeAttribute("CART");	//�ֹ� �Ϸ�� ��ٱ��� ���� ����
		mav.addObject("sale",sale);
		mav.addObject("total",total);
		return mav;
	}

}
