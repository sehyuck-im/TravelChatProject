package com.TravelChat.common.Util;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UtilServiceImpl implements UtilService{

    @Override
    public String ranNum(int length) {
        Random ran = new Random();
        String code = "";
        for (int i = 0; i < length; i++) {
            String temp = Integer.toString(ran.nextInt(10));
            code += temp;
        }
        return code;
    }
    @Override
    public String ranNumAndAlphabet(int length) {
//        Step.1.숫자의 경우 아스키코드로 변환시 48~57이 0~9로 표현됩니다.
//        Step.2.따라서 로직의 randim.ints() 메소드의 첫번째 파라미터를 48로 지정해준다. 두번째 파라미터는 알파벳의 제일끝이 122이므로 알파벳만 출력할때와 같이 122+1로 셋팅해줍니다.
//        Step.3.알파벳과 숫자만 출력되어야 하니깐 filter() 메소드를 활용해서 아스키코드의 범위를 제한 해줍니다.
//        Step.4.문자열 길이를 limit()메소드로 제한해줍니다.
//        Step.5.collect() 메소드로 StringBuilder 객체를 생성해줍니다.
//        Step.6. 이제 StringBuilder 객체를 toString() 으로 문자열로 변환합니다.
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }
}
