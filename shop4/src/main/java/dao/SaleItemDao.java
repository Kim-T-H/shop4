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

import dao.mapper.SaleItemMapper;
import logic.Sale;
import logic.SaleItem;

@Repository
public class SaleItemDao {
	@Autowired
	private SqlSessionTemplate template;
	private Map<String,Object> param = new HashMap<>();
	
	public void insert(SaleItem saleItem) {
		String sql="insert into saleitem (saleid,seq,itemid, quantity) values (:saleid,:seq,:itemid, :quantity) ";
		SqlParameterSource prop= new BeanPropertySqlParameterSource(saleItem);
		template.getMapper(SaleItemMapper.class).insert(saleItem);
		
	}

	public List<SaleItem> list(int saleid) {	// 주문상품목록
		String sql="select * from saleitem where saleid=:saleid";
		param.clear();
		param.put("saleid",saleid);
		return template.getMapper(SaleItemMapper.class).select(param); 
	}

	

}
