package com.green.user.resume.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.green.applicaions.vo.ApplicaionVo;
import com.green.application.mapper.ApplicationsMapper;
import com.green.company.recruit.vo.CompanyRecruitVo;
import com.green.region.vo.RegionVo;
import com.green.skills.vo.SkillVo;
import com.green.user.resume.mapper.UserResumeMapper;
import com.green.user.resume.vo.UserResumeVo;
import com.green.users.mapper.UserMapper;
import com.green.users.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping( "/Resume" )
public class UserResumeController {


   ModelAndView mv = new ModelAndView();
   
   @Autowired
   private UserResumeMapper userResumeMapper;
   
   @Autowired
   private UserMapper userMapper;
   
   @Autowired
   private ApplicationsMapper applicationsMapper;
   
   // 채용공고에서 자신의 이력서 ajax로 가져오기
   @RequestMapping("/ResumeListSubmit")
   @ResponseBody
   public List<UserResumeVo> ResumeListSubmit (HttpSession session) {
      
      UserVo userVo = (UserVo)session.getAttribute("userLogin");

      List<UserResumeVo>  userResumeList = userResumeMapper.getUserResumeListData( userVo );
    
      return userResumeList;
      
   } 
   
   
   /*이력서 목록*/
   @RequestMapping( "/ResumeList" )
   public  ModelAndView  resumelist( UserResumeVo userResumeVo, UserVo userVo, CompanyRecruitVo companyRecruitVo  ) {
      ModelAndView  mv  =  new ModelAndView();
       String user_id = userVo.getUser_id();
       List<HashMap<String, String>> userResumeList = userResumeMapper.getUserResumeList( user_id );
      mv.addObject( "userResumeList", userResumeList );
      mv.setViewName( "users/resume/list" );
      return mv;
   }
   
   /*이력서 등록*/
   @RequestMapping( "/RegisterResumeForm" )
   public ModelAndView resumeregisterForm( UserResumeVo userResumeVo, UserVo userVo ) {
       ModelAndView mv = new ModelAndView();
       String user_id = userVo.getUser_id();
       mv.addObject( "user_id", user_id );
        mv.addObject( "user_name", userMapper.getUserById( user_id ).getUser_name() );
        mv.addObject( "user_phone", userMapper.getUserById( user_id ).getUser_phone() );
        mv.addObject( "user_email", userMapper.getUserById( user_id ).getUser_email() );
       
       List<SkillVo> skillList = userResumeMapper.getSkill();
       mv.addObject( "skillList", skillList );

       List<RegionVo> regionList = userResumeMapper.getRegion(); 
       mv.addObject( "regionList", regionList );

       mv.setViewName( "users/resume/registerform" );
       return mv;
   }
   
    @RequestMapping( "/SearchAddress" )
    public String serarchAddress() {
        return "users/resume/popupaddress";
    }
   
   @RequestMapping( "/RegisterResume" )
   public ModelAndView resumeregister( HttpServletRequest request, UserResumeVo userResumeVo ) {
      
      Map<String, String[]> userResumemap = request.getParameterMap();
      String [] skills = userResumemap.get("skill_name");
      
      String user_id = userResumeVo.getUser_id();
      
      List<SkillVo> skillList = new ArrayList<>();
      
      for ( int i = 0; i < skills.length; i++ ) {
         SkillVo skillVo = new SkillVo();
         skillVo.setSkill_name( skills[i] );
         skillList.add( skillVo );
      };
      
      userResumeMapper.insertUserResume( userResumeVo );
      
      userResumeVo.setUser_resume_idx( userResumeMapper.getUserResumeIdx( user_id ) );
      
      int user_resume_idx = userResumeVo.getUser_resume_idx();
      
      userResumeMapper.insertUserSkill( user_resume_idx, skillList );


      ModelAndView mv = new ModelAndView();
      mv.setViewName( "redirect:/Resume/ViewResume?user_id=" + user_id + "&user_resume_idx=" + user_resume_idx );
      return mv;
   }
   
   
   /* 이력서 보기 */
   @RequestMapping( "/ViewResume" )
   public ModelAndView resume( UserResumeVo userResumeVo, UserVo userVo ) {
      UserResumeVo userResume = userResumeMapper.getUserResume( userResumeVo );
      ModelAndView mv = new ModelAndView();
       String user_id = userVo.getUser_id();
       List<RegionVo> regionList = userResumeMapper.getRegion(); 
       mv.addObject( "regionList", regionList );
       
       List<SkillVo> skillList = userResumeMapper.getSkill();
       mv.addObject( "skillList", skillList );
       
       List<SkillVo> selectedSkills = userResumeMapper.getSelectedSkills(userResume.getUser_resume_idx());
       mv.addObject( "selectedSkills", selectedSkills );
       
       mv.addObject( "user_id", user_id );
        mv.addObject( "userResume", userResume );
      mv.setViewName( "users/resume/view" );
      return mv;
      
   }
   
