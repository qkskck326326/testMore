<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<c:set var="currentPage" value="${requestScope.currentPage}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>first</title>

<%-- 아래의 자바 스크립트 함수에서 사용하는 url 요청 처리를 별도로 변수화 시킴 --%>

<c:url var="replyf" value="breplyform.do">
	<c:param name="bnum" value="${board.boardNum}" />
	<c:param name="page" value="${currentPage}" />
</c:url>

<c:url var="bdel" value="bdelete.do">
	<c:param name="boardNum" value="${board.boardNum}" />
	<c:param name="boardLev" value="${board.boardLev}" />
	<c:param name="boardRenameFileName" value="${board.boardRenameFileName}" />
</c:url>

<c:url var="bup" value="bupview.do">
	<c:param name="boardNum" value="${board.boardNum}" />
	<c:param name="currentPage" value="${currentPage}" />
</c:url>

<script type="text/javascript">
function requestReply(){
	//댓글달기 요청 함수
	location.href = "${replyf}";
}

function requestDelete(){
	//게시글(원글, 댓글, 대댓글) 삭제 요청 함수
	location.href = "${bdel}";
}

function moveUpdatePage(){
	//게시글 (원글, 댓글, 대댓글) 수정 페이지로 이동 처리 함수
	location.href = "${bup}";
}
</script>
</head>
<body>
<c:import url="/WEB-INF/views/common/menubar.jsp" />
<hr>

<h1 align="center">${board.boardNum}번 게시글 상세보기</h1>
<br>

<table align="center" width="500" border="1" cellspacing="0" cellpadding="5">
	<tr>
		<th width="120">제 목</th>
		<td>${board.boardTitle}</td>
	</tr>
	<tr>
		<th width="120">작성자</th>
		<td>${board.boardWriter}</td>
	</tr>
	<tr>
		<th width="120">등록날짜</th>
		<td><fmt:formatDate value="${board.boardDate}" pattern="yyyy-MM--dd" /></td>
	</tr>
	<tr>
		<th width="120">첨부파일</th>
		<td>
		<c:if test="${!empty board.boardOriginalFileName}">
			<c:url var="bdown" value="bdown.do">
				<c:param name="ofile" value="${board.boardOriginalFileName}"/>
				<c:param name="rfile" value="${board.boardRenameFileName}"/>
			</c:url>
			<a href="${bdown}">
			${board.boardOriginalFileName}</a>
		</c:if>
		<c:if test="${empty board.boardOriginalFileName}">
			&nbsp;
		</c:if>
		</td>
	</tr>
	<tr>
		<th width="120">내 용</th>
		<td>${board.boardContent}</td>
	</tr>
	<tr>
		<th colspan="2">
			
			<c:if test="${!empty loginMember}"><%-- 로그인 --%>
			<%-- IF 로그인 + 본인글 --%>
				<c:if test="${loginMember.userId eq board.boardWriter}">
					<button onclick="moveUpdatePage(); return false;">수정페이지로 이동</button> &nbsp;
					<button onclick="requestDelete(); return false;">글삭제</button> &nbsp;
				</c:if>
				
				<%-- IF 로그인 + 관리자 --%>
				<c:if test="${loginMember.adminYN eq 'Y' and loginMember.userId ne board.boardWriter}">
					<button onclick="requestDelete(); return false;">글삭제</button> &nbsp;
					<c:if test="${board.boardLev < 3}">
					<button onclick="requestReply(); return false;">댓글달기</button> &nbsp;
					</c:if>
				</c:if>
				
				<%-- 로그인 + 본인글x + 댓글level < 3 == 댓글달기 버튼 표시 --%>
				<c:if test="${loginMember.adminYN eq 'N' and loginMember.userId ne board.boardWriter}">
					<c:if test="${board.boardLev < 3}">
					<button onclick="requestReply(); return false;">댓글달기</button> &nbsp;
					</c:if>
				</c:if>
			</c:if>
			
			<%-- 댓글달기 버튼은 로그인한 경우에만 보이게 함 (본인 글이 아니라면) --%>
 			<%-- <% if(loginMember != null){ //로그인한 상태이면
					if(loginMember.getUserId().equals(board.getBoardWriter())){
						//로그인한 회원 아이디와 글작성자가 같다면 (본인이 올린 글이면)
			%>
				<button onclick="moveUpdatePage(); return false;">수정페이지로 이동</button> &nbsp;
				<button onclick="requestDelete(); return false;">글삭제</button> &nbsp;
			<%      }else if(loginMember.getAdminYN().equals("Y")){  //관리자이면 %>
				<button onclick="requestDelete(); return false;">글삭제</button> &nbsp;
			<%               if(board.getBoardLev() < 3){ %>
				<button onclick="requestReply(); return false;">댓글달기</button> &nbsp;
			<%               } %>
			<% 		 }else{ //로그인했는데 본인 글이 아닐 때 %>
				<%               if(board.getBoardLev() < 3){ %>
				<button onclick="requestReply(); return false;">댓글달기</button> &nbsp;
			<%               } %>
			<%       }
			     } %>  --%>
			     
			     
			     <c:url var="bl" value="blist.do">
			     	<c:param name="page" value="${currentPage}"></c:param>
			     </c:url>
			 <button onclick="javascript:location.href='${bl}';">목록</button> 
		</th>		
	</tr>
</table>
<br>

<hr>
<c:import url="/WEB-INF/views/common/footer.jsp" />
</body>
</html>









