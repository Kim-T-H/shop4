package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import dao.mapper.BoardMapper;
import logic.Board;



@Repository
public class boardDao {
	@Autowired
	private SqlSessionTemplate template;
	private Map<String,Object> param = new HashMap<>();
	private String boardcolumn="select num, name, pass, subject, content, file1 fileurl, regdate, readcnt, grp, grplevel, grpstep"
			+ " from board";

	public int maxnum() {
		
		return template.getMapper(BoardMapper.class).maxnum();
	}

	public void insert(Board board) {
		SqlParameterSource prop= new BeanPropertySqlParameterSource(board);
		String sql="insert into board (num,name,pass,subject,content,regdate,file1,readcnt,grp,grplevel,grpstep)"
				+ " values (:num,:name,:pass,:subject,:content,now(),:fileurl,0,:grp,:grplevel,:grpstep)";
		template.getMapper(BoardMapper.class).insert(board);
		
	}

	public int count(String searchtype,String searchcontent) {
		String sql="select count(*) from board";
		if(searchtype != null && searchcontent !=null) {
			sql+=" where " + searchtype + " like :searchcontent";
		}
		param.clear();
		param.put("searchtype", searchtype);
		param.put("searchcontent",searchcontent);
		return template.getMapper(BoardMapper.class).count(param);
		
	}

	public List<Board> list(Integer pageNum, int limit, String searchtype,String searchcontent) {
		String boardcolumn="select num,name,pass,subject,content,file1 fileurl,regdate,readcnt,grp,"
				+ "grplevel,grpstep from board ";
		param.clear();
		String sql=boardcolumn;
		if(searchtype != null && searchcontent !=null) {
			sql+=" where " + searchtype + " like :searchcontent";
				param.put("searchcontent","%"+searchcontent+"%");
		}
		sql+=" order by grp desc, grplevel , grpstep desc limit :startrow, :limit";
		param.put("startrow",(pageNum-1)*limit);
		param.put("limit",limit);
		param.put("searchtype", searchtype);
		param.put("searchcontent",searchcontent);
		System.out.println(searchtype+" , "+searchcontent);
		return template.getMapper(BoardMapper.class).list(param);
	}

	public void readcntadd(Integer num) {
		param.clear();
		param.put("num", num);
		String sql="update board set readcnt=readcnt+1"
				+" where num=:num";
		template.getMapper(BoardMapper.class).readcntadd(num);
		
	}

	public Board selectOne(Integer num) {
		String sql=boardcolumn +" where num= :num";
		param.clear();
		param.put("num",num);
		return template.getMapper(BoardMapper.class).selectOne(num);
	}

	public void updateGrpstep(Board board) {
		String sql="update board set grpstep=grpstep+1"
				+" where grp=:grp and grpstep > :grpstep";
		param.clear();
		param.put("grp", board.getGrp());
		param.put("grpstep", board.getGrpstep());
		template.getMapper(BoardMapper.class).updateGrpstep(board);
		
	}

	public void boardupdate(Board board) {
		SqlParameterSource prop = new BeanPropertySqlParameterSource(board);
		String sql="update board set name=:name, subject=:subject, content=:content,"
				+ "file1=:fileurl where num=:num";
		template.getMapper(BoardMapper.class).boardupdate(board);
				
		
	}

	public void delete(int num) {
		String sql="delete from board where num=:num";
		param.clear();
		param.put("num",num);
		template.getMapper(BoardMapper.class).delete(num);
		
		
	}

	public List<Map<String, Object>> graph1() {
		
		return template.getMapper(BoardMapper.class).graph1();
	}

	public List<Map<String, Object>> graph2() {
		return template.getMapper(BoardMapper.class).graph2();
		
	}

//	public Board detail(Integer num) {
//		param.clear();
//		param.put("num", num);
//		String sql="select num, name, pass, subject, content, file1 fileurl, regdate, readcnt, grp, grplevel, grpstep"
//				+ " from board where num = :num"; 
//		return template.queryForObject(sql, param, mapper);
//	}
//
//	public void updatereadCnt(Integer num) {
//		param.clear();
//		param.put("num", num);
//		template.update("update board set readcnt = readcnt+1 where num = :num", param);
//	}
	
	
}
