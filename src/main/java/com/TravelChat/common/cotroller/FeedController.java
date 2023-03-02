package com.TravelChat.common.cotroller;

import com.TravelChat.common.model.Comment;
import com.TravelChat.common.model.Feed;
import com.TravelChat.common.service.CommentService;
import com.TravelChat.common.service.FeedService;
import com.TravelChat.common.service.PhotoService;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/feed")
public class FeedController {
    @Autowired
    private HttpSession session;
    @Autowired
    private MemberService memberService;
    @Autowired
    private FeedService feedService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/myFeed")
    public String myFeed(Model model) {
        int mNo = (int) session.getAttribute("mNo");
        Member member = memberService.selectByMno(mNo);
        String[] nickAndCode = memberService.separateNick(member.getNick());
        String nick = nickAndCode[0];
        String code = nickAndCode[1];
        String profilePhoto = memberService.selectPhotoByMno(mNo);
        // 최초에 가장 최근 5개만 불러오기
        List<Feed> myFeedList = feedService.selectRecentFeedListByMNo(mNo);

        for (Feed feed : myFeedList) {
            // feed 사진 set
            String[] photoArr = feed.getPhoto().split(",");
            if (photoArr.length > 1) { // 사진이 두장 이상이면
                for (String tempPhoto : photoArr) {
                    feed.getPhotoNames().add(tempPhoto);
                }
            }
            // 댓글 set
            List<Comment> commentList = commentService.selectCommentListByFeedNo(feed.getFeedNo());
            for(Comment comment : commentList){ // nick code 분리
                String[] CommenterNickAndCode = memberService.separateNick(comment.getNick());
                comment.setNick(CommenterNickAndCode[0]);
                comment.setCode(CommenterNickAndCode[1]);
            }
            feed.setCommentList(commentList);
        }
        int feedCount = feedService.countAllFeedByWriter(mNo);

        model.addAttribute("feedCount", feedCount);
        model.addAttribute("nick", nick);
        model.addAttribute("code", code);
        model.addAttribute("profilePhoto", profilePhoto);
        model.addAttribute("myFeedList", myFeedList);
        model.addAttribute("myNo", mNo);

        return "/feed/myFeed";
    }
    @GetMapping("/append")
    @ResponseBody
    public String appendFeed(@RequestParam("feedNo") int lastNo, @RequestParam("type") String type){
        int mNo = (int) session.getAttribute("mNo");
        Member member;
        String[] nickAndCode;
        String nick;
        String code;
        String profilePhoto;
        // lastNo 보다 작은 5개 feed 가져오기
        List<Feed> myFeedList;

        if(type.equals("info")){
            Feed lastFeed = feedService.selectFeedByFeedNo(lastNo);
            member = memberService.selectByMno(lastFeed.getWriter());
            nickAndCode = memberService.separateNick(member.getNick());
            profilePhoto = memberService.selectPhotoByMno(member.getMNo());
            myFeedList = feedService.selectAppendFeed(member.getMNo(), lastNo);
        }else{
            member = memberService.selectByMno(mNo);
            nickAndCode = memberService.separateNick(member.getNick());
            profilePhoto = memberService.selectPhotoByMno(mNo);
            myFeedList = feedService.selectAppendFeed(mNo, lastNo);
        }
        nick = nickAndCode[0];
        code = nickAndCode[1];



        if(myFeedList.isEmpty()){
            return "noMoreFeed";
        }
        for (Feed feed : myFeedList) {
            // feed 사진 set
            String[] photoArr = feed.getPhoto().split(",");
            if (photoArr.length > 1) { // 사진이 두장 이상이면
                for (String tempPhoto : photoArr) {
                    feed.getPhotoNames().add(tempPhoto);
                }
            }
            // 댓글 set
            List<Comment> commentList = commentService.selectCommentListByFeedNo(feed.getFeedNo());
            for(Comment comment : commentList){ // nick code 분리
                String[] CommenterNickAndCode = memberService.separateNick(comment.getNick());
                comment.setNick(CommenterNickAndCode[0]);
                comment.setCode(CommenterNickAndCode[1]);
            }
            feed.setCommentList(commentList);
        }

        String feedToHtml;
        if(type.equals("info")){
            feedToHtml = feedService.toHtmlForInfo(myFeedList, nick, code, profilePhoto, mNo);
        }else{
            feedToHtml = feedService.toHtml(myFeedList, nick, code, profilePhoto);
        }


        return feedToHtml;
    }


    @GetMapping("/writeFeed")
    public String writeFeed() {


        return "/feed/writeFeed";
    }

    @PostMapping("/writeFeed")
    public String submitFeed(RedirectAttributes rattr, @RequestParam("photos") List<MultipartFile> photos, Feed feed) {
        int mNo = (int) session.getAttribute("mNo");
        //작성자 set
        feed.setWriter(mNo);
        String result;
        try {
            feedService.saveFeedPhotos(photos, feed);
            result = "SUCCESS_WRITE";
        } catch (Exception e) {
            e.printStackTrace();
            result = "FAILED_WRITE";
        }

        rattr.addFlashAttribute("result", result);
        return "redirect:/feed/myFeed";
    }

    @DeleteMapping("/deleteFeed")
    @ResponseBody
    public String deleteFeed(@RequestParam("feedNo") int feedNo) {
        int mNo = (int) session.getAttribute("mNo");
        Feed feed = feedService.selectFeedByFeedNo(feedNo);
        if (feed.getWriter() != mNo) {
            return "INCORRECT_WRITER";
        } else {
            feedService.deleteFeed(feed);
            return "SUCCESS_DEL";
        }
    }

    @GetMapping("/modifyFeed")
    public String modifyFeedForm(@RequestParam("feedNo") int feedNo, Model model) {
        int mNo = (int) session.getAttribute("mNo");
        Feed feed = feedService.selectFeedByFeedNo(feedNo);
        String[] photoArr = feed.getPhoto().split(",");
        if (photoArr.length > 1) { // 사진이 두장 이상이면
            for (String tempPhoto : photoArr) {
                feed.getPhotoNames().add(tempPhoto);
            }
        }
        model.addAttribute("feed", feed);
        return "/feed/modifyFeed";
    }

    @PostMapping("/modifyFeed")
    public String modifyFeed(RedirectAttributes rattr, @RequestParam("photos") List<MultipartFile> photos, Feed feed) {
        int mNo = (int) session.getAttribute("mNo");
        Feed temp = feedService.selectFeedByFeedNo(feed.getFeedNo());
        feed.setWriter(mNo);
        String result = "";
        if (photos.get(0).getSize() == 0) { // 사진 업데이트가 없는 경우
            try {
                feedService.updateContent(feed);
                result = "SUCCESS_MODIFY";
            } catch (Exception e) {
                e.printStackTrace();
                result = "FAILED_MODIFY";
            }
        }else{
            try {
                feedService.modifyPhotos(photos, feed);
                result = "SUCCESS_MODIFY";
            } catch (Exception e) {
                e.printStackTrace();
                result = "FAILED_MODIFY";
            }
        }

        rattr.addFlashAttribute("result", result);
        return "redirect:/feed/myFeed";
    }
}
