package com.TravelChat.common.cotroller;

import com.TravelChat.common.model.Feed;
import com.TravelChat.common.service.FeedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class FeedControllerTest {
    @Autowired
    private FeedService feedService;

    @Test
    @Transactional
    void saveFeedPhotosWithTx() {
        List<MultipartFile> photos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MultipartFile photo = new MockMultipartFile(String.valueOf(i), (byte[]) null);
            photos.add(photo);
        }
        System.out.println("photos = " + photos);

        Feed feed = new Feed();
        feed.setWriter(1);
        feed.setContent("testtest");

        try {
            feedService.saveFeedPhotos(photos, feed);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("e.getMessage() = " + e.getMessage());
        }

    }

    @Test
    void separatePhotos() {
        String ori = "20221116175606_1.jpg,20221116175606_2.jpg,20221116175606_3.gif,20221116175606_4.jpg,20221116175606_5.jpg";
        String[] strArr = ori.split(",");
        List<String> stringList = new ArrayList<>();
        Feed feed = new Feed();

        System.out.println("strArr.length = " + strArr.length);
        if (strArr.length > 1) {
            for (String tempPhoto : strArr) {
                feed.getPhotoNames().add(tempPhoto);
            }
        }
        System.out.println("feed = " + feed);


    }
}