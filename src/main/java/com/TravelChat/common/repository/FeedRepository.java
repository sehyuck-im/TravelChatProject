package com.TravelChat.common.repository;

import com.TravelChat.common.model.Feed;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FeedRepository {

    void insertFeed(Feed feed);

    void updatePhotos(Feed feed);

    List<Feed> selectFeedListByMNo(int mNo);

    Feed selectFeedByFeedNo(int feedNo);

    void deleteFeed(Feed feed);

    void updatePhotosAndContent(Feed feed);

    void updateContent(Feed feed);

    void updateCommentCount(int feedNo);

    void minusCommentCount(int feedNo, int count);

    List<Feed> selectAppendFeed(int mNo, int lastNo);

    int countAllFeedByWriter(int mNo);
}
