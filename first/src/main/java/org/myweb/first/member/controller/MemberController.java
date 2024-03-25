package org.myweb.first.member.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.myweb.first.common.SearchDate;
import org.myweb.first.member.model.service.MemberService;
import org.myweb.first.member.model.vo.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller //설정 xml 에 해당 클래스를 Controller 로 자동 등록해 줌
public class MemberController {
	// 이 클래스 안에 메소드들이 잘 동작하는지 또는 전달값이나 리턴값을 확인하기 위한 용도로 
	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	//서비스 연결 처리 : 자동 DI 처리
	@Autowired
	private MemberService memberService;
	//스프링에서는 부모 인터페이스 타입으로 레퍼런스 선언함 (다형성 이용함)
	//실행시 후손의 오버라이딩된 메소드를 연결 실행하게 됨 (동적 바인딩 이라고 함)
	
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoding;
	
	// 뷰 페이지 내보내기용 메소드 -----------------------------------------------------
	
	@RequestMapping(value="loginPage.do", method= {RequestMethod.GET, RequestMethod.POST})
	public String moveloginPage() {
		return "member/loginPage";
	}//
	
	@RequestMapping("test.do")
	public String moveTestPage() {
		return "common/test";
	}
	
	// 회원가입 페이지 내보내기용
	@RequestMapping("enrollPage.do")
	public String moveEnrollPage() {
		return"member/enrollPage";
	}
	
	// 요청 받아서 모델쪽 서비스로 넘기고, 결과 받는 메소드 ----------------------------------
//	
//	// 로그인 처리용 메소드 : Servlet 방식으로 처리
//	@RequestMapping(value="login.do", method=RequestMethod.POST)
//	public String loginMethod(HttpServletRequest request, HttpServletResponse response, Model model) {
//		
//		logger.info("login.do run()");
//		
//		// 1. 전송 온 값을 꺼내 객체에 저장하기
//		Member member = new Member();
//		member.setUserId(request.getParameter("userid"));
//		member.setUserPwd(request.getParameter("userpwd"));
//		
//		// 2. 서비스 메소드로 객체 전달하고, 실행된 결과 받기
//		Member loginMember = memberService.selectLogin(member);
//		logger.info(loginMember.toString());
//		
//		// 3. 받은 결과를 가지고 성공 또는 실패 페이지 내보내기
//		if(loginMember != null) {
//			// 로그인 세션 새로 생성
//			HttpSession session = request.getSession(); 
//			session.setAttribute("loginMember", loginMember); 
//			return "common/main";
//		} else {
//			// 스프링에서는 request 에 저장처리 하는 내용( 객체정보 ) 은 스프링이 제공하는 Model 을 사용하여
//			// 포워딩 못함 = 뷰 리졸버로 뷰 파일명과 뷰로 내보낼 값이 전달되는 구조이다
//			model.addAttribute("message", "로그인 실패! 아이디나 암호를 다시 확인하세요");
//			return "common/error";
//		}
//	}//
	
	// 로그인 처리용 메소드 : commend 객체 사용
	// 서버로 전송 온 parameter 값을 저장하는 객체를 commend 객체라고 함
	// input 테그의 name 속성의 이름과 vo 객체 필드명이 같으면, 자동으로 commend 객체가 값을 받음
	
	@RequestMapping(value="login.do", method=RequestMethod.POST)
	public String loginMethod(Member member, HttpSession session, SessionStatus status, Model model) {
		
		logger.info("login.do : " + member.toString());

		// 서비스 메소드로 보내고 결과 받기
		//Member loginMember = memberService.selectLogin(member);
		
		// 암호화 처리된 패스워드 일치 조회는 select 해 온 값으로 비교함
		// 전달온 회원 아이디로 먼저 회원 정보를 조회해 옴
		Member loginMember = memberService.selectMember(member.getUserId());
			
		// 조회해 온 회원 정보가 있고, 회원의 암호화된 패스워드와 뷰에서 전달받은 패스워드를 비교함
		// marches( 전달받은 암호글자, 암호화된 패스워드 ) 
		if (loginMember != null
				&& loginMember.getLoginOk().equals("Y")
												 && this.bcryptPasswordEncoding.matches(member.getUserPwd(),
												 loginMember.getUserPwd())
												 ) {
			
			session.setAttribute("loginMember", loginMember);
			status.setComplete(); // 로그인 성공 요청 결과로 HttpStatus code 200 보냄	
			return "common/main";
		}else {
			model.addAttribute("message", "로그인 실패! 아이디나 암호를 다시 확인하세요");
			return "common/error";
		}
		
		
	}//
	
