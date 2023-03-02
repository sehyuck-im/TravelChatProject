package com.TravelChat.common.service;

import com.TravelChat.member.model.Member;
import com.TravelChat.member.model.Profile;
import com.TravelChat.member.repository.MemberRepository;
import com.TravelChat.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class PhotoServiceImpl implements PhotoService {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository mr;

    @Override
    public String savePhoto(MultipartFile photo, Profile profile, String path) throws IOException {
        String realPath = path + "/" + profile.getMNo() + "/";
        // 회원별로 프로필사진 폴더 따로 존재
        File createDir = new File(realPath);
        // 폴더 없으면 생성
        if (!createDir.exists()) {
            createDir.mkdir();
            createDir.setWritable(true);
            createDir.setReadable(true);
        }
        Date today = new Date();

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat time = new SimpleDateFormat("HHmmss"); // 24시간
        String prefix = date.format(today);
        prefix += time.format(today);

        Member member = memberService.selectByMno(profile.getMNo());
        String fileName = prefix + member.getEmail();
        String originalName = photo.getOriginalFilename();
        fileName = fileName + originalName.substring(originalName.lastIndexOf(".")); // 확장자 붙여주기

        File destination = new File(realPath + "/" + fileName);

        photo.transferTo(destination);

        profile.setPhoto(fileName);

        mr.updatePhoto(profile); // db에 사진이름 저장

        String result = "/image/profile/" + profile.getMNo(); // 결과물로 이미지를 불러올 경로


        return result + "/" + fileName;
    }
}
