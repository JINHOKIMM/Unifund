package com.uni.fund.admin.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uni.fund.admin.dao.AnnouncementDAO;
import com.uni.fund.admin.dto.AnnouncementDTO;
import com.uni.fund.project.dto.ProjectDTO;

@Service
public class AnnouncementService {

	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired AnnouncementDAO annDAO;
	
	public String file_root = "C:/upload/Unifund/";

	public Map<String, Object> annList(int currPage, int pagePerCnt) {
		int start = (currPage-1) * pagePerCnt;
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<AnnouncementDTO> annList = annDAO.annList(start,pagePerCnt);
		result.put("annList", annList);
		result.put("currPage", currPage);
		result.put("totalPages", annDAO.allCount(pagePerCnt));
		logger.info("result:{}",result);
		
		return result;
	}

	public int adminAnnForm(MultipartFile noti_photo, Map<String, String> param, Integer mng_idx) {
		
		int row = -1;
		
		AnnouncementDTO announcementDTO = new AnnouncementDTO();
		announcementDTO.setMem_idx(mng_idx);
		announcementDTO.setNoti_title(param.get("noti_title"));
		announcementDTO.setNoti_content(param.get("noti_content"));
		announcementDTO.setNoti_top(param.get("noti_title"));
		
		row = annDAO.adminAnnForm(announcementDTO);
		
		if (row>0 ) {
			int noti_idx = announcementDTO.getNoti_idx();
			String noti_photoDiv = "공지사항";
			annFileSave(noti_idx, noti_photo,noti_photoDiv);
		}
		
	
		return row;
	}

	private void annFileSave(int noti_idx, MultipartFile noti_photo, String noti_photoDiv) {
		if (noti_photo != null) {
			String oriName = noti_photo.getOriginalFilename();
			String annPhoto = oriName.substring(oriName.lastIndexOf("."));
			String newAnnFileName = System.currentTimeMillis() + annPhoto;
			
			Path AnnPath = Paths.get(file_root + newAnnFileName);
			
			
			try {
				byte[] annBytes = noti_photo.getBytes();
				Files.write(AnnPath,annBytes);
				annDAO.createNotiPhoto(noti_idx, newAnnFileName, noti_photoDiv);
				Thread.sleep(1);
			} catch (Exception e) {
				logger.info(":::NotiFileSave Service FILE Exception:::");
				e.printStackTrace();
			}
			
			
			
			
			
		}
 		
	}

	public Map<String, Object> annListSearch(String keyWord, int currPage, int pagePerCnt) {
	    int start = (currPage - 1) * pagePerCnt;
	    
	    List<AnnouncementDTO> list = annDAO.annListSearch(keyWord, start, pagePerCnt); // 검색어를 LIKE 연산자로 감싸서 전달
	    Map<String, Object> result = new HashMap<String, Object>();
	    int totalCount = annDAO.searchAllCount(keyWord,pagePerCnt ); // 검색 결과에 대한 총 공지사항 수 가져오기
	    int totalPages = (int) Math.ceil(totalCount / pagePerCnt); // 전체 페이지 수 계산	
	    
	    logger.info(totalCount+"asdfasd"+totalPages);
	    
	    result.put("annList", list);
	    result.put("currPage", currPage);
	    result.put("totalPages", totalCount); // 검색어를 LIKE 연산자로 감싸서 전달
//	    logger.info(annDAO.searchAllCount(keyWord, pagePerCnt) + "asdfasd");
	    return result;
	}
	
	
	
	
	
//	
//	public Map<String, Object> annListSearch(String keyWord, int currPage, int pagePerCnt) {
//	    int start = (currPage - 1) * pagePerCnt;
//	    List<AnnouncementDTO> list = annDAO.annListSearch(keyWord, start, pagePerCnt);
//	    int totalCount = annDAO.searchAllCount("%" + keyWord + "%"); // 검색 결과에 대한 총 공지사항 수 가져오기
//	    int totalPages = (int) Math.ceil((double) totalCount / pagePerCnt); // 전체 페이지 수 계산
//	    Map<String, Object> result = new HashMap<String, Object>();
//	    result.put("annList", list);
//	    result.put("currPage", currPage);
//	    result.put("totalPages", totalPages); // 검색 결과에 대한 전체 페이지 수 전달
//	    return result;
//	}

	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
