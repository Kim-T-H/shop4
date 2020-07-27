package dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import logic.Board;

public interface BoardMapper {
	String boardcolumn="select num, name, pass, subject, content, file1 fileurl, regdate, readcnt, grp, grplevel, grpstep"
			+ " from board";

	@Select("select ifnull(max(num),0) from board")
	int maxnum();

	@Insert("insert into board (num,name,pass,subject,content,regdate,file1,readcnt,grp,grplevel,grpstep)"
			+"values (#{num},#{name},#{pass},#{subject},#{content},now(),#{fileurl},0,#{grp},#{grplevel},#{grpstep})")
	void insert(Board board);

	@Update("update board set readcnt=readcnt+1 where num=#{num}")
	void readcntadd(Integer num);

	@Select(boardcolumn +" where num= #{num}")
	Board selectOne(Integer num);

	@Update("update board set grpstep=grpstep+1"
			+" where grp=#{grp} and grpstep > #{grpstep}")	
	void updateGrpstep(Board board);

	@Update("update board set name=#{name}, subject=#{subject}, content=#{content},"
				+ "file1=#{fileurl} where num=#{num}")
	void boardupdate(Board board);

	@Delete("delete from board where num=#{num}")
	void delete(int num);

	@Select({"<script>",
			"select count(*) from board ",
			"<if test='searchtype != null and searchcontent !=null'> where ${searchtype}  like '%${searchcontent}%' </if>", 
			"</script>"})
	int count(Map<String, Object> param);

	
	@Select({"<script>",
		"select num,name,pass,subject,content,file1 fileurl,regdate,readcnt,grp,"
		+ "grplevel,grpstep from board ",
		"<if test='searchtype != null and searchcontent !=null'> where ${searchtype} like '%${searchcontent}%' </if>"
		+" order by grp desc, grplevel , grpstep desc limit #{startrow}, #{limit}",
		"</script>"
	})
	List<Board> list(Map<String, Object> param);

	@Select("select name, count(*) cnt from board "
			+ " group by name order by cnt desc LIMIT 0,7")
	
	List<Map<String, Object>> graph1();

	
	@Select("SELECT DATE_FORMAT(regdate,'%Y-%m-%d') regdate, COUNT(*) cnt from board "
			+ "GROUP BY DATE_FORMAT(regdate,'%Y-%m-%d') ORDER BY regdate LIMIT 0,7")
	List<Map<String, Object>> graph2();

}
