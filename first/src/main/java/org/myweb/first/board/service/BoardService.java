package org.myweb.first.board.service;

import java.util.ArrayList;

import org.myweb.first.board.model.vo.Board;
import org.myweb.first.common.Paging;

public interface BoardService {
	ArrayList<Board> selectTop3();

	int selectListCount();

	ArrayList<Board> selectList(Paging paging);

	int insertOriginBoard(Board board);

	void updateAddReadCount(int boardNum);

	Board selectBoard(int boardNum);

	int updaateReplySeq(Board reply);

	int insertReply(Board reply);

	int deleteBoard(Board board);

	int updaateReply(Board reply);

	int updaateOrigin(Board reply);
}
