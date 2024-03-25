<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>  
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시판 목록</title>
    <script type="text/javascript" src="/first/resources/js/jquery-3.7.0.min.js"></script>
    <!-- <script type="text/javascript">
    $(function(){
    	//최근 등록된 공지글 3개 전송받아서 출력 처리
    	$.ajax({
    		url: "ntop3.do",
    		type: "post",
    		dataType: "json",
    		success: function(data){
    			console.log("success : " + data);
    			
    			//object --> string
    			var str = JSON.stringify(data);
    			
    			//string --> json
    			var json = JSON.parse(str);
    					
    			for(var i in json.nlist){
    				values += "<tr><td>" + json.nlist[i].no 
    						+ "</td><td><a href='ndetail.do?N=" 
    						+ json.nlist[i].no + "'>"
    						+ decodeURIComponent(json.nlist[i].title).replace(/\+/gi, " ") 
    						+ "</a></td><td>"
    						+ json.nlist[i].date + "</td></tr>";
    			}
    			
    			$('#newnotice').html($('#newnotice').html() + values);
    			//$('#newnotice').append(values);
    		},
    		error: function(jqXHR, textStatus, errorThrown){
    			console.log("error : " + jqXHR + ", " + textStatus + ", " + errorThrown);
    		}
    	});
    });
</script> -->

</head>
<body>

<h2>게시판 목록</h2>

<table id="newnotice" border="1">
    <tr>
        <th>번호</th>
        <th>작성자</th>
        <th>날짜</th>
    </tr>
</table>

</body>
</html>