	//로그아웃 처리용 메소드 
	// 요청에 대한 전송 방식이 get 이면, method 속성을 생략해도 된다 
	// method 속성을 생략하면 value 속성 표기를 생략해도 된다
	@RequestMapping("logout.do")
	public String logoutMethod(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession(false);
		// 세션 객체가 있으면 리턴받고, 없으면 null 리턴
		if(session != null) { 
			session.invalidate();
			return "common/main";
		} else {
			model.addAttribute("message","로그인 세션이 존재하지 않습니다.");
			return "common/error";
		}
	}//
	
	// 회원가입요청 처리용 메소드
	@RequestMapping(value="enroll.do", method=RequestMethod.POST)
	public String memberInsert(Member member, Model model) {
		logger.info("enroll.do : " + member);
		
		// 패스워드 암호화 처리
		member.setUserPwd(bcryptPasswordEncoding.encode(member.getUserPwd()));
		logger.info("after encode : " + member.getUserPwd());
		
		if(memberService.insertMember(member) > 0) {
			return "common/main";
		} else {
			model.addAttribute("message", "회원가입 실패! 확인하고 다시 가입해 주세요.");
			return "common/error";
		}
		
		
	}//
	
	//ajax 통신으로 가입할 아이디 중복 확인 요청 처리용 메소드
	@RequestMapping(value="idchk.do", method=RequestMethod.POST)
	public void dupcheckId(@RequestParam("userid") String userId, HttpServletResponse response) throws IOException {
		// 메소드 매개변수 영역에서 사용하는 어노테이션 중에
		// @RequestParam("전송 온 이름") 자료형 값저장변수명
		// 자료형 값저장변수명 = request.getParameter("전송 온 이름"); 코드와 값음
		int idCount = memberService.selectCheckId(userId);
		
		String returnStr = null;
		if(idCount == 0) {
			returnStr = "ok";
		}else {
			returnStr = "dup";
		}
		
		// response 를 이용해서 클라이언트와 출력스트림을 열러서 문자열값 내보냄
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.append(returnStr);
		out.flush();
		out.close();
	}//
	
	// my Page 클릭시 내 정보보기 요청 처리용 메소드
	// 컨트롤러에서 뷰 리졸버로 리턴하는 타입 == Strig or ModelAndView 를 사용할 수 있음
	@RequestMapping("myinfo.do")
	public String memberDetail(@RequestParam("userId") String userId, Model model) {
		// 서비스 메소드로 아이디 전달하고, 해당 회원정보 받기
		Member member = memberService.selectMember(userId);
		if(member != null) {
			model.addAttribute(member);
			return "member/myInfoPage";
		}else {
			model.addAttribute("message",userId + "에 대한 회원정보 조회 실패!");
			return "common/error";
		}
	}//
	
	// 회원정보 수정 페이지로 이동 처리용 메소드 <= 수정할 회원 정보도 함께 보내야 함
	// 뷰리졸버로 리턴하는 타입을 String 을 사용한 경우
	@RequestMapping("moveup.do")
	public String moveUpdatePage(@RequestParam("userId") String userId, Model model) {
		
		Member member = memberService.selectMember(userId);
		
		if(member != null) {
			model.addAttribute(member);
			return "member/memberUpdatePage";
		}else {
			model.addAttribute("message",userId + "에 대한 수정페이지 이동 실패");
			return "common/error";
		}
	}//
	
	// 회원 정보 수정 처리용 메소드 : 수정 성공시 myPage 로 이동
	@RequestMapping(value="mupdate.do", method=RequestMethod.POST)
	public String memberUpdate(Member member, Model model, 
								@RequestParam("origin_userpwd") String originUserPwd) {
		logger.info("mupdate.do : ", member);
		
		// 새로운 암호가 왔다면, 패스워드 암호화 처리
		String userPwd = member.getUserPwd().trim();
		if(userPwd != null && userPwd.length() > 0) {
			// 암호화된 기존의 패스워드와 새로운 패스워드를 비교해서, 다른값이면 변경 처리
			if(!(this.bcryptPasswordEncoding.matches(userPwd, originUserPwd))){
				// member 의 새로운 패스워드를 암호화하여 저장
				member.setUserPwd(this.bcryptPasswordEncoding.encode(userPwd));
			}		
		}else {
			// 기존 암호이면, 원래 암호화된 패스워드를 저장함
			member.setUserPwd(originUserPwd);
		}
		
		if(memberService.updateMember(member) > 0) {
			// 뷰 리졸버로 리턴하지 않고, 컨트롤러의 다름 메소드를 실행 할 경우
			return "redirect:main.do";
			// 만약, 회원정보 수정 성공시 마이페이지로 이동하게 된다면
			// return "redirect:myinfo.do?userId=?"
		}else {
			model.addAttribute("message", member.getUserId() + "에 대한 회원정보 수정 실패!");
			return "common/error";
		}
	}//
	@RequestMapping("mdel.do")
	public String memberDeleteMethod(@RequestParam("userId") String userId, Model model) {
		// 회원 탈퇴 성공시 자동 로그아웃 처리
		if(memberService.deleteMember(userId) > 0) {
			// 컨트롤러 또는 다른 컨트롤러의 메소드를 직접 호출 할 경우
			return "redirect:logout.do";
		}else {
			model.addAttribute("message", userId + " 탈퇴 실패!");
			return "common/error";
		}
	}
	
