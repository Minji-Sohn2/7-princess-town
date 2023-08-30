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

const params = []

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
        userCount: 0
    },
    created() {
        this.initializeWebSocket();
        this.getChatHistory();
        this.scrollToBottom();
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
        getChatHistory() {
            console.log('getChatHistory 시작');
            this.roomId = localStorage.getItem('wschat.roomId');
            const _this = this;
            let page = 0;
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
            this.scrollToBottom();
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
            const messageContainer = document.getElementById('message-upper-container');
            messageContainer.scrollTop = messageContainer.scrollHeight;
        },
        handleConnectionError() {
            alert("서버 연결에 실패 하였습니다. 다시 접속해 주십시요.");
            location.href = "/chat/room";
        }
    }
});