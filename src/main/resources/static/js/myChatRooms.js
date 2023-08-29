// ----------------------- 쿠키/토큰 ---------------------------
const token = Cookies.get('Authorization');

const config = {
    headers: {
        'Authorization': `${token}`
    }
};
//----------------------- 모달 관련 js --------------------------
function showModal(modalId, overlayId) {
    const modal = document.getElementById(modalId);
    const overlay = document.getElementById(overlayId);
    modal.style.display = 'block';
    overlay.style.display = 'block';
}

function closeModal(modalId, overlayId) {
    const modal = document.getElementById(modalId);
    const overlay = document.getElementById(overlayId);
    modal.style.display = 'none';
    overlay.style.display = 'none';
}

// --------------------- 채팅방 생성하기 -------------------------
const createRoomButton = document.getElementById('createRoomButton');
const closeCreateRoomModal = document.getElementById('closeCreateRoomModal');

createRoomButton.addEventListener('click', showModal.bind(null, 'registerCreateRoomModal', 'createRoomModalOverlay'));
closeCreateRoomModal.addEventListener('click', closeModal.bind(null, 'registerCreateRoomModal', 'createRoomModalOverlay'));

document.getElementById('submitSearchKeyword').addEventListener('click', function () {
    const searchInput = document.getElementById('searchInput').value;
    console.log('검색 키워드 -> ' + searchInput);
    searchUserByKeyword(searchInput);
});

// 사용자 검색 요청
function searchUserByKeyword(keyword) {
    axios.get('/api/search/users?keyword=' + keyword, config)
        .then(response => {
            console.log(response);
            const results = response.data.searchUserResults;
            results.forEach(user => {
                createSearchResultCard(user);
            });
        })
        .catch(error => {
            console.error(error);
            alert('사용자 검색 결과 불러오기 실패');
        });
}

// 모달에 띄울 결과 카드 생성
function createSearchResultCard(user) {
    const searchResultsContainer = document.getElementById('searchResultsContainer'); // Replace with your actual container ID

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

    searchResultsContainer.appendChild(searchResultCard);
}

// ------------------- 내가 속한 채팅방 불러오기 -------------------
const roomCardsContainer = document.getElementById('roomCardsContainer');

// 내가 속한 채팅방 1개 카드 만들기
function createRoomCard(room) {
    const roomCard = document.createElement('div');
    roomCard.className = 'room-card';

    const roomInfo = document.createElement('div');
    roomInfo.className = 'room-info';

    const roomName = document.createElement('div');
    roomName.className = 'room-name';
    roomName.textContent = room.chatRoomName;

    roomInfo.appendChild(roomName);

    const enterRoomButton = document.createElement('button');
    enterRoomButton.className = 'enterRoomButton';
    enterRoomButton.textContent = 'enter';
    enterRoomButton.addEventListener('click', function () {
        enterRoom(room.chatRoomId, room.chatRoomName);
    });

    roomCard.appendChild(roomInfo);
    roomCard.appendChild(enterRoomButton);

    roomCardsContainer.appendChild(roomCard);
}

// 내가 속한 채팅방 불러오기
function getAllMyRooms() {
    axios.get('/api/chatRooms/myRooms', config)
        .then(response => {
            console.log(response);
            const rooms = response.data.myChatRoomList;
            rooms.forEach(room => {
                createRoomCard(room);
            });
        });
}

// 채팅방 입장
function enterRoom(roomId, roomName) {
    console.log('enterRoom clicked roomId : ' + roomId);
    console.log('enterRoom clicked roomName : ' + roomName);

    localStorage.setItem('wschat.roomId', roomId);
    localStorage.setItem('wschat.roomName', roomName);
    alert('이동');
    location.href = "/chat/room/enter/" + roomId;
}

// -----------------------로그아웃--------------------------
document.getElementById('logoutButton').addEventListener('click', logout);

function logout() {
    alert('로그아웃');
    Cookies.remove('Authorization', {path: '/'});
    window.location.href = "/auth/login-page";
}

//---------------------- 페이지 로딩 시 -----------------------
getAllMyRooms();