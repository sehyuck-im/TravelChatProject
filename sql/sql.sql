use travelplanner;
drop table Member;
create table Member
(
    mNo       int primary key auto_increment,
    email     varchar(100),
    nick      varchar(40),
    password  varchar(300),
    gender    varchar(12),
    status    varchar(8),
    regDate   DATE
);

drop table Profile;
create table Profile
(
    mNo         int primary key,
    photo       varchar(200),
    intro       varchar(300),
    openGender  varchar(8),
    openProfile varchar(8)
);

drop table Shaker;
create table Shaker (
  mNo   int primary key,
  shakers   varchar(300)
);
drop table ShakeRequest;
create table ShakeRequest
(
    shakeNo int primary key auto_increment,
    sender int,
    receiver int
);

drop table MemberHistory;
create table MemberHistory
(
    hNo       int primary key auto_increment,
    mNo       int,
    email     varchar(100),
    nick      varchar(40),
    gender    varchar(12),
    status    varchar(8),
    reportCnt int,
    regDate   DATE,
    leaveDate DATE
);

drop table chatRoom;
create table chatRoom
(
    crNo  int primary key auto_increment,
    crTitle varchar(50),
    groupChat varchar(2),
    admin int,
    updateTime datetime,
    crDel varchar(8)
);
drop table feed;
create table feed(
  feedNo int primary key auto_increment,
  writer int,
  photo varchar(1500),
  content varchar(300),
  commentCount int,
  createTime date,
  del   varchar(8)
);

drop table comment;
create table comment(
    cno int primary key auto_increment,
    feedNo int,
    pcno int,
    level int,
    writer int,
    content varchar(100),
    createTime dateTime,
    del   varchar(8)
);

drop table chatUser;
create table chatUser
(
    userNo int primary key auto_increment,
    mNo    int,
    crNo   int,
    chNo   int,
    joinPoint int, -- 채팅방에 들어왔을 때 chNo 지점 설정
    status varchar(2)
);

drop table chatHistory;
create table chatHistory
(
    chNo      int primary key auto_increment,
    crNo      int,
    userNo    int,
    message   varchar(100),
    readCount int
);

drop table ChatRequest;
create table ChatRequest
(
    requestNo int primary key auto_increment,
    sender int,
    receiver int
);
drop table emailCode;
create table emailCode
(
    email varchar(100) primary key,
    code  varchar(20)
);
drop table Board;
create table Board(
    bNo int primary key key auto_increment,
    crNo int,
    title varchar(100),
    writer int,
    content varchar(300),
    bDel varchar(2),
    updateTime datetime

);

drop table HighFiveRequest;
create table HighFiveRequest(
                      requestNo int primary key key auto_increment,
                      crNo int,
                      sender int,
                      receiver int
);

drop table Report;
create table Report(
    reportNo int primary key key auto_increment,
    type int,
    reason int,
    evidence int,
    reporter int,
    target int,
    chk varchar(2),
    reportTime dateTime
);

drop table ReportHistory;
create table ReportHistory(
    mNo int primary key,
    feed int,
    profile int,
    chat int,
    suspendCount int
);


