package org.myweb.first.board.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.myweb.first.board.model.vo.Board;
import org.myweb.first.board.service.BoardService;
import org.myweb.first.common.FileNameChange;
import org.myweb.first.common.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BoardController {
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	@Autowired
	private BoardService boardservice;
	
	//뷰 페이지 내보내기용 메소드 작성부 ---------------------------
	
	// 새 게시글 등록 페이지 내보내기용 메소드
	@RequestMapping("bwform.do")
	public String moveBoardWritePage() {
		return "board/boardwriteForm";
	}
	
	
	// 댓글 달기
	@RequestMapping("breplyform.do")
	public ModelAndView moveReplyPage(
							@RequestParam("bnum") int boardNum, 
							@RequestParam("page") int currentPage, ModelAndView mv) {
		
		mv.addObject("bnum", boardNum);
		mv.addObject("currentPage", currentPage);
		mv.setViewName("board/boardReplyForm");
		
		return mv;
	}
	
	// 게시글 (원글, 댓글, 대댓글) ` 페이지로 이동 처리용
	@RequestMapping("bupview.do")
	public String moveBoardUpdatePage(
			@RequestParam("boardNum") int boardNum,
			@RequestParam("currentPage") int currentPage, Model model) {
		// 수정 페이지에 전달해서 출력할 board 정보 조회함
		Board board = boardservice.selectBoard(boardNum);
		
		if(board != null) {
			model.addAttribute("board", board);
			model.addAttribute("page", currentPage);
			
			return "board/boardUpdateView";
		}else {
			model.addAttribute("message", boardNum + "번 글 수정 페이지로 이동 실패!");
			return "common/error";
		}

	}//
	
	
	
	// 요청 처리용 메소드 ---------------------------------------
	
	// 댓글과 대댓글 수정 처리용
	@RequestMapping(value="breplyupdate.do", method=RequestMethod.POST)
	public String replyUpdate(Board reply,
						@RequestParam("page") int page, Model model) {
		
		if(boardservice.updaateReply(reply) > 0){
			// 댓글과 대댓글 수정 성공시 다시 상세보기가 보여지게 처리
			model.addAttribute("bnum", reply.getBoardNum());
			model.addAttribute("page", page);
			
			return "redirect:bdetail.do";
		}else {
			model.addAttribute("message", reply.getBoardNum() + "번 글 수정 실패!");
			return "common/error";
		}
	}//
	
	// 원글 수정 처리용
	@RequestMapping(value="boriginupdate.do", method=RequestMethod.POST)
	public String originUpdate(Board board,
						HttpServletRequest resquest,
						@RequestParam(name="page", required=false) int page, 
						@RequestParam(name="upfile", required=false) MultipartFile mfile,
						@RequestParam(name="deleteFlag", required=false) String delFlag , Model model) {
		
		// 첨부 파일이 변경 되었을 때의 처리
		
		if(boardservice.updaateOrigin(board) > 0){
			//수정 성공시 다시 상세보기가 보여지게 처리
			model.addAttribute("bnum", board.getBoardNum());
			model.addAttribute("page", page);
			
			return "redirect:bdetail.do";
		}else {
			model.addAttribute("message", board.getBoardNum() + "번 글 수정 실패!");
			return "common/error";
		}
	}//
	
	
	// 게시글 ( 원글, 댓글, 대댓글 ) 삭제 요청 처리용
	@RequestMapping("bdelete.do")
	public String boardDeleteMethod(Board board, Model model, HttpServletRequest request) {
		
		if(boardservice.deleteBoard(board) > 0) {
			// 게시글 삭제 성공시 저장 폴더에 있는 첨부파일도 삭제함
			if(board.getBoardRenameFileName() != null) {
				String savePath = request.getSession().getServletContext().getRealPath("resources/board_upfiles/");
				// 저장 폴더에서 파일 삭제함
				new File(savePath + "\\" + board.getBoardRenameFileName()).delete();
			}
			
			return "redirect:blist.do";
		}else {
			model.addAttribute("message", board.getBoardNum() + "번 게시글 삭제 실패");
			return "common/error";
		}
		
	}//
	
	
	// 첨부파일 다운로드 요청 처리용
	// 파일 다운로드 처리용 메소드는 리턴 타입이 ModelAndView 로 정해져 있다
	@RequestMapping("bdown.do")
	public ModelAndView fileDown(
					@RequestParam("ofile") String originalFileName, 
					@RequestParam("rfile") String renameFileName,
					ModelAndView mv, HttpServletRequest request) {
		
		// 게시글 첨부파일 저장 폴더 지정
		String savePath = request.getSession().getServletContext().getRealPath("resources/board_upfiles/");
		
		// 저장폴더에서 읽어올 파일에 대한 File 객체 생성
		File readFile = new File(savePath + renameFileName);
		// 파일 다운시 브라우저로 내 보낼 원래의 파일명에 대한 File 객체 생성
		File originFile = new File(originalFileName);
		
		// 스프링에서는 파일 다운로드를 처리하는 뷰 클래스를 별도 작성하도록 되어 있음
		// 스프링이 제공하는 View 클래스를 상속받은 후손 클래스로 만들어야 함
		
		// 파일 다운로드용 뷰로 전달할 정보를 저장 처리
		mv.setViewName("filedown"); // 등록된 파일 다운로드용 뷰클래스의 id 명
		mv.addObject("renameFile", readFile);
		mv.addObject("originFile", originFile);

		
		return mv;
	}
	
	// 게시글 댓글, 대댓글 등록 처리용
	@RequestMapping(value="breply.do", method=RequestMethod.POST)
	public String replyInsert(Board reply, 
						@RequestParam("bnum") int bnum, 
						@RequestParam("page") int page, 
						Model model) {
		// 새로 등록할 댓글의 원글 또는 대댓글의 댓글을 조회해 옴
		Board origin = boardservice.selectBoard(bnum);
		
		// 새로 등록할 댓글의 원글 또는 대댓글의 레벨 지정
		reply.setBoardLev(origin.getBoardLev() + 1);
		
		// 참조 원글 번호
		reply.setBoardRef(origin.getBoardReplyRef());
		
		// 대댓글일 때는 boardReplyRef 지정 ( 참조 댓글 번호 )
		if(reply.getBoardLev() == 3) {
			//참조 댓글 번호 지정
			reply.setBoardReplyRef(origin.getBoardReplyRef());
		}
		
		// 최근 등록 댓글 또는 대댓글의 순번을 1로 지정함
		reply.setBoardReplySeq(1);
		logger.info("breply.do : " + reply);
		
		// 기존 같은 레벨의 댓글 또는 대댓글의 순번을 1증가 처리
		boardservice.updaateReplySeq(reply);
		
		if(boardservice.insertReply(reply) > 0) {
			return "redirect:boriginupdate.do?page=" + page;
		}else{
		 model.addAttribute("", bnum + "번 글에 대한 댓/대댓글 등록 실패");
		 return "common/error";
		}
		
	}//
	
	
	// 게시글 상세보기 요청 처리용
	@RequestMapping("bdetail.do")
	public String boardDetail(@RequestParam("bnum") int boardNum, 
							  @RequestParam(name="page", required=false) String page, 
							  Model model) {
		// 목록으로 돌아갈 때의 페이지를 기억, 저장
		int currentPage = 1;
		if(page != null) {
			currentPage = Integer.parseInt(page);
		}
		
		// 조회수 1증가 처리 
		boardservice.updateAddReadCount(boardNum);
		
		// 요청된 글번호의 게시글 가져오기
		Board board = boardservice.selectBoard(boardNum);
		
		if(board != null) {
			model.addAttribute("board", board);
			model.addAttribute("currentPage", currentPage);
			
			return "board/boardDetailView";
		}else {
			model.addAttribute("message", boardNum + " 에 대한 게시글 상세보기 요청 실패!");
			return "common/error";
		}

	}//
	
	// 새 게시글 등록 요청 처리용 (첨부파일 업로드 기능 추가됨)
	
	@RequestMapping(value="binsert.do", method=RequestMethod.POST)
	public String boardInsert(Board board, Model model, HttpServletRequest request, 
					@RequestParam(name="upfile", required=false) MultipartFile mfile){
		logger.info("binsert.do : ", board);
		
		// 게시글 첨부 파일 저장용 폴더 저장 : 톰캣(WAS)이 구동하고 있는 애플리케이션 프로젝트 안의 폴더 지정
		
		// EL  절대경로 표기인 ${pageContext.servletContext.contextPath}
		
		String savePath = request.getSession().getServletContext().getRealPath("resources/board_upfiles");
		
		// 첨부 파일이 있을 떄
		if(!mfile.isEmpty()){
			//전송온 첨부파일명 추출함
			String fileName = mfile.getOriginalFilename();
			String renameFileName = null;
			
			// 저장 폴더에는 변경된 파일 이름으로 파일 저장
			// 파일 이름 바꾸기 => 년월일시분초.확장자
			if(fileName != null && fileName.length() > 0) {
				// 바꿀 파일명에 대한 문자열 포멧 만들기
				renameFileName = FileNameChange.change(fileName, "yyyyMMddhhmmss");
				logger.info("파일명 변경 : " + fileName + " => " + renameFileName);
				
				try {
					// 지정한 저장 폴더에 파일명 바꾸기 처리함
					mfile.transferTo(new File(savePath + "\\" + renameFileName));
				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("message", "첨부파일 저장 실패");
					return "common/error";
				}
			}/// 파일명 바꾸기
			// board 에 첨부파일 정보 저장 처리
			board.setBoardOriginalFileName(fileName);
			board.setBoardRenameFileName(renameFileName);
		}// 첨부 파일
		if(boardservice.insertOriginBoard(board) > 0) {
			return "redirect:blist.do";
			
		}else {
			model.addAttribute("message", "등록 실패!");
			return"common/error";
		}
		

	}//
	
	@RequestMapping(value="btop3.do", method=RequestMethod.POST)
	@ResponseBody	// response 객체에 받아서 보내겠다는 의미로 ajax 통신에서만 사용
	public String boardTop3() throws UnsupportedEncodingException {
		// response 객체 이용 방법 2가지
		// 1. 출력 스트림으로 응답하는 방법
		// 2. 뷰 리졸버로 리턴하는 방법 : response body 에 내보낼 값을 저장
		// new - 2. =>> sevelet-context.xml 에 설정된 JSONVIEW 클래스가 받아서 내보냄 ( 등록설정 필요 )
		
		// 조회수 많은 순으로 인기 게시글 3개 조회해 옴
		ArrayList<Board> list = boardservice.selectTop3();
		
		// list 를 json 배열에 옮기기
		JSONArray jarr = new JSONArray();
		
		for(Board board : list) {
			// board 객체 저장용 json 객체 생성
			JSONObject job = new JSONObject();
			
			job.put("bnum", board.getBoardNum());
			// 한글에 대해서는 인코딩해서 json에 담아야 한다
			job.put("btitle", URLEncoder.encode(board.getBoardTitle(), "utf-8"));
			job.put("rcount", board.getBoardReadCount());
			
			// job 을 jarr 에 추가
			jarr.add(job);
		}
		
		// 전송용 json 객체 준비
		JSONObject sendJson = new JSONObject();
		//전송용 객체에 jarr을 담음
		sendJson.put("list", jarr);
		
		// 전송용 json 을 json strinf 으로 바꿔서 전송 처리
		return sendJson.toJSONString();
	}//
	
	// 게시글 목록 보기 요청 처리용 ( 페이징 처리 )
	@RequestMapping("blist.do")
	public String boardListMethod(
			@RequestParam(name="page", required=false) String page, 
			@RequestParam(name="limit", required=false) String slimit , Model model) {
		int currentPage = 1;
		if(page != null) {
			currentPage = Integer.parseInt(page);
		}
		
		//한 페이지게 게시글 10개씩 출력되게 한다면
		int limit = 10;
		if(slimit != null) {
			limit = Integer.parseInt(slimit);	//전송받은 한 페이지게 출력할 목록 갯수를 적용
		}
		
		// 총 페이지 수 계산을 위해 게시글 전체 갯수 조회
		int listCount = boardservice.selectListCount();
		
		// 페이징 계산 처리 실행
		Paging paging = new Paging(listCount, currentPage, limit, "blist.do");
		paging.calculate();
		
		// 출력할 페이지에 대한 목록 조회
		ArrayList<Board>  list = boardservice.selectList(paging);
		
		// 받은 결과로 성공/실패 페이지 내보냄
		if(list != null && list.size() > 0) {
			model.addAttribute("list", list);
			model.addAttribute("paging", paging);
			model.addAttribute("currentPage", currentPage);
			model.addAttribute("limit", limit);
			
			return "board/boardListView";
		}else {
			model.addAttribute("message", currentPage + "페이지 목록 조회 실패");
			return "common/error";
		}
		
	}//
	
	
	
}//


















