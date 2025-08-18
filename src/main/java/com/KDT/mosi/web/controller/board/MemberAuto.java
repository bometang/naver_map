package com.KDT.mosi.web.controller.board;

import com.KDT.mosi.domain.dev.DevDao;
import com.KDT.mosi.domain.entity.Member;
import com.KDT.mosi.domain.entity.Role;
import com.KDT.mosi.domain.member.dao.MemberDAO;
import com.KDT.mosi.domain.member.dao.MemberRoleDAO;
import com.KDT.mosi.domain.member.svc.MemberSVC;
import com.KDT.mosi.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Controller
@RequestMapping("/dev")
@RequiredArgsConstructor
public class MemberAuto {

  private final MemberDAO memberDAO;
  private final MemberSVC memberSVC;
  private final MemberRoleDAO memberRoleDAO;
  private final DevDao devDAO;

//  @GetMapping("/test-login")
//  public String testLogin(
//      HttpSession session,
//      Model model
//  ) {
//    // 예시로 Member 엔티티를 직접 new 하지 않고, LoginMember DTO에 id만 담음
//    Member loginMember = new Member(1L,"minjun","a","a",null,null,null);
//    // 세션에 저장
//    session.setAttribute("loginMember", loginMember);
//    session.setAttribute("loginMemberId", 1L);
//
//    return "redirect:/";
//  }


  /**
   * member_id = 1 인 사용자를 세션에 로그인 처리
   */
  @GetMapping("/test-login")
  public String testLogin(HttpServletRequest request) {
    // 1) 임시 사용자 조회
    Member member = memberSVC.findByEmail("a")
        .or(() -> memberSVC.findByEmail("shinnosuke@naver.com"))
        .or(() -> memberSVC.findByEmail("test5@kh.com"))
        .orElseThrow(() -> new IllegalStateException(
            "dev(a), shinnosuke@naver.com, test5@kh.com 계정이 모두 없습니다."
        ));

    List<Role> roles = memberRoleDAO.findRolesByMemberId(member.getMemberId());

    // 2) Authentication 객체 준비
    UserDetails userDetails = new CustomUserDetails(member,roles);   // ← UserDetails 구현체
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

    // 3) SecurityContextHolder(현재 쓰레드)에 저장
    SecurityContext context = SecurityContextHolder.createEmptyContext();   // ⭐ 추가
    context.setAuthentication(auth);                                        // ⭐ 추가
    SecurityContextHolder.setContext(context);

    // 4) 세션에 SecurityContext + 프로필 캐시 저장
    HttpSession session = request.getSession(true);
    session.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,   // ⭐ 핵심 키
        context);                                                            // ⭐ 추가
    session.setAttribute("loginMember", member);        // (옵션) 뷰용 캐시
    session.setAttribute("loginMemberId", member.getMemberId());

    return "redirect:/";   // 필터 체인을 통과하는 경로로 이동
  }

  @GetMapping("/update-pics")
  public String updateAllPics(HttpSession session) throws IOException {
    // 두 경로를 미리 정의
    String primaryBaseDir   = "C:/KDT/projects/realBbs/src/main/resources/static/img/member";
    String secondaryBaseDir = "D:/study/project/realBbs/src/main/resources/static/img/member";

// 존재 여부 검사 후 baseDir 결정
    String baseDir;
    File dir = new File(primaryBaseDir);
    if (dir.exists() && dir.isDirectory()) {
      baseDir = primaryBaseDir;
    } else {
      baseDir = secondaryBaseDir;
    }

    String[] fileNames = {
        "1_인물.jpg",
        "2_인물.png",
        "3_인물.png",
        "4_인물.jpg",
        "5_인물.jpg",
        "6_인물.jpg",
        "7_인물.jpg",
        "8_인물.png"
    };
    for (int i = 0; i < fileNames.length; i++) {
      long memberId = i + 1L;
      Path imgPath = Path.of(baseDir, fileNames[i]);
      byte[] picBytes = Files.readAllBytes(imgPath);
      memberDAO.updatePic(memberId, picBytes);
    }

    // (원하시면 세션에 새로 갱신된 Member 객체를 담아주실 수도 있습니다)
    return "redirect:/";
  }

  @GetMapping("/pro")
  public String seedProduct1() {
    String base = new File("C:/KDT/img/product").isDirectory()
        ? "C:/KDT/img/product" : "D:/study/img/product";

    devDAO.insertProductWithImagesFixedId(
        1L,                 // PRODUCT_ID 고정 = 1
        6L,                 // MEMBER_ID
        "area",             // CATEGORY
        "바다로 가자",        // TITLE
        "Y",                // GUIDE_YN
        100000L,            // NORMAL_PRICE
        1500000L,           // GUIDE_PRICE
        80000L,             // SALES_PRICE
        1100000L,           // SALES_GUIDE_PRICE
        3,                  // TOTAL_DAY
        10,                 // TOTAL_TIME
        70000L,             // REQ_MONEY
        "Y",                // SLEEP_INFO
        "전철",              // TRANSPORT_INFO
        "Y",                // FOOD_INFO
        "10",               // REQ_PEOPLE
        "수영 가능",          // TARGET
        "갈아입을 옷",        // STUCKS
        "바다로",             // DESCRIPTION
        "가십다",             // DETAIL
        base,               // PRODUCT.FILE_* 경로
        "제7회 K-디지털 트레이닝 해커톤_훈련증빙자료 안내.pdf",
        base,               // 이미지 경로
        List.of("바다1.jpg","바다2.jpg","바다3.jpg","바다4.jpg")
    );
    return "redirect:/";
  }

}
