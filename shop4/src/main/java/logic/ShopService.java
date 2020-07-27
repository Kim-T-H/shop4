package logic;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



import dao.ItemDao;
import dao.UserDao;
import dao.boardDao;
import dao.SaleDao;
import dao.SaleItemDao;


@Service // @Component +service(Controller �� dao �߰� ����)
public class ShopService {

	@Autowired
	private ItemDao itemDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SaleDao saleDao;
	
	@Autowired
	private boardDao boardDao;
	
	@Autowired
	private SaleItemDao saleItemDao;

	public List<Item> getItemList() {
		return itemDao.list();
	}

	public Item getItem(Integer id) {

		return itemDao.selectOne(id);
	}

	// ���� ���ε�, dao�� ������ ����
	public void itemCreate(Item item, HttpServletRequest request) {
		if (item.getPicture() != null && !item.getPicture().isEmpty()) {

			uploadFileCreate(item.getPicture(), request, "img/");
			item.setPictureUrl(item.getPicture().getOriginalFilename());
		}

		itemDao.insert(item);
	}

	private void uploadFileCreate(MultipartFile picture, HttpServletRequest request, String path) { // picture : ������ ����
																									// ����
		String orgFile = picture.getOriginalFilename();
		String uploadPath = request.getServletContext().getRealPath("/") + path;
		File fpath = new File(uploadPath);
		if (!fpath.exists())
			fpath.mkdirs();
		try {
			// ������ ������ ���� ���Ϸ� ����
			picture.transferTo(new File(uploadPath + orgFile));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void itemUpdate(Item item, HttpServletRequest request) {
		if (item.getPicture() != null && !item.getPicture().isEmpty()) {

			uploadFileCreate(item.getPicture(), request, "img/");
			item.setPictureUrl(item.getPicture().getOriginalFilename());
		}

		itemDao.update(item);

	}

	public void itemdelete(String id) {
		itemDao.delete(id);
		
	}

	public void userInsert(User user) {
		userDao.insert(user);   
		
	}

	public User getUser(String userid) {
		
		return userDao.selectOne(userid);
	}

	
	/*
	 * db�� sale ������ saleitem ���� ����. ����� ������  Sale ��ü�� ����
	 * 1. sale ���̺��� saleid ���� ���� => �ִ밪+1 
	 * 2. sale�� ���� ����. => insert
	 * 3. Cart �����κ��� SaleItem ���� ���� =>insert 
	 * 4. ��� ������  Sale ��ü�� ����
	 */
	
	public Sale checkend(User loginUser, Cart cart) {
		Sale sale= new Sale();
		int maxno=saleDao.getMaxSaleid();  
		sale.setSaleid(++maxno);
		sale.setUser(loginUser);
		sale.setUserid(loginUser.getUserid());
		saleDao.insert(sale);
		List<ItemSet> itemList=cart.getItemSetList(); //Cart ��ǰ����
		int i=0;
		for(ItemSet is : itemList) {
			int seq=++i;
			SaleItem saleItem= new SaleItem(sale.getSaleid(),seq,is);
			sale.getItemList().add(saleItem);
			saleItemDao.insert(saleItem); 
		}
		return sale;
	}

	public List<Sale> salelist(String id) {
		
		return saleDao.list(id); //����� id
	}

	public List<SaleItem> saleItemList(int saleid) {
		
		return saleItemDao.list(saleid); //saleid �ֹ���ȣ
	}

	public void userUpdate(User user) {
		userDao.update(user);
		
	}

	public void userdelete(String userid) {
		userDao.delete(userid);
		
	}

	public List<User> getlistAll() {
		return userDao.getlistAll();
	}

	public List<User> userList(String[] idchks) {
		
		return userDao.list(idchks); 
	}

	public void boardWrite(Board board, HttpServletRequest request) {
		if(board.getFile1() !=null && !board.getFile1().isEmpty()) {
			uploadFileCreate(board.getFile1(), request, "board/file/");
			board.setFileurl(board.getFile1().getOriginalFilename());
		}
		int max=boardDao.maxnum();
		board.setNum(++max);
		board.setGrp(max);
		boardDao.insert(board); 
		
	}

	public int boardcount(String searchtype,String searchcontent) {
		return boardDao.count(searchtype,searchcontent);
	}

	public List<Board> boardlist(Integer pageNum, int limit, String searchtype,String searchcontent) {
		return boardDao.list(pageNum,limit,searchtype,searchcontent);
	}

	public Board getBoard(Integer num, boolean able) {
		if(able) {
			boardDao.readcntadd(num);
		}
		return boardDao.selectOne(num);
	}

	public void boardReply(Board board) {
		boardDao.updateGrpstep(board);	//���� ����� grpstep ����.
		int max=boardDao.maxnum();
		board.setNum(++max);
		board.setGrplevel(board.getGrplevel()+1);
		board.setGrpstep(board.getGrpstep()+1);
		boardDao.insert(board);
		
	}

	public void boardUpdate(Board board, HttpServletRequest request) {
		
		//���ε� ������ �����ϴ� ���
		if(board.getFile1() !=null && !board.getFile1().isEmpty()) {
			uploadFileCreate(board.getFile1(), request, "board/file/");
			board.setFileurl(board.getFile1().getOriginalFilename());
		}
		boardDao.boardupdate(board);
	}

	public void boardDelete(Board board) {
		boardDao.delete(board.getNum());
		
	}

	public Map<String, Object> graph1() {
		Map<String, Object> map=new HashMap<String, Object>();
		for(Map<String, Object> m: boardDao.graph1()) {
			map.put((String)m.get("name"), m.get("cnt"));
		}
		return map;
	}

	public Map<String, Object> graph2() {
		Map<String,Object> map=new HashMap<String, Object>();
		for(Map<String, Object> m: boardDao.graph2()) { 
			map.put((String)m.get("regdate"),m.get("cnt"));
		}
		return map;
	}
	

//	public void boardDelete(Integer num) {
//		boardDao.boarddelete(num);
//		
//	}

//	public Board getdetail(Integer num) {
//		boardDao.updatereadCnt(num);
//		return boardDao.detail(num);
//	}

	

}