	// 관리자용 : 회원 관리용 회원전체목록 조회 처리용 메소드 - String 반환
	/*
	 * @RequestMapping("mlist.do") public String memberListView(Model model) {
	 * ArrayList<Member> list = memberService.selectList();
	 * 
	 * if(list != null && list.size() > 0) { model.addAttribute("list", list);
	 * return "member/memberListView"; }else { model.addAttribute("message",
	 * "회원 정보가 존재하지 않습니다"); return "common/error"; } }//
	 */	
	
//	@RequestMapping("mlist.do")
//	public ModelAndView memberListView(ModelAndView mv) {
//		ArrayList<Member> list = memberService.selectList();
//		if(list != null && list.size() > 0) {
//			mv.addObject("list", list);
//			mv.setViewName("member/memberListView");
//		}else {
//			mv.addObject("message", "회원 정보가 존재하지 않습니다.");
//			mv.setViewName("common/error");
//		}
//		return mv;
//	}
	// 반환형이 ModelAndView : 페이징 처리 추가된 메소드
	@RequestMapping("mlist.do")
	public ModelAndView memberListView(
			@RequestParam(name="page", required=false) String page, 
			ModelAndView mv) {
		int currentPage = 1;
		if(page != null) {
			currentPage = Integer.parseInt(page);
		} 
		
		// 한페이지에 출력할 목록 갯수를 10개로 지정
		int limit = 10;
		// db 테이블에 저장된 회원 총 갯수 조회
		int listCount = memberService.selectListCount();
		// 페이징 계산 처리
		Paging paging = new Paging(listCount, currentPage, limit, "mlist.do");
		paging.calculate();
		
		ArrayList<Member> list = memberService.selectListP(paging);
		
		if(list != null && list.size() > 0) {
			mv.addObject("list", list);
			mv.addObject("paging", paging);
			mv.setViewName("member/memberListView");
		}else {
			mv.addObject("message", "회원 정보가 존재하지 않습니다.");
			mv.setViewName("common/error");
		}
		return mv;
	}
	
	// 관리자용 회원 로그인 제한
	@RequestMapping("loginok.do")
	public String changeLoginOk(Member member, Model model) {
		if(memberService.updateLoginOK(member) > 0) {
			return "redirect:mlist.do";
		}else {
			model.addAttribute("message", "로그인 제한 처리 오류");
			return "common/error";
		}
	}//
	
