package com.TravelChat.common.service;

import com.TravelChat.common.model.Feed;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FeedService {
    void saveFeedPhotos(List<MultipartFile> photos, Feed feed) throws Exception;

    int insertFeed(Feed feed) throws Exception;

    void updatePhotos(Feed feed) throws Exception;

    List<Feed> selectRecentFeedListByMNo(int mNo);

    Feed selectFeedByFeedNo(int feedNo);

    void deleteFeed(Feed feed);

    void modifyPhotos(List<MultipartFile> photos, Feed feed) throws Exception;

    void updatePhotosAndContent(Feed feed) throws Exception;

    void updateContent(Feed feed) throws Exception;

    void updateCommentCount(int feedNo);

    void minusCommentCount(int feedNo, int count);

    List<Feed> selectAppendFeed(int mNo, int lastNo);

    String toHtml(List<Feed> myFeedList, String nick, String code, String profilePhoto);

    String toHtmlForInfo(List<Feed> myFeedList, String nick, String code, String profilePhoto, int mNo);

    int countAllFeedByWriter(int mNo);
}
