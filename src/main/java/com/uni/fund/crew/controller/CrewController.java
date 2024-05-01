package com.uni.fund.crew.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

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

import com.uni.fund.crew.dto.ActivityDTO;
import com.uni.fund.crew.dto.CrewDTO;
import com.uni.fund.crew.service.CrewService;

@Controller
public class CrewController {

	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired CrewService crewService;
	
	
	@RequestMapping(value="/crew/crewCreate.go")
	public String crewCreateGo() {
		logger.info("crewCreate 들어왔다.");
		return "crew/crewCreateForm";
	}
	
	@RequestMapping(value="/crew/crewCreate.do", method = RequestMethod.POST)
	public String crewCreateDo(MultipartFile crew_logo_photo,MultipartFile crew_recru_photo, 
			@RequestParam Map<String, String>param, Integer mem_idx, Model model,HttpSession session) {
		logger.info("create 들어왔다.");
		logger.info("Create param:{}",param);
		
		String page="redirect:/";
		String msg="크루 등록에 실패했습니다.";
		int memIdx=(int)session.getAttribute("mem_idx");
		
		int row = crewService.crewCreateDo(crew_logo_photo,crew_recru_photo,memIdx,param);
		if(memIdx!=0) {
			if(row == 1) {
				page ="redirect:/";
				msg="크루등록이 완료되었습니다!";
			}
			model.addAttribute("msg",msg);
		}		
		return page;
	}
	

	@RequestMapping(value="/crew/activity/write.do", method = RequestMethod.POST)
	public String crewActivityWrite(MultipartFile crew_activity_photo, HttpSession session, 
			@RequestParam Map<String,Object> param, int crew_idx, Model model) {		
		
		int memIdx =(int) session.getAttribute("mem_idx");
		String page="redirect:/";
		String msg = "크루 등록에실패했습니다.";		
		
		if(memIdx!=0) {	
				crewService.crewActivityWrite(crew_activity_photo,memIdx,param,crew_idx);
			
				logger.info("crewActivityWrite 시작");
				logger.info("활동등록하는 크루 idx"+crew_idx);
				logger.info("crew_activity_photo : {}"+ crew_activity_photo);
				logger.info("param_crew_idx : {}"+param.get("crew_idx"));
				msg="크루 등록이 완료되었습니다.";
				
			model.addAttribute("msg",msg);
		}		
		return "redirect:/crew/detail.go?crew_idx="+param.get("crew_idx");
	}
	
	
	@RequestMapping(value="/crew/crewOverlay.do")
	@ResponseBody
	public Map<String, Object> crewOverlay(String crew_name){
		logger.info("crew_name =  "+crew_name);
		Map<String, Object>map = new HashMap<String, Object>();
		map.put("use",crewService.crewOverlay(crew_name));
		return map;
	}
	
	@RequestMapping(value="/crew/crewUpdateForm.go")
	public String crewUpdateFormGo(Model model, HttpSession session, int crew_idx) {
		String page="redirect:/crewDetail";
		logger.info("update form idx = "+crew_idx);
		crewService.crewUpdateForm(crew_idx,model);
		page="crew/crewUpdateForm";		

	    logger.info("loginInfo : "+session.getAttribute("mem_id"));
	    logger.info("memIdx : "+session.getAttribute("mem_idx"));
		
		String memId= (String) session.getAttribute("mem_id");
		if(memId!=null) {
			page="crew/crewUpdateForm";
		}
	    
		
		return page;
	}
	
	@RequestMapping(value="/crew/crewUpdate.do",method = RequestMethod.POST)
	public String crewUpdateDo(MultipartFile crew_logo_photo,MultipartFile crew_recru_photo, @RequestParam Map<String, String>param){
		logger.info("param : {}",param);
		//int crew_idx=1;
		String page="redirect:/crewDetail";
		
		crewService.crewUpdate(param,crew_logo_photo,crew_recru_photo);
		page="redirect:/detail?crew_idx="+param.get("crew_idx");
		logger.info("page : "+page);
		
		return page;
	}
	