   /* 이력서 수정 */
   @RequestMapping( "/UpdateResumeForm" )
   public  ModelAndView  updateResumeForm( UserResumeVo userResumeVo, UserVo userVo) {
      UserResumeVo userResume = userResumeMapper.getUserResume( userResumeVo );
      ModelAndView mv = new ModelAndView();
       String user_id = userVo.getUser_id();
       List<RegionVo> regionList = userResumeMapper.getRegion(); 
       mv.addObject( "regionList", regionList );
       
       List<SkillVo> skillList = userResumeMapper.getSkill();
       mv.addObject( "skillList", skillList );
       
       List<SkillVo> selectedSkills = userResumeMapper.getSelectedSkills(userResume.getUser_resume_idx());
       mv.addObject( "selectedSkills", selectedSkills );
       
       mv.addObject( "user_id", user_id );
        mv.addObject( "userResume", userResume );
      mv.setViewName( "users/resume/updateform" );
      return mv;
   }
   

   @RequestMapping( "/UpdateResume" )
   public  ModelAndView  updateResume( UserResumeVo userResumeVo, UserVo userVo, HttpServletRequest request ) {
      ModelAndView mv = new ModelAndView();
      String user_id = userResumeVo.getUser_id();
      userResumeMapper.updateUserResume( userResumeVo );
      userResumeMapper.deleteUserResumeSkills( userResumeVo );
      //-----
      Map<String, String[]> userResumemap = request.getParameterMap();
      String [] skills = userResumemap.get("skill_name");
      
      List<SkillVo> skillList = new ArrayList<>();
      
      for ( int i = 0; i < skills.length; i++ ) {
         SkillVo skillVo = new SkillVo();
         skillVo.setSkill_name( skills[i] );
         skillList.add( skillVo );
      };
      
      userResumeVo.setUser_resume_idx( userResumeMapper.getUserResumeIdx( user_id ) );
      int user_resume_idx = userResumeVo.getUser_resume_idx();
      userResumeMapper.insertUserSkill( user_resume_idx, skillList );

        mv.setViewName( "redirect:/Resume/ResumeList?user_id=" + user_id );


      return mv;
   }
   
   /*이력서 삭제*/
   @RequestMapping( "/DeleteResume" )
   public  ModelAndView delete( UserResumeVo userResumeVo ) {
      applicationsMapper.deleteApplicationResume( userResumeVo );
      userResumeMapper.deleteUserResumeSkills( userResumeVo );
      userResumeMapper.deleteUserResume( userResumeVo );
      ModelAndView  mv  =  new ModelAndView();
      userResumeMapper.deleteUserResume( userResumeVo );
      String user_id = userResumeVo.getUser_id();
      mv.setViewName( "redirect:/Resume/ResumeList?user_id=" + user_id );
      return mv;
   }
   

   /* 이력서 지원 */

   // 이력서 지원하는 기능

   @RequestMapping("/ResumeSubmit")
   public ModelAndView resumeSubmit( UserResumeVo userResumeVo, CompanyRecruitVo companyRecruitVo ) {
      
      ModelAndView mv = new ModelAndView();
      
      ApplicaionVo applicationVo = new ApplicaionVo();
      applicationVo.setApplication_status("서류검토중");
      applicationVo.setUser_resume_idx( userResumeVo.getUser_resume_idx() );
      applicationVo.setCompany_recruit_idx( companyRecruitVo.getCompany_recruit_idx() );
      
      int count = applicationsMapper.countApplication(applicationVo);
      String message = "";
      if(count != 0) {
         message = null;
      };
      if(count == 0) {
               applicationsMapper.setApplicationData(applicationVo);
         message = "지원성공";
      };
      mv.addObject("message", message);
      mv.setViewName("redirect:/Common/RecruitInfo?company_recruit_idx="+companyRecruitVo.getCompany_recruit_idx());
      return mv;
      
   }
   


}
   