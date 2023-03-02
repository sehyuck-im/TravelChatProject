package com.TravelChat.common.service;

import com.TravelChat.member.model.Profile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {

    String savePhoto(MultipartFile photo, Profile profile, String path) throws IOException;
}
