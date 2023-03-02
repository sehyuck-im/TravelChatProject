package com.TravelChat.board.model;

import lombok.Data;

@Data
public class PageHandler {

    private SearchCondition sc;

    private int totalCnt; // 총 게시물 갯수
    private int naviSize = 10; // 페이지 내비게이션의 크기
    private int totalPage; // 전체 페이지의 수
    private int beginPage; // 내비게이션의 첫번째 페이지
    private int endPage; // 내비게이션의 마지막 페이지
    private boolean showPrev; // 이전 페이지로 이동하는 링크를 보여줄 것인지 여부
    private boolean showNext; // 다음 페이지로 이동하는 링크를 보여줄 것인지 여부

    public PageHandler(int totalCnt, SearchCondition sc){
        this.totalCnt = totalCnt;
        this.sc = sc;

        doPaging(totalCnt, sc);
    }

    public void doPaging(int totalCnt, SearchCondition sc) {
        this.totalCnt = totalCnt;

        totalPage = (int) Math.ceil((double) totalCnt / sc.getPageSize());
        beginPage = (sc.getPage()-1) / naviSize * naviSize + 1;
        endPage = Math.min(beginPage + naviSize - 1, totalPage);
        showPrev = beginPage !=1;
        showNext = endPage != totalPage;
    }

}