	// 관리자용 : 회원검색 처리용 메소드 --- 페이징 처리 없는 코드
//	@RequestMapping(value="msearch.do", method=RequestMethod.POST)
//	public ModelAndView memberSearch(HttpServletRequest request, ModelAndView mv) {
//		// 전송 온 값 꺼내기
//		String action = request.getParameter("action");
//		// 필요한 변수 선언
//		String keyword = null, begin = null, end = null;
//		
//		if(action.equals("enrolldate")) {
//			begin = request.getParameter("begin");
//			end = request.getParameter("end");
//		}else {
//			keyword = request.getParameter("keyword");
//		}
//		
//		// 서비스 메소드 호출하고 리턴 결과 받기
//		ArrayList<Member> list = null;
//		switch(keyword) {
//		case "id": list = memberService.selectSearchUserid(keyword); break;
//		case "gender": list = memberService.selectSearchGender(keyword); break;
//		case "age": list = memberService.selectSearchAge(Integer.parseInt(keyword)); break;
//		case "enrolldate": {SearchDate searchDate = new SearchDate();
//						   searchDate.setBegin(Date.valueOf(begin));
//						   searchDate.setEnd(Date.valueOf(end));
//			list = memberService.selectSearchEnrollDate(searchDate);} break;
//		case "loginok": list = memberService.selectSearchUserid(keyword); break;
//		}
//		
//		// 받은 결과에 따라 성공/실패 페이지 내보내기
//		if(list != null && list.size() > 0) {
//			mv.addObject("list", list);
//			mv.setViewName("member/memberListView");
//		}else {
//			if(keyword != null) {
//				mv.addObject("message", action + "에 대한" + keyword + "검색결과가 존재하지 않습니다");
//			} else {
//				mv.addObject("message", action + "에 대한" + begin + "부터"
//						+ end + "기간 사이에 가입한 회원 정보가 존재하지 않습니다");
//			}
//			mv.setViewName("common/error");
//		}
//		return mv;
//	}
	//      ++ 페이징 처리 ++
	@RequestMapping(value="msearch.do", method=RequestMethod.POST)
	public ModelAndView memberSearch(HttpServletRequest request, ModelAndView mv) {
		// 전송 온 값 꺼내기
		String action = request.getParameter("action");
		// 필요한 변수 선언
		String keyword = null, begin = null, end = null;
		
		SearchDate searchDate = null;
		
		if(action.equals("enrolldate")) {
			begin = request.getParameter("begin");
			end = request.getParameter("end");
			searchDate = new SearchDate(Date.valueOf(begin), Date.valueOf(end));
		}else {
			keyword = request.getParameter("keyword");
		}
		
		// 검색 결과에 대한 페이징 처리
		// 출력할 페이지 지정
		int currentPage = 1;
		// 전송온 페이지 값이 있다면
		if(request.getParameter("page") != null){
		currentPage = Integer.parseInt(request.getParameter("page"));
		}
					
		// 한 페이지에 출력할 목록 갯수 지정
		int limit = 10;
		// 전송 온 limit 값이 있다면
		if(request.getParameter("limit") != null){
		limit = Integer.parseInt(request.getParameter("limit"));
		}
					
		// 총 페이지 수 계산을 위해 검색 결과가 적용된 총 목록 갯수 조회
		int listCount = 0;

			switch(action) {
			case "id": 			listCount = memberService.selectSearchUseridCount(keyword); break;
			case "gender": 		listCount = memberService.selectSearchGenderCount(keyword); break;
			case "age": 		listCount = memberService.selectSearchAgeCount(Integer.parseInt(keyword)); break;
			case "enrolldate" : listCount = memberService.selectSearchDateCount(searchDate); break;
			case "loginok": 	listCount = memberService.selectSearchLoginOkCount(keyword); break;
			}

		// 뷰 페이지와 쿼리문에서 사용할 페이징 관련 값 계산 처리
		Paging paging = new Paging(listCount, currentPage, limit, "msearch.do");
		paging.calculate();
		
		// 마이바티스 매퍼 쿼리문으로는 객체 한 개만 전달 가능 => 별도의 클래스 만들어서 사용
		Search search = new Search();
		search.setStartRow(paging.getStartRow());
		search.setEndRow(paging.getEndRow());
	
		
		
		// 서비스 메소드 호출하고 리턴 결과 받기
		ArrayList<Member> list = null;
			switch(action) {
			case "id": 			search.setKeyword(keyword);
								list = memberService.selectSearchUserid(search); break;
			case "gender": 		search.setKeyword(keyword);
								list = memberService.selectSearchGender(search); break;
			case "age":			search.setKeyword(keyword);
								list = memberService.selectSearchAge(search); break;
			case "emrolldate":	search.setBegin(Date.valueOf(begin));
								search.setEnd(Date.valueOf(end));
								list = memberService.selectSearchEnrollDate(search);break;
			case "loginok": 	search.setKeyword(keyword);
								list = memberService.selectSearchLoginOK(search); break;
			}

		
		// 받은 결과에 따라 성공/실패 페이지 내보내기
		if(list != null && list.size() > 0) {
			mv.addObject("list", list);
			mv.addObject("Paging", paging);
			mv.addObject("currentPage", currentPage);
			mv.addObject("limit", limit);
			mv.addObject("action", action);
			
			if(keyword != null) {
				mv.addObject("keyword", keyword);
			}else {
				mv.addObject("begin", begin);
				mv.addObject("end", end);
			}
			
			mv.setViewName("member/memberListView");
		}else {
			if(keyword != null) {
				mv.addObject("message", action + "에 대한" + keyword + "검색결과가 존재하지 않습니다");
			} else {
				mv.addObject("message", action + "에 대한" + begin + "부터"
						+ end + "기간 사이에 가입한 회원 정보가 존재하지 않습니다");
			}
			mv.setViewName("common/error");
		}
		return mv;
	}
	
}//








