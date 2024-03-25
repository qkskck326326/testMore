package org.myweb.first.board.service;

import java.util.ArrayList;

import org.myweb.first.board.model.dao.BoardDao;
import org.myweb.first.board.model.vo.Board;
import org.myweb.first.common.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("boardservice")
public class BoardServiceImpl implements BoardService {
	@Autowired
	private BoardDao boarddao;
	
	@Override
	public ArrayList<Board> selectTop3() {
		return boarddao.selectTop3();
	}

	@Override
	public int selectListCount() {
		return boarddao.selectListCount();
	}

	@Override
	public ArrayList<Board> selectList(Paging paging) {
		return boarddao.selectList(paging);
	}

	@Override
	public int insertOriginBoard(Board board) {
		return boarddao.insertOriginBoard(board);
	}

	@Override
	public void updateAddReadCount(int boardNum) {
		boarddao.updateAddReadCount(boardNum);
	}

	@Override
	public Board selectBoard(int boardNum) {
		return boarddao.selectBoard(boardNum);
	}

	@Override
	public int updaateReplySeq(Board reply) {
		return boarddao.updaateReplySeq(reply);
	}

	@Override
	public int insertReply(Board reply) {
		return boarddao.insertReply(reply);
	}

	@Override
	public int deleteBoard(Board board) {
		return boarddao.deleteBoard(board);
	}

	@Override
	public int updaateReply(Board reply) {
		return boarddao.updaateReply(reply);
	}
	
	@Override
	public int updaateOrigin(Board reply) {
		return boarddao.updaateReply(reply);
	}


	
}//
