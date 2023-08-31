// ------------------------ 편의 함수 ---------------------------
function showElement(elementId) {
    const element = document.getElementById(elementId);
    element.style.display = 'block';
}

function hideElement(elementId) {
    const element = document.getElementById(elementId);
    element.style.display = 'none';
}


// ----------------------- 실시간 채팅 ---------------------------
const token = Cookies.get('Authorization');

const headers = {
    Authorization: token
};

const config = {
    headers: {
        'Authorization': `${token}`
    }
};

// 변수 초기화
let page = 0;
let bottomFlag = true;
const roomId = localStorage.getItem('wschat.roomId');
const containerDiv = document.getElementById("app");

// websocket, stomp 설정
const sock = new SockJS("/ws-stomp");
const ws = Stomp.over(sock);

// vue.js
const vm = new Vue({
    el: '#app',
    data: {
        roomId: '',
        roomName: '',
        message: '',
        messages: [],
        members: [],
        users: [],
        token: '',
    },
    created() {
        this.initializeWebSocket();     // 접속 시 웹소켓 연결
        this.getChatHistory(page);         // 접속 시 가장 최근 채팅들 로드
        //this.$nextTick(() => {      // 접속 시 스크롤 가장 아래로 (안됨)
        //this.scrollToBottom();
        //})
    },
    updated() {

        if (bottomFlag) {
            this.containerDiv.scrollTop = this.containerDiv.scrollHeight;
        }
    },
    methods: {
        initializeWebSocket() {
            this.roomId = localStorage.getItem('wschat.roomId');
            this.roomName = localStorage.getItem('wschat.roomName');
            this.username = localStorage.getItem('username');
            const _this = this;

            ws.connect(headers, function (frame) {
                ws.subscribe("/sub/chat/room/" + _this.roomId, function (message) {
                    const recv = JSON.parse(message.body);
                    _this.recvMessage(recv);
                });
            }, function (error) {
                _this.handleConnectionError();
            });
        },
        getChatHistory(page) {
            console.log('getChatHistory 시작');
            this.roomId = localStorage.getItem('wschat.roomId');
            const _this = this;
            // let page = 0;
            axios.get(`/api/chatRooms/${_this.roomId}?page=${page}`, config)
                .then(response => {
                    console.log(response);

                    let lastMessages = response.data;
                    lastMessages.forEach(lastM => {
                        this.messages.unshift({
                            "type": lastM.type,
                            "sender": lastM.sender,
                            "message": lastM.message,
                            "createdAt": lastM.createdAt
                        })
                    })
                })
                .catch(error => {
                    console.error('실패' + error);
                });
        },
        sendMessage: function (type) {

            if (this.message === '') {
                alert('내용을 입력하세요.');
                return;
            }

            ws.send("/pub/chat/message", headers,
                JSON.stringify({
                    type: type,
                    roomId: this.roomId,
                    message: this.message
                })
            );
            this.message = '';
        },
        recvMessage: function (recv) {

            this.messages.push({
                "type": recv.type,
                "sender": recv.sender,
                "message": recv.message,
                "createdAt": recv.createdAt
            })
        },
        leaveChatRoom() {
            this.message = '.';
            this.sendMessage('QUIT');

            axios.delete('/api/chatRooms/' + this.roomId + '/members', config)
                .then(response => {
                    console.log(response);
                    alert('채팅방을 나갔습니다.');
                    location.href = "/chat/room";
                })
                .catch(error => {
                    console.error(error);
                    alert('채팅방 나가기 실패');
                });
        },
        getThisChatRoomMembers() {
            showElement('chatMembersModalOverlay');
            showElement('registerChatMemberModal');

            axios.get('/api/chatRooms/' + this.roomId + '/members', config)
                .then(response => {
                    console.log(response);

                    let members = response.data.chatMemberInfoList;
                    members.forEach(member => {
                        this.members.push({
                            "id": member.id,
                            "username": member.username,
                            "nickname": member.nickname
                        })
                    })
                })
                .catch(error => {
                    console.error(error);
                })
        },
        startInviteUser() {
            const searchInput = document.getElementById('searchInput').value;
            console.log('검색 키워드 -> ' + searchInput);
            searchUserByKeyword(searchInput);
        },
        searchUserByKeyword(keyword) {
            const searchResultsContainer = document.getElementById('searchResultsContainer');
            searchResultsContainer.innerHTML = '';

            axios.get('/api/search/users?keyword=' + keyword, config)
                .then(response => {
                    console.log(response);
                    const results = response.data.searchUserResults;

                    if (results.length === 0) {
                        showElement('no-search-result');
                    } else {
                        results.forEach(user => {
                            createSearchResultCard(user, searchResultsContainer);
                        });
                    }
                })
                .catch(error => {
                    console.error(error);
                    alert('사용자 검색 결과 불러오기 실패');
                });
        },
        loadMoreChatHistory() {
            page++; // Increment the page counter
            this.getChatHistory(page);
        },
        handleConnectionError() {
            alert("서버 연결에 실패 하였습니다. 다시 접속해 주세요.");
            location.href = "/chat/room";
        }
    }
});

