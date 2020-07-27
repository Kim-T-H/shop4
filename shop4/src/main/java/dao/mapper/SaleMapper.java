package dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import logic.Sale;

public interface SaleMapper {

	@Select("select ifnull(max(saleid),0) from sale")
	int maxSaleid();

	@Select("select * from sale where userid = #{userid}")
	List<Sale> select(Map<String, Object> param);

	@Update("insert into sale (saleid,userid,saledate) values (#{saleid},#{userid},now())")
	void update(Sale sale);

}
