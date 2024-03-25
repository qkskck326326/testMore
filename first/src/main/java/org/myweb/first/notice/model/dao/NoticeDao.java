package org.myweb.first.notice.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.myweb.first.notice.model.vo.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("noticeDao")
public class NoticeDao {
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	// 새 공지글 3개 조회 리턴 : 작성 날짜로 Top-3 분석 이용함
	public ArrayList<Notice> selectTop3(){
		List<Notice> list = sqlSessionTemplate.selectList("noticeMapper.newTop3");
		return (ArrayList<Notice>)list;
	}
} //
