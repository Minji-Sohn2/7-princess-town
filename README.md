# 우리 동네 커뮤니티 토마토

![토마토 소개](https://github.com/Minji-Sohn2/7-princess-town/assets/130354169/7ce7b1fa-60f3-433a-b717-7135641c5577)

## 토마토 소개
사용자의 위치와 반경 정보를 입력 받아 주변 사람들과 소통할 수 있도록 하는 **지역 커뮤니티**

## 서비스 아키텍처
<img width="13511" alt="서비스 아키텍처 부트스트랩,시맨틱 UI 포함" src="https://github.com/Minji-Sohn2/7-princess-town/assets/130354169/f1be89e0-3afa-468d-bf1f-a9b4c440ef0e">

## ERD
![7조 erd](https://github.com/Minji-Sohn2/7-princess-town/assets/130354169/fcf1c2fa-681e-4641-84d3-504b4a5d6bf4)


## 사용한 주요 기술
- **Redis, MySQL**	
- [x] 단기 정보(세션 정보, 인증 코드 등) 관리를 위해 Redis 사용
- [x] 사용자와 게시글 등의 정보를 저장할 RDBMS는 팀원들에게 가장 익숙한 MySQL 사용
- **Websocket, Stomp**
- [x] 채팅 서비스를 구현할 때 실시간 양방향 소통을 구현하기 위해 Websocket을 사용
- [x] 메세지 형식, 메세지 헤더 활용 등을 통해 메세지 전송을 효율적으로 할 수 있도록 Stomp 사용
- **GeoLocation API**
- [x] 사용자의 위치 정보(위도, 경도) 조회를 위해 사용
- **CoolSMS**
- [x] 회원 가입, 탈퇴 시 사용되는 휴대폰 인증을 구현하기 위해 간편하게 연동할 수 있고 안정적인 Cool SMS 사용
- **AWS, Azure, NCP**
- [x] 다양한 클라우드 서비스를 이용해보기 위해 세가지 클라우드 서비스로 배포

## 팀원 소개
| 이름 | 역할 | 담당 | 깃허브 주소 |
| --- | --- | --- | --- |
| 김휘수 | 팀원 | 게시판, 위치 기반 게시글 | https://github.com/notitle12 |
| 모성민 | 부팀장 | 댓글, 이모티콘, 리소스 | https://github.com/Garim12 |
| 손민지 | 팀장 | 채팅, AWS | https://github.com/Minji-Sohn2 |
| 주인수 | 팀원 | 사용자 관리 기능, 위치 서비스 | https://github.com/insu20230427 |
