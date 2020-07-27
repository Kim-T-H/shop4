package controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import exception.CartEmptyException;
import logic.Item;
import logic.ShopService;

@Controller // @Component + controller(��û�� ���� �� �ִ� ��ü)   ��Ʈ�ѷ��� ������Ʈ�� ���� Ŭ����
@RequestMapping("item")	// /item /��û��  
public class ItemController {

	@Autowired
		private ShopService service; 
	
	@RequestMapping("list")	//	/item/list.shop
	public ModelAndView list() {
		ModelAndView mav = new ModelAndView();
		List<Item> itemList = service.getItemList();
		mav.addObject("itemList",itemList);
		return mav;
	}
	
	@GetMapping("*") // /item/ *.shop
	public ModelAndView detail(Integer id) {
		 ModelAndView mav=new ModelAndView();
		 Item item = service.getItem(id);
		 System.out.println(item);
		 if(item == null) throw new CartEmptyException("�ش��ǰ�� �����ϴ�.", "list.shop");
		 mav.addObject("item",item);
		 return mav;
	}
	
	@RequestMapping("create")	// /item.create.shop
	public String addform(Model model) {
		model.addAttribute(new Item());
		return "item/add";
		
//		ModelAndView mav= new ModelAndView();
//		mav.addObject("item", new Item());		//��ü�� �־� ��ߵ�   new Item()
//		mav.setViewName("/item/add");		
//		return mav;
	}
	
	@RequestMapping("register")
	public ModelAndView add(@Valid Item item, BindingResult bresult, HttpServletRequest request) {
		ModelAndView mav =new ModelAndView("item/add");
		if(bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		service.itemCreate(item,request);
		mav.setViewName("redirect:/item/list.shop");
		return mav;
	}
	
	@PostMapping("update")
	public ModelAndView update(@Valid Item item, BindingResult bresult, HttpServletRequest request) {
	ModelAndView mav =new ModelAndView("item/add");
	if(bresult.hasErrors()) {
		mav.getModel().putAll(bresult.getModel());
		return mav;
	}
	//db,���Ͼ��ε�
	service.itemUpdate(item,request);
	mav.setViewName("redirect:/item/detail.shop?id="+item.getId());
	return mav;

			
}
	
	@RequestMapping("delete")
	public ModelAndView itemdelete(String id) {	//itemdelete(String id) => id��� �Ķ���͸� ����.
		ModelAndView mav =new ModelAndView();
		service.itemdelete(id);
		mav.setViewName("redirect:/item/list.shop");
		return mav;
	}
	
}
	
