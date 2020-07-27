package controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import exception.BoardException;
import logic.Board;
import logic.ShopService;

@Controller
@RequestMapping("board")
public class BoardController {

	@Autowired
	ShopService service;
	
	
	@GetMapping("*")
	public ModelAndView getBoard(Integer num, HttpServletRequest request) {
		ModelAndView mav=new ModelAndView();
		//Board board= service.getdatail(num);
		Board board=null;
		if(num==null) {	//num �Ķ���Ͱ� ���� ���
			board=new Board();
		}else {
			boolean readcntable=false;
			if(request.getRequestURI().contains("detail.shop"))
				readcntable=true;
			board=service.getBoard(num,readcntable);		//board: �Ķ���� num�� �ش��ϴ� �Խù� ���� ����
			System.out.println("===============================================================================================");
		}
		System.out.println("board: "+board);
		mav.addObject("board",board);
		return mav;
	}
	
	@PostMapping("write")
	public ModelAndView write(@Valid Board board,BindingResult bresult, HttpServletRequest request) throws BoardException {
		ModelAndView mav= new ModelAndView();
		if(bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		try {
			service.boardWrite(board,request);
			mav.setViewName("redirect:list.shop");  
		}catch(Exception e) {
			e.printStackTrace();
			throw new BoardException("�Խù� ��Ͽ� ���� �߽��ϴ�.","write.shop");
		}
		return mav;
	}
	
	/*
	 * pageNum: ���� ������ ��ȣ
	 * maxpage: �ִ� ������
	 * startpage:�������� ���� ��������ȣ
	 * endpage: �������� �� ��������ȣ
	 * listcount:��ü ��ϵ� �Խù� �Ǽ�
	 * boardlist:ȭ�鿡 ������ �Խù� ��ü��
	 * boardno: ȭ�鿡 �������� �Խù� ��ȣ
	 */
	
	@RequestMapping("list")
	public ModelAndView list(Integer pageNum,String searchtype, String searchcontent, HttpSession session) {
		ModelAndView mav=new ModelAndView();
		if(pageNum==null|| pageNum.toString().equals("")) {
			pageNum=1;
		}
		
		if(searchtype ==null || searchcontent==null ||
				searchtype.trim().equals("") || searchcontent.trim().equals("")) {
			searchtype=null;
			searchcontent=null;
		}
		
		
		int limit=10; //���������� ������ �Խù��� �Ǽ�
		int listcount=service.boardcount(searchtype, searchcontent);//��� �Խù��Ǽ�
		List<Board> boardlist=service.boardlist(pageNum,limit,searchtype, searchcontent);
		int maxpage=(int)((double)listcount/limit+0.95);
		int startpage=(int)((pageNum/10.0+0.9)-1)*10+1;
		int endpage=startpage+9;
		if(endpage>maxpage) endpage=maxpage;
		int boardno= listcount-(pageNum-1)*limit;
		System.out.println(boardlist);
		
		
		mav.addObject("pageNum", pageNum);
		mav.addObject("maxpage",maxpage);
		mav.addObject("startpage",startpage);
		mav.addObject("endpage", endpage);
		mav.addObject("listcount",listcount);
		mav.addObject("boardlist",boardlist);
		mav.addObject("boardno",boardno);
		mav.addObject("today", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		return mav;
	}
//	@RequestMapping("detail")
//	public ModelAndView detail(Integer num, HttpServletRequest request) {
//		ModelAndView mav=new ModelAndView();
//		Board board= service.getdetail(num);
//		mav.addObject("board",board);
//		return mav;
//	}
	
	@RequestMapping("imgupload")
	
	//upload : ckeditor ���� ������ �ִ� ���� ������ �̸�
	//		<input type="file" name="upload">
	//CKEditorFuncNum:ckeditor���� ���ο��� ���Ǵ� �Ķ����
	public String imgupload(MultipartFile upload,String CKEditorFuncNum,HttpServletRequest request,Model model) {
		String path=request.getServletContext().getRealPath("/")+"board/imgfile/";	//�̹����� ������ ������ ����
		File f=new File(path);
		if(!f.exists()) f.mkdirs();
		if(!upload.isEmpty()) {
			File file= new File(path,upload.getOriginalFilename()); //���ε�� ������ ������ File ��ü ����
			try {
				upload.transferTo(file);	//���ε� ���ϻ���
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		String fileName="/shop3/board/imgfile/"+upload.getOriginalFilename();
		model.addAttribute("fileName",fileName);
		model.addAttribute("CKEditorFuncNum", CKEditorFuncNum);
		return "ckedit";
	}
	
	/*
	 * 1.�Ķ���� ��  Board ��ü ����. ��ȿ�� ����
	 * 2.�亯�� ������ �߰� => service ��ó ó��
	 * 		- grp �� �ش��ϴ� ���ڵ� grpstep ������ ū  grpstep �� ���� grpstep+1
	 * 		- maxnum+1������ num ���� ����
	 * 		- grplevel +1 ������ grplevel ���� ����
	 * 		- grpstep +1 ������ grpstep ���� ����
	 * 		- �Ķ���Ͱ����� board ���̺� insert�ϱ�
	 * 3. list.shop ������ ��û
	 * 
	 */
	
	@PostMapping("reply")
	public ModelAndView reply(@Valid Board board,BindingResult bresult) {
		ModelAndView mav=new ModelAndView();
		
		//��ȿ�� ���� �κ�
		if(bresult.hasErrors()) {
			Board dbBoard=service.getBoard(board.getNum(),false);
			Map<String,Object> map= bresult.getModel();
			Board b= (Board)map.get("board");
			b.setSubject(dbBoard.getSubject()); //������ ������ �ʰ� ����
			return mav;
		}
		
		
		try {
			//ȭ�鿡�� ���� ���� �Ķ���� ���� ����
			/*
			 * num,grp,grplevel,grpstep : ���� ����
			 * name,subject,content,pass : �Է��� ������ ������ ��ۿ� ����� ����
			 */
			service.boardReply(board);
			mav.setViewName("redirect:list.shop");  
		}catch(Exception e) {
			e.printStackTrace();
			throw new BoardException("�亯�� ��Ͽ� ���� �߽��ϴ�.","reply.shop?num="+board.getNum());
		}
		return mav;
		
	}
	
	/*
	 * 1.�Ķ���� �� Board ��ü ����.��ȿ�� ����
	 * 2.�Էµ� ��й�ȣ�� db�� ��й�ȣ�� �� ��й�ȣ�� �´� ���  3������
	 * 	Ʋ����� '��й�ȣ�� Ʋ���ϴ�.', update.shop  Get������� ȣ��
	 * 3.���� ������ db�� ����
	 * 	-÷�� ���� ����: ÷������ ���ε�, fileurl ���� ����
	 * 4.detail.shop ������ ��û
	 * 
	 */
	
	@PostMapping("update")
	public ModelAndView update( @Valid Board board,BindingResult bresult, HttpServletRequest request) {
		ModelAndView mav= new ModelAndView();
		Board dbBoard= service.getBoard(board.getNum(),false);
		
		
		if(bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		if(!board.getPass().equals(dbBoard.getPass()))
			throw new BoardException("��й�ȣ�� Ʋ�Ƚ��ϴ�.", "update.shop?num="+board.getNum());
		try {
			service.boardUpdate(board, request);
			mav.setViewName("redirect:detail.shop?num="+board.getNum());
		}catch(Exception e) {
			e.printStackTrace();
			throw new BoardException("�Խù� ���� �����Դϴ�.", "update.shop?num="+board.getNum());
		}
		
		return mav;
	}
	
	/*
	 * 1.num, pass �Ķ���� ����
	 * 2.db�� ��й�ȣ�� �Էµ� ��й�ȣ�� Ʋ����  error.login.password
	 * 		�ڵ� �Է�=> ��ȿ�� ���� ���� ����ϱ�
	 * 3.db���� �ش� �Խù� ����
	 * 	���� ����: �Խñ� ���� ����. delete.shop ������ �̵�
	 * 	���� ����: list.shop ������ �̵�
	 * 
	 */
	
	@PostMapping("delete")
	public ModelAndView delete(Board board,BindingResult bresult) {
		ModelAndView mav= new ModelAndView();
		try {
			Board dbboard=service.getBoard(board.getNum(), false);  //false? =>getboard�� boolean able ������
		if(!board.getPass().equals(dbboard.getPass())) {
			bresult.reject("error.login.password"); //�۷ι� ����
			return mav;
		}
			service.boardDelete(board);
			mav.setViewName("redirect:list.shop");
		}catch(Exception e) {
			e.printStackTrace();
			throw new BoardException("�Խù� ���� ����.", "delete.shop?num="+board.getNum());
		}
	return mav;
	}
	
	
}
