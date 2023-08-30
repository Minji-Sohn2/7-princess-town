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

let page = 0;
let bottomFlag = true;

const containerDiv = document.getElementById("app");

// websocket & stomp initialize
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
        token: '',
    },
    created() {
        this.initializeWebSocket();     // 접속 시 웹소켓 연결
        this.getChatHistory(page);         // 접속 시 가장 최근 채팅들 로드
        //this.$nextTick(() => {      // 접속 시 스크롤 가장 아래로 (안됨)
            this.scrollToBottom();
        //})
    },
    updated () {

        if(bottomFlag) {
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
            this.$nextTick(() => {      // 스크롤 가장 아래로
                this.scrollToBottom();
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
        scrollToBottom: function () {
            if(bottomFlag) {
                this.containerDiv.scrollTop = this.containerDiv.scrollHeight;
            }
        },
        handleScroll(event) {
            const messageContainer = event.target;
            if (messageContainer.scrollTop === 0) {
                this.loadMoreChatHistory();
            }
        },
        loadMoreChatHistory() {
            page++; // Increment the page counter
            this.getChatHistory(page);
        },
        handleConnectionError() {
            alert("서버 연결에 실패 하였습니다. 다시 접속해 주십시요.");
            location.href = "/chat/room";
        }
    }
});