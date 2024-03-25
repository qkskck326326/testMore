package org.myweb.first.notice.controller;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.myweb.first.notice.model.service.NoticeService;
import org.myweb.first.notice.model.vo.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class NoticeController {
	
	@Autowired
	private NoticeService noticeService;
	
	// 요청 처리용 메소드 --------------------------
	@RequestMapping(value="ntop3.do", method=RequestMethod.POST)
	@ResponseBody
	public String noticeTop3() {
		ArrayList<Notice> list = noticeService.selectTop3();
		
		JSONArray jarr = new JSONArray();
		
		for(Notice notice : list) {
			JSONObject job = new JSONObject();
			
			job.put("no", notice.getNoticeNo());
			job.put("title", notice.getNoticeTitle());
			job.put("date", notice.getNoticeDate().toString());
			
			jarr.add(job);
		}
		
		JSONObject sendJson = new JSONObject();
		sendJson.put("nlist", jarr);
		
		return sendJson.toJSONString();

	}//
}//