// ----------------------- ready ---------------------------
$(document).ready(function () {
    // 사용자 초대 모달 열기, 닫기
    $("#inviteModalBtn").click(function () {
        showElement('inviteUserModalOverlay');
        showElement('registerInviteUserModal');
    });

    $("#closeCreateRoomModal").click(function () {
        hideElement('inviteUserModalOverlay');
        hideElement('registerInviteUserModal');
        selectedUserIds.length = 0;
        document.getElementById('searchInput').value = '';
        document.getElementById('searchResultsContainer').innerHTML = '';
        location.reload();
    });

    document.getElementById('submitSearchKeyword').addEventListener('click', () => {
        const searchInput = document.getElementById('searchInput').value;
        console.log('검색 키워드 -> ' + searchInput);
        searchUserByKeyword(searchInput);
    });

    function searchUserByKeyword(keyword) {
        const searchResultsContainer = document.getElementById('searchResultsContainer');
        searchResultsContainer.innerHTML = '';

        axios.get('/api/search/users?keyword=' + keyword, config)
            .then(response => {
                console.log(response);
                const results = response.data.searchUserResults;

                if (results.length === 0) {
                    createNoSearchResultCard(searchResultsContainer);
                } else {
                    results.forEach(user => {
                        createSearchResultCard(user, searchResultsContainer);
                    });
                }
            })
            .catch(error => {
                console.error(error);
                alert('사용자 검색 결과 불러오기 실패');
            });
    }

    // 검색 결과가 없을 경우
    function createNoSearchResultCard(container) {
        const noResultsCard = document.createElement('div');
        noResultsCard.className = 'search-results-card';
        noResultsCard.textContent = '검색 결과 없음';
        container.appendChild(noResultsCard);
    }

    // 체크박스 클릭 -> 선택한 사용자 id 담을 배열
    const selectedUserIds = [];

    // 모달에 띄울 결과 카드 생성
    function createSearchResultCard(user, container) {

        const searchResultCard = document.createElement('div');
        searchResultCard.className = 'search-result-card';
        searchResultCard.id = user.userId;


        const profileContainer = document.createElement('div');
        profileContainer.className = 'user-profile-container';

        // const userImage = document.createElement('img');
        // userImage.className = 'user-image';
        // userImage.src = user.profileImageURL;

        const username = document.createElement('div');
        username.className = 'user-username';
        username.textContent = user.username;

        const nickname = document.createElement('div');
        nickname.className = 'user-nickname';
        nickname.textContent = user.nickname;

        // 체크박스
        const checkboxContainer = document.createElement('div');
        checkboxContainer.className = 'checkbox-container';

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.id = 'checkbox-' + user.userId;

        const checkboxLabel = document.createElement('label');
        checkboxLabel.className = 'checkbox-label';
        checkboxLabel.htmlFor = 'checkbox-' + user.userId;

        checkboxContainer.appendChild(checkbox);
        checkboxContainer.appendChild(checkboxLabel);

        profileContainer.appendChild(username);
        profileContainer.appendChild(nickname);
        // profileContainer.appendChild(userImage);

        searchResultCard.appendChild(checkboxContainer);
        searchResultCard.appendChild(profileContainer);

        container.appendChild(searchResultCard);

        checkbox.addEventListener('click', () => {
            if (checkbox.checked) {
                selectedUserIds.push(user.userId);
            } else {
                const index = selectedUserIds.indexOf(user.userId);
                if (index !== -1) {
                    selectedUserIds.splice(index, 1);
                }
            }
            console.log(selectedUserIds);
        });
    }

    // 모달의 초대 버튼 누르면
    document.getElementById('submitSelectUser').addEventListener('click', function () {
        const memberIdList = selectedUserIds.map(userId => ({userId}));
        console.log(memberIdList);
        let data = {
            memberIdList: memberIdList
        };

        axios.post(`/api/chatRooms/` + roomId + `/members`, data, config)
            .then(response => {
                console.log(response);
                hideElement('search-container');
                hideElement('submitSelectUser');
                showElement('complete-creating');
            })
            .catch(error => {
                console.error(error);
                console.error('사용자 초대 요청 실패');
            });
    });

    $("#closeChatMemberModal").click(function () {
        hideElement('chatMembersModalOverlay');
        hideElement('registerChatMemberModal');
        document.getElementById('memberListContainer').innerHTML = '';
    });

});
