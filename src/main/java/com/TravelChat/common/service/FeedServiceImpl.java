package com.TravelChat.common.service;

import com.TravelChat.common.model.Comment;
import com.TravelChat.common.model.Feed;
import com.TravelChat.common.repository.FeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class FeedServiceImpl implements FeedService {
    @Autowired
    private FeedRepository feedRepository;
    @Autowired
    private PhotoService photoService;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveFeedPhotos(List<MultipartFile> photos, Feed feed) throws Exception {
        // 1. 피드를 먼저 저장
        int feedNo = insertFeed(feed);
        feed.setFeedNo(feedNo);
        // 2. 저장한 피드no 으로 사진 dir 생성
        String path = "aws 경로"; //aws에 올리면 변경해야함
//        path = "C:/SoloProject/image/feed";
        path = "/var/lib/tomcat9/webapps/image/feed"; // 현재 내컴에 경로 저장
        path += "/" + feed.getWriter() + "/" + feedNo + "/";
        File createDir = new File(path);
        if (!createDir.exists()) {
            createDir.mkdir();
        }
        Date today = new Date();

        int photoCount = 1;
        String photoNameAtDB = "";
        for (MultipartFile photo : photos) {
            SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat time = new SimpleDateFormat("HHmmss"); // 24시간
            String prefix = date.format(today);
            prefix += time.format(today);
            String fileName = prefix + "_" + photoCount;
            String originalName = photo.getOriginalFilename();
            fileName = fileName + originalName.substring(originalName.lastIndexOf(".")); // 확장자 붙여주기

            File destination = new File(path + "/" + fileName);

            try {
                photo.transferTo(destination);
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception("IOException");
            } catch (IllegalStateException e) {
                e.printStackTrace();
                throw new Exception("IllegalStateException");
            }

            if (photoCount != photos.size()) { // 마지막 사진이 아니라면 , 붙여주기
                photoNameAtDB += fileName + ",";
            } else {
                photoNameAtDB += fileName;
            }
            photoCount++;
        }

        // 3. 사진 저장 (feed update)
        feed.setPhoto(photoNameAtDB);
        updatePhotos(feed);

    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertFeed(Feed feed) throws Exception {
        try {
            feedRepository.insertFeed(feed);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("FEED_INSERT_ERR");
        }
        return feed.getFeedNo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updatePhotos(Feed feed) throws Exception {

        try {
            feedRepository.updatePhotos(feed);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("UPDATE_ERR");
        }
    }

    @Override
    public List<Feed> selectRecentFeedListByMNo(int mNo) {
        return feedRepository.selectFeedListByMNo(mNo);
    }

    @Override
    public Feed selectFeedByFeedNo(int feedNo) {
        return feedRepository.selectFeedByFeedNo(feedNo);
    }

    @Override
    public void deleteFeed(Feed feed) {
        feedRepository.deleteFeed(feed);
    }

    @Override
    public void modifyPhotos(List<MultipartFile> photos, Feed feed) throws Exception {
        // 2. 저장한 피드no 으로 사진 dir 생성
        String path = "aws 경로"; //aws에 올리면 변경해야함
//        path = "C:/SoloProject/image/feed";
        path = "C:/apache-tomcat-9.0.52/webapps/image"; // 현재 내컴에 경로 저장
        path += "/" + feed.getWriter() + "/" + feed.getFeedNo() + "/";
        File createDir = new File(path);
        if (!createDir.exists()) {
            createDir.mkdir();
        }
        Date today = new Date();

        int photoCount = 1;
        String photoNameAtDB = "";
        for (MultipartFile photo : photos) {
            SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat time = new SimpleDateFormat("HHmmss"); // 24시간
            String prefix = date.format(today);
            prefix += time.format(today);
            String fileName = prefix + "_" + photoCount;
            String originalName = photo.getOriginalFilename();
            fileName = fileName + originalName.substring(originalName.lastIndexOf(".")); // 확장자 붙여주기

            File destination = new File(path + "/" + fileName);

            try {
                photo.transferTo(destination);
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception("IOException");
            } catch (IllegalStateException e) {
                e.printStackTrace();
                throw new Exception("IllegalStateException");
            }

            if (photoCount != photos.size()) { // 마지막 사진이 아니라면 , 붙여주기
                photoNameAtDB += fileName + ",";
            } else {
                photoNameAtDB += fileName;
            }
            photoCount++;
        }
        feed.setPhoto(photoNameAtDB);
        updatePhotosAndContent(feed);

    }

    @Override
    public void updatePhotosAndContent(Feed feed) throws Exception {
        try {
            feedRepository.updatePhotosAndContent(feed);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("UPDATE_ERR");
        }

    }

    @Override
    public void updateContent(Feed feed) throws Exception {

        try {
            feedRepository.updateContent(feed);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("UPDATE_ERR");
        }
    }

    @Override
    public void updateCommentCount(int feedNo) {
        feedRepository.updateCommentCount(feedNo);
    }

    @Override
    public void minusCommentCount(int feedNo, int count) {
        feedRepository.minusCommentCount(feedNo, count);
    }

    @Override
    public List<Feed> selectAppendFeed(int mNo, int lastNo) {
        return feedRepository.selectAppendFeed(mNo, lastNo);
    }

    @Override
    public String toHtml(List<Feed> myFeedList, String nick, String code, String profilePhoto) {
        String str = "";

        for (Feed feed : myFeedList) {
            str += "<div class=\"card mb-3\" id=\"" + feed.getFeedNo() + "\"><h3 class=\"card-header\">";
            str += "<a href=\"/member/info?mNo=" + feed.getWriter() + "\" style=\"text-decoration: none\">";
            str += "<img width=\"35\" height=\"35\" class=\"rounded-circle\" ";
            if (profilePhoto.equals("none")) {
                str += " src=\"../images/noPhoto.png\">";
            } else {
                str += " src=\"/image/profile/" + feed.getWriter() + "/" + profilePhoto + "\">";
            }
            str += "<span>" + nick + "</span>" + "<span class=\"text-muted\">" + code + "</span></a>";
            str += "<div class=\"nav-item dropdown\" style=\"float: right\">";
            str += "<a class=\"nav-link dropdown-toggle\" data-bs-toggle=\"dropdown\" href=\"#\" role=\"button\"";
            str += "aria-haspopup=\"true\" aria-expanded=\"false\">";
            str += "<i class=\"fa-sharp fa-solid fa-ellipsis-vertical\"></i></a>";
            str += "<div class=\"dropdown-menu\" style=\"\">";
            str += "<button class=\"dropdown-item delBtn\" data=\"" + feed.getFeedNo() + "\">Delete <i class=\"fa-regular fa-trash-can\"></i></button>";
            str += "<a class=\"dropdown-item\" href=\"/feed/modifyFeed?feedNo=" + feed.getFeedNo() + "\">Modify" +
                    "<i class=\"fa-solid fa-file-pen\"></i></a>";
            str += "</div></div></h3>";
            str += "<div class=\"slider\">";
            if (feed.getPhotoNames().isEmpty()) {
                str += "<div><img width=\"100%\" height=\"200\" src=\"/image/feed/" + feed.getWriter() + "/" + feed.getFeedNo() + "/" + feed.getPhoto() + "\"></div>";
            } else {
                for (String photo : feed.getPhotoNames()) {
                    str += "<div><img width=\"100%\" height=\"200\" src=\"/image/feed/" + feed.getWriter() + "/" + feed.getFeedNo() + "/" + photo + "\"></div>";
                }
            }
            str += "</div>";
            str += "<div class=\"card-body\"><pre class=\"content card-text\">" + feed.getContent() + "</pre></div>";
            str += "<div class=\"card-body\" id=\"commentDiv" + feed.getFeedNo() + "\">";
            if (feed.getCommentList().size() > 3) {
                str += "<span class=\"text-muted\" id=\"head" + feed.getFeedNo() + "\"><span type=\"button\" data-bs-toggle=\"collapse\"";
                str += "data-bs-target=\"#body" + feed.getFeedNo() + "\" aria-expanded=\"false\" aria-controls=\"body" + feed.getFeedNo() + "\">";
                str += "댓글<span>" + feed.getCommentList().size() + "</span>개 모두 보기</span></span>";
                str += "<div id=\"body" + feed.getFeedNo() + "\" class=\"accordion-collapse collapse\" data=\"" + feed.getCommentList().size() + "\" aria-labelledby=\"head" + feed.getFeedNo() + "\" style=\"\">";

            }  // 3개 미만일 때랑 댓글 없을 때 만들어야함

            if(feed.getCommentList().isEmpty()) { // 댓글이 없을 때
                str += "<span class=\"text-muted\">작성된 댓글이 없습니다.</span></div>";
            }else{ // 댓글이 있을 때
                for (Comment comment : feed.getCommentList()) {
                    str += "<div class=\"row mb-3\" feedNo=\"" + feed.getFeedNo() + "\">";
                    if (comment.getLevel() == 0) {
                        str += "<div class=\"col-1\"></div><div class=\"col-10\" ";
                    } else {
                        str += "<div class=\"col-2\"></div><div class=\"col-8\" ";
                    }
                    str += "data=\""+comment.getCno()+"\" id=\"comment"+ comment.getCno()+"\"><a href=\"/member/info/mNo="+comment.getWriter()+"\" style=\"text-decoration: none\">";
                    str += " <img width=\"25\" height=\"25\" class=\"rounded-circle\"";
                    if (comment.getPhoto().equals("none")) {
                        str += " src=\"../images/noPhoto.png\">";
                    } else {
                        str += " src=\"/image/profile/" + comment.getWriter() + "/" + comment.getPhoto() + "\">";
                    }
                    str += "<span>" + comment.getNick() + "</span><span class=\"text-muted\">" + comment.getCode() + "</span></a>";
                    str += "<span class=\"reply text-muted ms-2\" type=\"button\"> 댓글</span>";
                    if (feed.getWriter() == comment.getWriter()) {
                        str += " | <span class=\"modRep text-muted\" type=\"button\">수정</span> | <span class=\"delRep text-muted\" type=\"button\">삭제</span>";
                    }
                    str += "<div class=\"ms-2\"><span class=\"text-secondary\" id=\"contentBox" + comment.getCno() + "\">" + comment.getContent() + "</span></div>";
                    str += "</div></div>";
                }
                if(feed.getCommentList().size() > 3){
                    str += "</div></div>";
                }else{
                    str += "</div>";
                }
            }

            str += "<div class=\"form-group mb-2 mt-2 mx-2 ms-2\"><div class=\"input-group\">";
            str += "<input type=\"text\" class=\"commentBox form-control\" placeholder=\"댓글을 남기시겠어요?\" aria-describedby=\"sendBtn\" id=\"commentInput" + feed.getFeedNo() + "\">";
            str += "<button class=\"commentBtn btn btn-primary\" type=\"button\" data=\"" + feed.getFeedNo() + "\"><i class=\"fa-solid fa-pencil\"></i></button>";
            str += "</div></div>";
            str += "<div class=\"card-footer text-muted\">" + feed.getStringDate() + "</div></div></div>";
        }

        return str;
    }

    @Override
    public String toHtmlForInfo(List<Feed> myFeedList, String nick, String code, String profilePhoto, int mNo) {
        String str = "";

        for (Feed feed : myFeedList) {
            str += "<div class=\"card mb-3\" id=\"" + feed.getFeedNo() + "\"><h3 class=\"card-header\">";
            str += "<a href=\"/member/info?mNo=" + feed.getWriter() + "\" style=\"text-decoration: none\">";
            str += "<img width=\"35\" height=\"35\" class=\"rounded-circle\" ";
            if (profilePhoto.equals("none")) {
                str += " src=\"../images/noPhoto.png\">";
            } else {
                str += " src=\"/image/profile/" + feed.getWriter() + "/" + profilePhoto + "\">";
            }
            str += "<span>" + nick + "</span>" + "<span class=\"text-muted\">" + code + "</span></a>";
            str += "<div class=\"nav-item dropdown\" style=\"float: right\">";
            str += "<a class=\"nav-link dropdown-toggle\" data-bs-toggle=\"dropdown\" href=\"#\" role=\"button\"";
            str += "aria-haspopup=\"true\" aria-expanded=\"false\">";
            str += "<i class=\"fa-sharp fa-solid fa-ellipsis-vertical\"></i></a>";
            str += "<div class=\"dropdown-menu\" style=\"\">";
            str += "<button type=\"button\" class=\"reportFeedBtn dropdown-item\" feedNo=\""+feed.getFeedNo()+"\">Report <i class=\"fa-sharp fa-solid fa-bell\"></i></button>";
            str += "</div></div></h3>";
            str += "<div class=\"slider\">";
            if (feed.getPhotoNames().isEmpty()) {
                str += "<div><img width=\"100%\" height=\"200\" src=\"/image/feed/" + feed.getWriter() + "/" + feed.getFeedNo() + "/" + feed.getPhoto() + "\"></div>";
            } else {
                for (String photo : feed.getPhotoNames()) {
                    str += "<div><img width=\"100%\" height=\"200\" src=\"/image/feed/" + feed.getWriter() + "/" + feed.getFeedNo() + "/" + photo + "\"></div>";
                }
            }
            str += "</div>";
            str += "<div class=\"card-body\"><pre class=\"content card-text\">" + feed.getContent() + "</pre></div>";
            str += "<div class=\"card-body\" id=\"commentDiv" + feed.getFeedNo() + "\">";
            if (feed.getCommentList().size() > 3) {
                str += "<span class=\"text-muted\" id=\"head" + feed.getFeedNo() + "\"><span type=\"button\" data-bs-toggle=\"collapse\"";
                str += "data-bs-target=\"#body" + feed.getFeedNo() + "\" aria-expanded=\"false\" aria-controls=\"body" + feed.getFeedNo() + "\">";
                str += "댓글<span>" + feed.getCommentList().size() + "</span>개 모두 보기</span></span>";
                str += "<div id=\"body" + feed.getFeedNo() + "\" class=\"accordion-collapse collapse\" data=\"" + feed.getCommentList().size() + "\" aria-labelledby=\"head" + feed.getFeedNo() + "\" style=\"\">";

            }if(feed.getCommentList().isEmpty()) { // 댓글이 없을 때
                str += "<span class=\"text-muted\">작성된 댓글이 없습니다.</span></div>";
            }else{ // 댓글이 있을 때
                for (Comment comment : feed.getCommentList()) {
                    str += "<div class=\"row mb-3\" feedNo=\"" + feed.getFeedNo() + "\">";
                    if (comment.getLevel() == 0) {
                        str += "<div class=\"col-1\"></div><div class=\"col-10\" ";
                    } else {
                        str += "<div class=\"col-2\"></div><div class=\"col-8\" ";
                    }
                    str += "data=\""+comment.getCno()+"\" id=\"comment"+ comment.getCno()+"\"><a href=\"/member/info/mNo="+comment.getWriter()+"\" style=\"text-decoration: none\">";
                    str += " <img width=\"25\" height=\"25\" class=\"rounded-circle\"";
                    if (comment.getPhoto().equals("none")) {
                        str += " src=\"../images/noPhoto.png\">";
                    } else {
                        str += " src=\"/image/profile/" + comment.getWriter() + "/" + comment.getPhoto() + "\">";
                    }
                    str += "<span>" + comment.getNick() + "</span><span class=\"text-muted\">" + comment.getCode() + "</span></a>";
                    if(comment.getLevel() == 0){
                        str += "<span class=\"reply text-muted ms-2\" type=\"button\"> 댓글</span>";
                    }
                    if (mNo == comment.getWriter()) {
                        str += " | <span class=\"modRep text-muted\" type=\"button\">수정</span> | <span class=\"delRep text-muted\" type=\"button\">삭제</span>";
                    }
                    str += "<div class=\"ms-2\"><span class=\"text-secondary\" id=\"contentBox" + comment.getCno() + "\">" + comment.getContent() + "</span></div>";
                    str += "</div></div>";
                }
                if(feed.getCommentList().size() > 3){
                    str += "</div></div>";
                }else{
                    str += "</div>";
                }
            }

            str += "<div class=\"form-group mb-2 mt-2 mx-2 ms-2\"><div class=\"input-group\">";
            str += "<input type=\"text\" class=\"commentBox form-control\" placeholder=\"댓글을 남기시겠어요?\" aria-describedby=\"sendBtn\" id=\"commentInput" + feed.getFeedNo() + "\">";
            str += "<button class=\"commentBtn btn btn-primary\" type=\"button\" data=\"" + feed.getFeedNo() + "\"><i class=\"fa-solid fa-pencil\"></i></button>";
            str += "</div></div>";
            str += "<div class=\"card-footer text-muted\">" + feed.getStringDate() + "</div></div></div>";
        }

        return str;
    }

    @Override
    public int countAllFeedByWriter(int mNo) {
        return feedRepository.countAllFeedByWriter(mNo);
    }
}
