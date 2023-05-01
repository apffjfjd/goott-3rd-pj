package com.goott.pj3.travelinfo.controller;


import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.goott.pj3.common.util.aws.S3FileUploadService;
import com.goott.pj3.common.util.paging.Criteria;
import com.goott.pj3.travelinfo.dto.TravelInfoDTO;
import com.goott.pj3.travelinfo.service.TravelInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/travelinfo/**")
public class TravelInfoController {

	@Autowired
	TravelInfoService travelInfoService;

	// AWS S3 파일 업로드
	@Autowired
	S3FileUploadService s3FileUploadService;

	/**
	 * 23.04.07. 여행지 정보 생성 페이지 호출
	 *
	 * @return
	 */
	@GetMapping("create")
	public ModelAndView create() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/travelinfo/travelinfo_create");
		return mv;
	}

	/**
	 * 조원재 23.04.07 여행지 정보 생성
	 * 23.04.26 이미지 파일 업로드 기능 추가
	 *
	 * @param travelInfoDTO
	 * @param mv
	 * @param httpSession
	 * @param multipartFile
	 * @return
	 */
	@PostMapping("create")
	public ModelAndView CreatePost(TravelInfoDTO travelInfoDTO, ModelAndView mv, HttpSession httpSession,
								   @RequestParam("file[]") List<MultipartFile> multipartFile) {
		String user_id = (String) httpSession.getAttribute("user_id"); // 로그인한 유저 아이디 세션
		System.out.println("user_id : " + user_id);
		travelInfoDTO.setUser_id(user_id); // DTO에 유저 아이디 할당
		int travel_location_idx = this.travelInfoService.create(travelInfoDTO); // 생성된 게시글 idx
		ImgFileUpload(travelInfoDTO, multipartFile, travel_location_idx); // 이미지 파일 업로드 API
		if (travel_location_idx != 0) {
			mv.setViewName("redirect:/travelinfo/detail/" + travel_location_idx);
		} else {
			mv.setViewName("travelinfo/travelinfo_create");
		}
		return mv;
	}

	/**
	 * 조원재 23.04.26 이미지 파일 업로드 API
	 *
	 * @param travelInfoDTO
	 * @param multipartFile
	 * @param travel_location_idx
	 */
	private void ImgFileUpload(TravelInfoDTO travelInfoDTO, List<MultipartFile> multipartFile, int travel_location_idx) {
		try {
			if (multipartFile != null && !multipartFile.isEmpty()) { // 이미지 파일이 존재하는 경우
				List<String> imgList = s3FileUploadService.upload(multipartFile);
				travelInfoDTO.setT_img(imgList);
				travelInfoDTO.setTravel_location_idx(travel_location_idx);
				this.travelInfoService.createImg(travelInfoDTO);
			} else { // 이미지 파일이 없는 경우
				travelInfoDTO.setTravel_location_idx(travel_location_idx);
				this.travelInfoService.createImg(travelInfoDTO);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 조원재 23.04.08 여행지 정보 디테일 페이지 호출
	 */
	@GetMapping("detail/{travel_location_idx}")
	public ModelAndView detail(@PathVariable int travel_location_idx,
							   TravelInfoDTO travelInfoDTO, ModelAndView mv) {
		travelInfoDTO.setTravel_location_idx(travel_location_idx);
		TravelInfoDTO detail = this.travelInfoService.detail(travelInfoDTO);
		mv.addObject("data", detail);
		mv.setViewName("travelinfo/travelinfo_detail");
		return mv;
	}

	@GetMapping("update/{travel_location_idx}")
	public ModelAndView update(@PathVariable int travel_location_idx,
							   TravelInfoDTO travelInfoDTO, ModelAndView mv) {
		travelInfoDTO.setTravel_location_idx(travel_location_idx);
		TravelInfoDTO detail = this.travelInfoService.detail(travelInfoDTO); // 게시글 정보
		mv.addObject("data", detail); // 게시글 정보
		mv.setViewName("travelinfo/travelinfo_update");
		return mv;
	}

	@PostMapping("update/{travel_location_idx}")
	public ModelAndView updatePost(@PathVariable int travel_location_idx, TravelInfoDTO travelInfoDTO,
								   ModelAndView mv,
								   @RequestParam("file[]") List<MultipartFile> multipartFile) {
		System.out.println("multipartFile : " + multipartFile.toString());
		travelInfoDTO.setTravel_location_idx(travel_location_idx);
		int succeessIdx = this.travelInfoService.update(travelInfoDTO); // 본문 내용 업데이트 (이미지 제외)
		for(String fileName : this.travelInfoService.detail(travelInfoDTO).getT_img()){ // URL주소 하나씩 가져와서
			s3FileUploadService.deleteFromS3(fileName); // 서버에서 삭제
		}
		boolean success = this.travelInfoService.deleteImg(travelInfoDTO); // 기존 img 삭제
		ImgFileUpdate(travel_location_idx, travelInfoDTO, mv, multipartFile, succeessIdx); // 이미지 파일 업데이트 API
		mv.setViewName("redirect:/travelinfo/detail/" + travel_location_idx);
		return mv;
	}

	/**
	 * 조원재 23.05.02. 이미지 파일 업데이트 API
	 * @param travel_location_idx
	 * @param travelInfoDTO
	 * @param mv
	 * @param multipartFile
	 * @param succeessIdx
	 */
	private void ImgFileUpdate(int travel_location_idx, TravelInfoDTO travelInfoDTO,
							   ModelAndView mv, List<MultipartFile> multipartFile, int succeessIdx) {
		try {
			if (multipartFile !=null || !multipartFile.isEmpty()) {
				List<String> imgList = s3FileUploadService.upload(multipartFile);
				travelInfoDTO.setT_img(imgList);
				travelInfoDTO.setTravel_location_idx(succeessIdx);
				this.travelInfoService.updateImg(travelInfoDTO);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 조원재 23.04.08. 여행지 정보 삭제
	 * @param travel_location_idx
	 * @param travelInfoDTO
	 * @param mv
	 * @return
	 */
	@PostMapping("delete/{travel_location_idx}")
	public ModelAndView delete(@PathVariable int travel_location_idx,
							   TravelInfoDTO travelInfoDTO, ModelAndView mv) {
		travelInfoDTO.setTravel_location_idx(travel_location_idx);
		boolean success = this.travelInfoService.delete(travelInfoDTO); // 게시글 삭제(이미지 제외)
		try {
			TravelInfoDTO detailDTO = this.travelInfoService.detail(travelInfoDTO); // 리뷰 상세 정보 가져오기
			if (detailDTO != null && detailDTO.getT_img() != null){
				for(String fileName : detailDTO.getT_img()){
					s3FileUploadService.deleteFromS3(fileName);
				}
			}
			this.travelInfoService.deleteImg(travelInfoDTO); // 이미지 삭제
		} catch (Exception e) {
			//예외 처리
			System.out.println("여행정보 삭제 중 오류가 발생했습니다 : " + e.getMessage());
			mv.setViewName("/error/500");
		}
		if (success) {
			mv.setViewName("redirect:/travelinfo/list");
		} else {
			mv.setViewName("redirect:/travelinfo/detail/" + travel_location_idx);
		}
		return mv;
	}

	/**
	 * 조원재 23.04.08. 리스트 조회, 검색, 페이징
	 * @param mv
	 * @param cri
	 * @param travelInfoDTO
	 * @return
	 */
	@RequestMapping("list")
	public ModelAndView list(ModelAndView mv, Criteria cri, TravelInfoDTO travelInfoDTO) {
		try {
			List<TravelInfoDTO> originalList = travelInfoService.imgList(travelInfoDTO);
			System.out.println("originalList : " + originalList);
			List<TravelInfoDTO> newList = new ArrayList<>(); // 인덱스와 첫번째 이미지만 담을 List 생생
			for (TravelInfoDTO dto : originalList) {
				List<String> tImgList = dto.getT_img(); // 이미지만 List에 담기
				if (tImgList != null && !tImgList.isEmpty()) { // 이미지가 있는 경우
					String firstImg = tImgList.get(0); // 첫번째 이미지 변수에 담기
					TravelInfoDTO newDto = new TravelInfoDTO(); // 인덱스+첫번째 이미지 값 담을 dto
					newDto.setTravel_location_idx(dto.getTravel_location_idx()); // 인덱스 담기
					newDto.setT_img(Collections.singletonList(firstImg)); // 첫번째 이미지 담기
					newList.add(newDto);
				}
			}
			System.out.println("newList" + newList);
			mv.addObject("imgList", newList);
			mv.addObject("paging", travelInfoService.paging(cri));
			mv.addObject("data", travelInfoService.list(cri));
			mv.setViewName("/travelinfo/travelinfo_list");
		} catch (Exception e) {
			// 예외 처리
			System.out.println("여행정보 목록 조회 중 오류가 발생했습니다 : " + e.getMessage());
			mv.setViewName("error/500");
		}
		return mv;
	}
}
