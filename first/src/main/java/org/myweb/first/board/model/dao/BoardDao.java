package org.myweb.first.board.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.myweb.first.board.model.vo.Board;
import org.myweb.first.common.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("boarddao")
public class BoardDao {
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	// 조회수 많은 인기 게시글 3개 조회
	public ArrayList<Board> selectTop3(){
		List<Board> list = sqlSessionTemplate.selectList("boardMapper.selectTop3");
		return (ArrayList<Board>)list;
	}//
	
	public ArrayList<Board> selectSearchUserid3(){
		List<Board> list = sqlSessionTemplate.selectList("boardMapper.selectSearchUserid");
		return (ArrayList<Board>)list;
	}//

	public int selectListCount() {
		return sqlSessionTemplate.selectOne("boardMapper.selectListCount");
	}

	public ArrayList<Board> selectList(Paging paging) {
		List<Board> list = sqlSessionTemplate.selectList("boardMapper.selectList", paging);
		return (ArrayList<Board>)list;
	}

	public int insertOriginBoard(Board board) {
		return sqlSessionTemplate.insert("boardMapper.insertOriginBoard", board);
	}

	public void updateAddReadCount(int boardNum) {
		sqlSessionTemplate.update("boardMapper.updateAddReadCount", boardNum);
	}

	public Board selectBoard(int boardNum) {
		return sqlSessionTemplate.selectOne("boardMapper.selectBoard", boardNum);
	}

	public int updaateReplySeq(Board reply) {
		int result = 0;
		
		if(reply.getBoardLev() == 2) {
			result = sqlSessionTemplate.update("boardMapper.updateReplySeq2", reply);
		}
		
		if(reply.getBoardLev() == 3){
			result = sqlSessionTemplate.update("boardMapper.updateReplySeq3", reply);
		}
		
		return result;
	}

	public int insertReply(Board reply) {
int result = 0;
		
		if(reply.getBoardLev() == 2) {
			result = sqlSessionTemplate.insert("boardMapper.insertReplySeq2", reply);
		}
		
		if(reply.getBoardLev() == 3){
			result = sqlSessionTemplate.insert("boardMapper.insertReplySeq3", reply);
		}
		
		return result;
	}

	public int deleteBoard(Board board) {
		return sqlSessionTemplate.delete("boardMapper.deleteBoard", board);
	}

	public int updaateReply(Board reply) {
		return sqlSessionTemplate.update("boardMapper.updaateReply", reply);
	}

	public int updaateOrigin(Board reply) {
		return sqlSessionTemplate.update("boardMapper.updateOrigin", reply);
	}
	
	
}//
