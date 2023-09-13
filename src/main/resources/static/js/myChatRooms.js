// ----------------------- 쿠키/토큰 ---------------------------
const token = Cookies.get('Authorization');
const config = {
    headers: {
        'Authorization': `${token}`
    }
};

// ----------------------- 모달 관련 js --------------------------
function showElement(elementId) {
    const element = document.getElementById(elementId);
    element.style.display = 'block';
}

function hideElement(elementId) {
    const element = document.getElementById(elementId);
    element.style.display = 'none';
}

// --------------------- 채팅방 생성하기 모달 ---------------------
const createRoomButton = document.getElementById('createRoomButton');
const closeCreateRoomModal = document.getElementById('closeCreateRoomModal');

createRoomButton.addEventListener('click', () => {
    showElement('registerCreateRoomModal');
    showElement('createRoomModalOverlay');
});

closeCreateRoomModal.addEventListener('click', () => {
    hideElement('registerCreateRoomModal');
    hideElement('createRoomModalOverlay');
    selectedUserIds.length = 0;
    document.getElementById('searchInput').value = '';
    document.getElementById('newChatRoomNameInput').value = '';
    document.getElementById('searchResultsContainer').innerHTML = '';
    location.reload();
});

//------------------------- 사용자 검색  -------------------------------
document.getElementById('submitSearchKeyword').addEventListener('click', () => {
    const searchInput = document.getElementById('searchInput').value;

    console.log('검색 키워드 -> ' + searchInput);
    if (searchInput.trim() === '') {
        alert('검색어를 입력하세요');
        return;
    }
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
            selectedUserIds.push(user.userId); // Add the ID to the array
        } else {
            const index = selectedUserIds.indexOf(user.userId);
            if (index !== -1) {
                selectedUserIds.splice(index, 1);
            }
        }
        console.log(selectedUserIds);
    });
}

document.getElementById('submitSelectUser').addEventListener('click', function () {
    showElement('new-roomname-container');
    hideElement('search-container');
    showElement('registerNewChatRoom');
    hideElement('submitSelectUser');
});

// ------------------- 초대 후 방 이름 입력받기 -------------------
// 만들기 버튼 눌렀을 때
document.getElementById('registerNewChatRoom').addEventListener('click', function () {
    const newChatRoomName = document.getElementById('newChatRoomNameInput').value;
    console.log('입력한 새로운 채팅방 이름 -> ' + newChatRoomName);
    createNewChatRoom(newChatRoomName);
    hideElement('registerNewChatRoom');
    hideElement('new-roomname-container');
    showElement('complete-creating');
});

// 새로운 채팅방 정보 전송
function createNewChatRoom(newChatRoomName) {

    const memberIdList = selectedUserIds.map(userId => ({userId}));
    console.log(memberIdList);
    let data = {
        "chatRoomName": newChatRoomName,
        memberIdList
    };

    axios.post("/api/chatRooms", data, config)
        .then(response => {
            console.log(response);

        })
        .catch(error => {
            console.error(error);
            console.error('채팅방 생성 요청 실패');
        });
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

    const enterRoomButton = document.createElement('div');
    enterRoomButton.className = 'enterRoomButton';

    const enterIcon = document.createElement('i');
    enterIcon.className = 'arrow alternate circle right outline icon';

    enterRoomButton.appendChild(enterIcon);

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
            if (rooms.length !== 0) {
                rooms.forEach(room => {
                    createRoomCard(room);
                });
            } else {
                $('#noRoomCard').show();
            }
        });
}

// 채팅방 입장
function enterRoom(roomId, roomName) {
    console.log('enterRoom clicked roomId : ' + roomId);
    console.log('enterRoom clicked roomName : ' + roomName);

    localStorage.setItem('wschat.roomId', roomId);
    localStorage.setItem('wschat.roomName', roomName);
    location.href = "/view/chatRooms/" + roomId;
}

//---------------------- 페이지 로딩 시 -----------------------
function initializePage() {
    getAllMyRooms();
}

initializePage();