	@RequestMapping(value="/crew/crewList.go", method = RequestMethod.GET)
	public String crewListGo() {
		logger.info("crewList 진입");
		String page= "crew/crewList";
		return page;
	}
	
	
	@RequestMapping(value="/crew/crewList.ajax", method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> crewListCall(String filterType,String page, String cnt,Integer crew_idx) {
	    logger.info("crewList.ajax");
	    logger.info("filterType : {}"+filterType);
	    logger.info("페이지 당 보여줄 갯수 : "+cnt);
		logger.info("요청 페이지 : " +page);
	    
	    int currentPage = Integer.parseInt(page); // 현재 보여지는 페이지
	    int pagePerCnt = Integer.parseInt(cnt);   // 페이지당 보여줄 개수
	    
	    Map<String,Object> map = crewService.crewList(filterType,currentPage, pagePerCnt,crew_idx);
	    
	    return map;
	}
	
	@RequestMapping(value="/crew/searchCrew.ajax", method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> searchCrew(String Page, String cnt,String keyword) {
	    int currentPage = Integer.parseInt(Page);
	    int pagePerCnt = Integer.parseInt(cnt);
	    logger.info("페이지당 보여줄 갯수 : "+Page);
	    logger.info("요청 페이지 : "+cnt);
	    logger.info("searchKeyword : "+keyword);
	    
	    Map<String,Object> map = crewService.searchCrew(keyword,currentPage,pagePerCnt);
	    
	    return map;
	}
	
	@RequestMapping(value="/crew/apply.ajax")
	@ResponseBody
	public Map<String, Object> crewApply(Integer mem_idx, Integer crew_idx,HttpSession session) {
		
	    Map<String, Object> response = new HashMap<String, Object>();
		int memIdx=(Integer)session.getAttribute("mem_idx");
		logger.info("memidx : {}", memIdx);
	    
	    String result = crewService.applyCrew(memIdx, crew_idx);
	    
	    if(result.equals("success")) {
	        response.put("success", "신청이 완료되었습니다.");
	    } else if(result.equals("isApplying")) {
	        response.put("error", "이미 신청 중 크루가 있습니다.");
	    } else {
	        response.put("error", "현재 가입된 크루가 있습니다.");
	    }
	    
	    return response;        
	}
	
	@RequestMapping(value="/crew/CoolCheck.ajax")
	@ResponseBody
	public Map<String, Object> crewCoolCheck(Integer crew_idx, HttpSession session){		
		int memIdx=(Integer)session.getAttribute("mem_idx");
		Map<String, Object> map = new HashMap<String, Object>();
		logger.info("crewCoolCheck");
		logger.info("mem_idx : {}",memIdx);

		if(memIdx!=0) {
			map.put("crewCool", crewService.crewCoolCheck(memIdx, crew_idx));
		}		
		return map;		
	}
	
	@RequestMapping(value="/crew/detail.go",method = RequestMethod.GET)	
	public String crewDetailGo(Model model, HttpSession session, String row, String crew_idx) {
	    logger.info("crewDetail 들어간다.");
	    String page="crew/detail";
	    String memId= (String) session.getAttribute("mem_id");
	    int memIdx=(int)session.getAttribute("mem_idx");
	    
		model.addAttribute("mem_idx",memId);	    
	    logger.info("loginInfo : "+session.getAttribute("mem_id"));
	    logger.info("memIdx : "+session.getAttribute("mem_idx"));
	    model.addAttribute("mem_idx","memIdx");	    
	    
	    CrewDTO crew= new CrewDTO();
	    logger.info("crew"+crew);
	    
	    if(session.getAttribute("mem_idx")!=null) {
	    	String crew_state= crewService.stateCheck(crew_idx);
		    
	    	crew= crewService.detail(crew_idx,memId,memIdx);
		    	   model.addAttribute("crew",crew);
		    	   logger.info("else session in, memIdx : " +memId);
		    	   logger.info("else session in, row = "+row);
		    	   logger.info("else session in, crew_idx = "+crew_idx);   
		    	   if(row !=null) {
		    		   model.addAttribute("msg","크루가 정상적으로 삭제되었습니다.");
		    	   }
		   }
	    return page;
	}
	
	@RequestMapping(value="/crew/detail.ajax",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> detailCrewMember(String crew_idx, HttpSession session,
			String page, String cnt){
		
		int memIdx =(Integer)session.getAttribute("mem_idx");		
		int currentPage = Integer.parseInt(page); // 현재 보여지는 페이지
	    int pagePerCnt = Integer.parseInt(cnt);   // 페이지당 보여줄 개수
	    Map<String,Object> map = new HashMap<String, Object>();
		
		if(memIdx!=0) {
			logger.info("detail.ajax들어간다");
			logger.info("크루 번호 : "+crew_idx);
			logger.info("페이지 당 보여줄 갯수 : "+cnt);
			logger.info("요청 페이지 : " +page);
			map=crewService.detailCrewMember(currentPage, pagePerCnt,crew_idx);
		}
		return map;
	}
	
	@RequestMapping(value="/crew/activityList.ajax",method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> activityList(String crew_idx, HttpSession session, String page, String cnt,String crew_activity_details_idx){
		logger.info("activityList 들어왔다.");
		logger.info("크루 번호 : "+crew_idx);
    	logger.info("페이지당 보여줄 개수 : "+cnt);
    	logger.info("요청페이지 : "+page);
		
		int memIdx =(Integer)session.getAttribute("mem_idx");
		int currentPage = Integer.parseInt(page); // 현재 보여지는 페이지
	    int pagePerCnt = Integer.parseInt(cnt);   // 페이지당 보여줄 개수
	    Map<String,Object> map =crewService.activityList(currentPage, pagePerCnt,crew_idx);
	    	    
	    return map;
	}
	
	@RequestMapping(value="/crew/report.ajax",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> report(String crew_idx,HttpSession session, String repContent) {
		
		Map<String,Object>map=new HashMap<String, Object>();		
		int memIdx=(Integer)session.getAttribute("mem_idx");
		
		if(memIdx!=0 ) {
			logger.info("report.do 들어간다.");
			logger.info("신고사유 : "+repContent);
			logger.info("크루번호 : "+crew_idx);
			logger.info("memIdx : "+memIdx);
			crewService.report(crew_idx,repContent,memIdx);
		}
		return map;
	}
	
	@RequestMapping(value="/crew/delete.ajax",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> crewDel(HttpSession session,int crew_idx,String delContent) {
		
		int memIdx=(Integer)session.getAttribute("mem_idx");
		Map<String,Object>map=new HashMap<String, Object>();	
		
		if(memIdx!=0) {
			logger.info("crewDel 드가자");
			logger.info("삭제 사유 : "+delContent);
			logger.info("크루번호 : "+crew_idx);
			logger.info("memIdx : "+memIdx);
			crewService.crewDelete(crew_idx,memIdx);
			crewService.deleteReason(crew_idx,delContent,memIdx);
		}
		return map;
	}
	
	@RequestMapping(value="/crew/out.do",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> crewOut(HttpSession session, int crew_idx) {
		
		int memIdx=(Integer)session.getAttribute("mem_idx");
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(memIdx!=0) {
			logger.info("crewOut 시작");
			logger.info("탈퇴할 크루 idx : "+crew_idx);
			logger.info("memIdx : "+memIdx);
			crewService.crewOut(crew_idx,memIdx);
		}		
		return map;
	}
	

	@RequestMapping(value="/crew/deport.ajax",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> memberDeport(HttpSession session,String crewMem_idx,String crew_idx){
		
		int memIdx=(Integer)session.getAttribute("mem_idx");
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(memIdx!=0) {
			logger.info("crew 추방 시작");
			logger.info("추방할 멤버 idx : "+crewMem_idx);
			logger.info("추방시키는 크루idx : "+crew_idx);
			crewService.memberDeport(crew_idx,crewMem_idx);
		}		
		return map;
	}

	@RequestMapping(value="/crew/delegate.ajax", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> crewChiefDelegate(HttpSession session,String crewMem_idx, String crew_idx,
			String delgateContent){
		
		int memIdx=(Integer)session.getAttribute("mem_idx");
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(memIdx!=0) {
			logger.info("크루장 위임 시작");
			logger.info("위임할 멤버 idx : "+crewMem_idx);
			logger.info("위임시키는 크루idx : "+crew_idx);
			logger.info("위임 사유 : "+delgateContent);
			crewService.crewChiefDelegate(crew_idx,crewMem_idx,memIdx,delgateContent);
		}		
		return map;
	}
	
	@RequestMapping(value = "/crew/judgeList.go")
	public String judgeListGo(HttpSession session, String crew_idx, Model model) {
		String page = "member/login";
		crew_idx = "1";
		if (session.getAttribute("mem_id") != null) {
			model.addAttribute("crew_idx", crew_idx);
			page = "crew/judgeList";
		}
		return page; 
	}
	
	@RequestMapping(value = "/crew/appList.ajax", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> crewAppList(String crew_idx,String cnt, String page){
		logger.info("crew_idx : {}", crew_idx);
		logger.info("페이지당 보여줄 갯수 : " + cnt);
		logger.info("요청 페이지 : " + page);
		
		int currPage = Integer.parseInt(page);
		int pagePerCnt = Integer.parseInt(cnt);
		Map<String, Object> map =  crewService.appList(crew_idx,currPage, pagePerCnt);
		return map;
	}
	
	@RequestMapping(value = "/crew/approve.ajax", method = RequestMethod.POST)
	@ResponseBody
	public void approve(String mem_idx, String crew_idx) {
		crewService.approve(mem_idx,crew_idx);
	}
	
	@RequestMapping(value="/crew/activityDel.ajax",method = RequestMethod.POST)
	@ResponseBody
	public void activityDel(int crew_activity_details_idx) {
		logger.info("crew_activity_details_idx : {} "+crew_activity_details_idx);
		crewService.activityDel(crew_activity_details_idx);
	}
	
	
	
	
	
	
	
	
	
	
	
}
