// URL에서 postid를 뽑아냄
function getPostIdFromUrl() {
    const urlParts = window.location.pathname.split('/');
    const postIdIndex = urlParts.indexOf('posts') + 1;
    return urlParts[postIdIndex];
}

// 댓글,답글 유저 id 중간 부분 별표로 가려 보안을 강화
function maskingName(username) {
    if (username.length >= 8) {
        return (
            username.slice(0, 3) +
            "*".repeat(Math.max(0, username.length - 5)) +
            username.slice(-3)
        );
    } else if (username.length >= 4){
        return (
            username.slice(0, 2) +
            "*".repeat(Math.max(0, username.length - 3)) +
            username.slice(-1)
        );
    } else {
        return username.replaceAll('*', username);
    }
}

const postId = getPostIdFromUrl();

// jwt 추출후 앞으로 적용할 유저id와 닉네임을 변수에 저장하는 메서드 아래 토큰변수는 임시변수

// 로그인 프론트가 구현되면 사용할 변수
const token = Cookies.get('Authorization');

// let usernames;

let username;
let nickname;

// if (token) {
//     $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
//         jqXHR.setRequestHeader('Authorization', token);
//     });
//     // JWT 토큰 디코딩하여 페이로드 추출
//     // 예시 {sub: 'testid3', nickname: 'testnick3', auth: 'USER', exp: 1689745728, iat: 1689742128}
//     // 그중 username을 추출해야하니 sub를 가져옴. 만약 관리자 확인이면 auth를 가져올듯.
if (token) {
    const payload = JSON.parse(atob(token.split(".")[1]));
    usernames = payload.sub;
    // nickname = payload.nickname;
    // 댓글 닉네임에 표시해주는 기능
    $.ajax({
        type: 'GET',
        url: `/api/nickname?username=${usernames}`,
        success: function (data) {
            $('#loginUserName').text(data.result);
        },
        error: function (e) {
            console.log(e.message)
            console.log(`error = ${e}`)
        }
    })

    const tokenData = payload.exp;
    const expirationTimeInMillis= tokenData * 1000;
    const timeUntilExpiration = expirationTimeInMillis - Date.now();
    setTimeout(deleteToken, timeUntilExpiration);

    function deleteToken() {
        Swal.fire({
            icon: 'warning',
            title: '토큰 만료',
            text: '토큰이 만료되었습니다. 다시 로그인를 해주십시요.'
        });
        Cookies.remove('Authorization');
        window.location.reload();
    }

} else if (token === undefined || token === "") {
    usernames = "Guest"
    nickname = "Guest"
    $('#loginUserName').text(nickname);
    $(`#userComment`).remove();
    $(`#createComments`).remove();
    $(`#img-wrap`).remove();
    $('#show').remove();
}

$.ajax({
    url: `/api/posts/${postId}/commentlist`,
    method: 'GET',
    success: function (data) {
        const commentsList = data.result.comments.length;

        $('.comment-h2').text("댓글 ( 댓글수 : " + commentsList + " )");
    }
})

// 화면이 띄워질경우 실행되는 메소드
$(document).ready(function () {
    $('#eventLoading').show();

    // 페이징
    const commentsContainer = $('.clear');
    const paginationContainer = $('.pagination');

    let currentPage = 0;
    let totalPages = 0;
    let startPage = 0;
    let endPage = 9;
    const pageSize = 10; // 페이지당 댓글/답글 개수
    let totalItems = 0;

    // 페이지 로드시 댓글과 답글 가져오는 로직 호출
    function loadCommentsAndReplies(page) {
        $.ajax({
            url: `/api/posts/${postId}/comments?page=${page}&size=${pageSize}`,
            method: 'GET',
            success: function (data) {
                const comments = data.result.comments;
                totalPages = data.result.paginationInfo.totalPages;
                totalItems = data.result.paginationInfo.totalItems;
                displayCommentsAndReplies(comments, page);
                updatePagination(totalPages, page);
                // console.log(page,pageSize)
            }
        });
    }

    // 화면에 댓글과 답글을 띄워줌
    function displayCommentsAndReplies(comments, page) {
        // console.log("페이지 로딩")
        // 이전에 표시된 댓글과 답글 제거
        commentsContainer.empty();

        $.ajax({
            url: `/api/posts/${postId}/comments?page=${page}&size=${pageSize}`,
            method: "GET",
            dataType: "json",
            beforeSend: function() {
                $('#eventLoading').show();
                $('body').on('scroll touchmove mousewheel', function(e) {
                    e.stopPropagation();
                    return false;
                });
            },
            complete:function() {
                $('#eventLoading').hide();
                $('body').off('scroll touchmove mousewheel')
            },
            success: function (comments) {
                let data = comments.result.comments;
                var commentsContainer = $("#comment");

                // 댓글의 좋아요 정보 가져오기
                $.ajax({
                    url: "/api/posts/" + postId + "/comments/likes",
                    method: "GET",
                    dataType: "json",
                    success: function (likesval) {
                        let likesData = likesval.result;

                        data.forEach(function (comment) {

                            const createdAt = comment.createdAt;

                            const date = new Date(createdAt);

                            const formattedDate = date.toLocaleString("ko-KR", {
                                year: "numeric",
                                month: "2-digit",
                                day: "2-digit",
                                hour: "2-digit",
                                minute: "2-digit",
                                second: "2-digit",
                            });

                            // console.log(comment)

                            // console.log(comment)

                            let temp_html = `
                                <div class="grid1_of_2" data-comment-id="${comment.id}">
                                <div class="grid_text">
                                    <hr style="border-top: 1px solid #6c757d;">
                                    <div class="grid_img">
                                        <img class="profile-img" src="${comment.img}" alt="My Image" style="border-radius: 50%;" data-comment-id="${comment.id}">
                                    </div>
                                    <h4 class="style1 list" style="font-family: 'Jua', sans-serif;"><a href="#" style="font-family: 'Jua', sans-serif;">${comment.nickname}(${maskingName(comment.username)})</a></h4>
                                    <p class="para top" style="font-family: 'Jua', sans-serif;" >${formattedDate}</p>
                                    <br/>
                                    <br/>
                                    <h4 class="style1 list userComment" data-comment-id="${comment.id}">${comment.content}</h4>
                                    <br/>
                                    <img class="emoji" src="${comment.emoji}" alt="emoji" data-comment-id="${comment.id}">
                                    <br/>
                                    <br/>
                                    <div class="commentsLikes" data-comment-id="${comment.id}">
                                        <a class="commentunLikes" style="cursor: pointer" data-comment-id="${comment.id}" onclick="likesClick(${postId}, ${comment.id}, ${comment.likeCnt})">🤍</a>
                                        <span class="commentcnt" data-comment-id="${comment.id}">${comment.likeCnt}</span>
                                    </div>
                                    <a class="btn1 editComments" style="font-family: 'Jua', sans-serif; cursor: pointer" data-comment-id="${comment.id}">수정</a>
                                    <a class="btn1 editCommentsClose" style="font-family: 'Jua', sans-serif; cursor: pointer; display: none" data-comment-id="${comment.id}">수정창닫기</a>
                                    <a class="btn1 deleteComments" style="font-family: 'Jua', sans-serif; cursor: pointer" data-comment-id="${comment.id}">삭제</a>
                                    <a class="btn1 replyCreate" style="font-family: 'Jua', sans-serif; cursor: pointer" data-comment-id="${comment.id}">답글달기</a>
                                    <a class="btn1 replyCreateClose" style="font-family: 'Jua', sans-serif; cursor: pointer; display: none" data-comment-id="${comment.id}">답글입력창닫기</a>
                                    <a class="btn1 replyRead" style="font-family: 'Jua', sans-serif; cursor: pointer"  data-comment-id="${comment.id}" onclick="openReply(${comment.id})">답글 펼치기  (0개)</a>
                                    <a class="btn1 replyClose" style="font-family: 'Jua', sans-serif; cursor: pointer; display: none" data-comment-id="${comment.id}" onclick="closeReply(${comment.id})">답글 닫기</a>
                                </div>
                                <div class="grid_text userCommentEdit-Form" style="display: none; position: relative" data-comment-id="${comment.id}">
                                    <label style="font-family: 'Jua', sans-serif;">수정창<span>*</span></label>
                                    <button class="editshow" type="button" data-comment-id="${comment.id}" onclick="editpopup(${comment.id})">😀</button>
                                    <textarea type="text" class="userCommentEdit" name="content" placeholder="내용을 입력해주세요." data-comment-id="${comment.id}"></textarea>
                                    <button class="btn btn-secondary userEditCommentsComplete" type="button" data-comment-id="${comment.id}">댓글 수정</button>
                                </div>
                                <div class="grid_text userReply-Form" style="display: none; position: relative" data-comment-id="${comment.id}">
                                    <label style="font-family: 'Jua', sans-serif;">답글창<span>*</span></label>
                                    <textarea type="text" class="userReply" name="content" placeholder="내용을 입력해주세요." data-comment-id="${comment.id}"></textarea>
                                    <button class="replyshow" type="button" data-comment-id="${comment.id}" onclick="replypopup(${comment.id})">😀</button>
                                    <button class="btn btn-secondary btn-replycreate" type="button" data-comment-id="${comment.id}" onclick="replyCreate(${postId}, ${comment.id})">답글 작성</button>
                                </div>
                            `

                            // 해당 댓글에 대한 좋아요 정보 검사
                            const likeInfo = likesData.find(function (like) {
                                return like.comment_id === comment.id && like.username === usernames;
                            });

                            if (likeInfo && likeInfo.likes) {
                                // console.log(likeInfo)
                                temp_html = `
                                <div class="grid1_of_2" data-comment-id="${comment.id}">
                                <div class="grid_text">
                                    <hr style="border-top: 1px solid #6c757d;">
                                    <div class="grid_img">
                                        <img class="profile-img" src="${comment.img}" alt="My Image" style="border-radius: 50%;" data-comment-id="${comment.id}">
                                    </div>
                                    <h4 class="style1 list" style="font-family: 'Jua', sans-serif;"><a href="#" style="font-family: 'Jua', sans-serif;">${comment.nickname}(${maskingName(comment.username)})</a></h4>
                                    <p class="para top" style="font-family: 'Jua', sans-serif;" >${formattedDate}</p>
                                    <br/>
                                    <br/>
                                    <h4 class="style1 list userComment" data-comment-id="${comment.id}">${comment.content}</h4>
                                    <br/>
                                    <img class="emoji" src="${comment.emoji}" alt="emoji" data-comment-id="${comment.id}">
                                    <br/>
                                    <br/>
                                    <div class="commentsLikes" data-comment-id="${comment.id}">
                                        <a class="commentLikes" style="cursor: pointer" data-comment-id="${comment.id}" onclick="unlikesClick(${postId}, ${comment.id}, ${comment.likeCnt})">❤️</a>
                                        <span class="commentcnt" data-comment-id="${comment.id}">${comment.likeCnt}</span>
                                    </div>
                                    <a class="btn1 editComments" style="font-family: 'Jua', sans-serif; cursor: pointer" data-comment-id="${comment.id}">수정</a>
                                    <a class="btn1 editCommentsClose" style="font-family: 'Jua', sans-serif; cursor: pointer; display: none" data-comment-id="${comment.id}">수정창닫기</a>
                                    <a class="btn1 deleteComments" style="font-family: 'Jua', sans-serif; cursor: pointer" data-comment-id="${comment.id}">삭제</a>
                                    <a class="btn1 replyCreate" style="font-family: 'Jua', sans-serif; cursor: pointer" data-comment-id="${comment.id}">답글달기</a>
                                    <a class="btn1 replyCreateClose" style="font-family: 'Jua', sans-serif; cursor: pointer; display: none" data-comment-id="${comment.id}">답글입력창닫기</a>
                                    <a class="btn1 replyRead" style="font-family: 'Jua', sans-serif; cursor: pointer" data-comment-id="${comment.id}" onclick="openReply(${comment.id})">답글 펼치기 (0개)</a>
                                    <a class="btn1 replyClose" style="font-family: 'Jua', sans-serif; cursor: pointer; display: none" data-comment-id="${comment.id}" onclick="closeReply(${comment.id})">답글 닫기</a>
                                </div>
                                <div class="grid_text userCommentEdit-Form" style="display: none; position: relative" data-comment-id="${comment.id}">
                                    <label style="font-family: 'Jua', sans-serif;">수정창<span>*</span></label>
                                    <button class="editshow" type="button" data-comment-id="${comment.id}" onclick="editpopup(${comment.id})">😀</button>
                                    <textarea type="text" class="userCommentEdit" name="content" placeholder="내용을 입력해주세요." data-comment-id="${comment.id}"></textarea>
                                    <button class="btn btn-secondary userEditCommentsComplete" type="button" data-comment-id="${comment.id}">댓글 수정</button>
                                </div>
                                <div class="grid_text userReply-Form" style="display: none; position: relative" data-comment-id="${comment.id}">
                                    <label style="font-family: 'Jua', sans-serif;">답글창<span>*</span></label>
                                    <textarea type="text" class="userReply" name="content" placeholder="내용을 입력해주세요." data-comment-id="${comment.id}"></textarea>
                                    <button class="replyshow" type="button" data-comment-id="${comment.id}" onclick="replypopup(${comment.id})">😀</button>
                                    <button class="btn btn-secondary  btn-replycreate" type="button" data-comment-id="${comment.id}" onclick="replyCreate(${postId}, ${comment.id})">답글 작성</button>
                                </div>
                            `
                            }

                            let commentWrapper = $(`<div class="commentbox" data-comment-id="${comment.id}">${temp_html}</div>`);

                            // console.log(comment.username)

                            // 답글 가져오기
                            $.ajax({
                                url: "/api/posts/" + postId + "/comments/" + comment.id + "/reply",
                                method: "GET",
                                dataType: "json",
                                success: function (replies) {
                                    let data2 = replies.result.replys;
                                    var repliesContainer = $(`<div class="grid1_of_2 left replyLists" id="replyList" style="display: none" data-comment-id="${comment.id}"></div>`);

                                    $.ajax({
                                        url: "/api/posts/" + postId + "/comments/" + comment.id + "/reply/likes",
                                        method: "GET",
                                        dataType: "json",
                                        success: function (replylikesval) {
                                            let replyLikesData = replylikesval.result;
                                            // console.log(replyLikesData)

                                            data2.forEach(function (reply) {
                                                const createdAt = reply.createdAt;

                                                const date = new Date(createdAt);

                                                const formattedDate = date.toLocaleString("ko-KR", {
                                                    year: "numeric",
                                                    month: "2-digit",
                                                    day: "2-digit",
                                                    hour: "2-digit",
                                                    minute: "2-digit",
                                                    second: "2-digit",
                                                });

                                                const commentId = comment.id;

                                                let temp_html = `
                                                    <div  class="grid1_of_2 left" id="replyList" data-reply-id="${reply.id}">
                                                        <div class="grid_text">
                                                            <hr style="border-top: 1px solid #6c757d;">
                                                            <div class="grid_img">
                                                                <img class="profile-img" src="${reply.img}" alt="My Image" style="border-radius: 50%;" data-reply-id="${reply.id}">
                                                            </div>
                                                            <h4 class="style1 list" style="font-family: 'Jua', sans-serif;"><a href="#" style="font-family: 'Jua', sans-serif;" data-reply-id="${reply.id}">${reply.nickname}(${maskingName(reply.username)})</a></h4>
                                                            <p class="para top" style="font-family: 'Jua', sans-serif;" data-reply-id="${reply.id}">${formattedDate}</p>
                                                            <br/>
                                                            <h4 class="style1 list replyContents" data-reply-id="${reply.id}">${reply.content}</h4>
                                                            <br/>
                                                            <img class="emoji" src="${reply.emoji}" alt="emoji" data-reply-id="${reply.id}">
                                                            <div class="replyslikes" data-reply-id="${reply.id}">
                                                                <a class="unreplysLikes" style="cursor: pointer" data-reply-id="${reply.id}" onclick="replyLikesClick(${postId}, ${commentId}, ${reply.id}, ${reply.likeCnt})">🤍</a>
                                                                <span class="likecnt" data-reply-id="${reply.id}">${reply.likeCnt}</span>
                                                            </div>
                                                            <a class="btn1 replyedit" style="font-family: 'Jua', sans-serif; cursor: pointer" data-reply-id="${reply.id}" onclick="openReplyEdit(${reply.id})">수정</a>
                                                            <a class="btn1 replyeditclose" style="font-family: 'Jua', sans-serif; cursor: pointer; display: none;" data-reply-id="${reply.id}" onclick="closeReplyEdit(${reply.id})">수정취소</a>
                                                            <a class="btn1 replydelete" style="font-family: 'Jua', sans-serif; cursor: pointer" data-reply-id="${reply.id}" onclick="replyDelete(${postId},${commentId},${reply.id})">삭제</a>
                                                        </div>
                                                        <div class="grid_text userReplyEdit-Form" style="display: none; position: relative" data-reply-id="${reply.id}">
                                                            <label style="font-family: 'Jua', sans-serif;">답글수정창<span>*</span></label>
                                                            <textarea type="text" class="userReplyEdit" name="content" placeholder="내용을 입력해주세요." data-reply-id="${reply.id}"></textarea>
                                                            <button class="editreplyshow" type="button" data-reply-id="${reply.id}" onclick="editreplypopup(${reply.id})">😀</button>
                                                            <button class="btn btn-secondary btn-replyedit" type="button" data-reply-id="${reply.id}" onclick="replyEdit(${postId},${commentId},${reply.id})">답글 수정</button>
                                                        </div>
                                                    </div>
                                                `

                                                // 해당 댓글에 대한 좋아요 정보 검사
                                                var replylikeInfo = replyLikesData.find(function (replylike) {
                                                    return replylike.comment_id === comment.id && replylike.reply_id === reply.id && replylike.username === usernames;
                                                });

                                                if (replylikeInfo && replylikeInfo.likes) {
                                                    temp_html = `
                                                    <div  class="grid1_of_2 left" id="replyList" data-reply-id="${reply.id}" style="display: none">
                                                        <div class="grid_text">
                                                            <hr style="border-top: 1px solid #6c757d;">
                                                            <div class="grid_img">
                                                                <img class="profile-img" src="${reply.img}" alt="My Image" style="border-radius: 50%;" data-reply-id="${reply.id}">
                                                            </div>
                                                            <h4 class="style1 list" style="font-family: 'Jua', sans-serif;"><a href="#" style="font-family: 'Jua', sans-serif;" data-reply-id="${reply.id}">${reply.nickname}(${maskingName(reply.username)})</a></h4>
                                                            <p class="para top" style="font-family: 'Jua', sans-serif;" data-reply-id="${reply.id}">${formattedDate}</p>
                                                            <br/>
                                                            <h4 class="style1 list replyContents" data-reply-id="${reply.id}">${reply.content}</h4>
                                                            <br/>
                                                            <img class="emoji" src="${reply.emoji}" alt="emoji" data-reply-id="${reply.id}">
                                                            <div class="replyslikes" data-reply-id="${reply.id}">
                                                                <a class="replysLikes" style="cursor: pointer" data-reply-id="${reply.id}" onclick="replyUnlikesClick(${postId}, ${commentId}, ${reply.id}, ${reply.likeCnt})">❤️</a>
                                                                <span class="likecnt" data-reply-id="${reply.id}">${reply.likeCnt}</span>
                                                            </div>
                                                            <a class="btn1 replyedit" style="font-family: 'Jua', sans-serif; cursor: pointer" data-reply-id="${reply.id}" onclick="openReplyEdit(${reply.id})">수정</a>
                                                            <a class="btn1 replyeditclose" style="font-family: 'Jua', sans-serif; cursor: pointer; display: none;" data-reply-id="${reply.id}" onclick="closeReplyEdit(${reply.id})">수정취소</a>
                                                            <a class="btn1 replydelete" style="font-family: 'Jua', sans-serif; cursor: pointer" data-reply-id="${reply.id}" onclick="replyDelete(${postId},${commentId},${reply.id})">삭제</a>
                                                        </div>
                                                        <div class="grid_text userReplyEdit-Form" style="display: none; position: relative" data-reply-id="${reply.id}">
                                                            <label style="font-family: 'Jua', sans-serif;">답글수정창<span>*</span></label>
                                                            <textarea type="text" class="userReplyEdit" name="content" placeholder="내용을 입력해주세요." data-reply-id="${reply.id}"></textarea>
                                                            <button class="editreplyshow" type="button" data-reply-id="${reply.id}" onclick="editreplypopup(${reply.id})">😀</button>
                                                            <button class="btn btn-secondary btn-replyedit" type="button" data-reply-id="${reply.id}" onclick="replyEdit(${postId},${commentId}, ${reply.id})">답글 수정</button>
                                                        </div>
                                                        </div>
                                                    `
                                                }

                                                // 각 답글에 대한 좋아요 정보 가져오기
                                                repliesContainer.append(temp_html);

                                                // 답글 갯수 구한후 텍스트 변환
                                                let replyCount = data2.length;
                                                $(`.commentbox[data-comment-id="${comment.id}"] .replyRead`).text(`답글 펼치기 (${replyCount}개)`);

                                                // 답글 작성자와 일치하지 않는경우 수정버튼 삭제버튼 없앰
                                                if (reply.username !== usernames) {
                                                    $(`.replyedit[data-reply-id="${reply.id}"]`).remove();
                                                    $(`.replydelete[data-reply-id="${reply.id}"]`).remove();
                                                }

                                                // 답글의 이모티콘이 존재하지 않을경우 이모티콘 태그를 숨김
                                                if (reply.emoji === undefined || reply.emoji === "" || reply.emoji === null) {
                                                    $(`.emoji[data-reply-id="${reply.id}"]`).hide();
                                                    // console.log("이미지 생성")
                                                }

                                                if (reply.img === null) {
                                                    // console.log(reply.img)
                                                    $(`.profile-img[data-reply-id="${reply.id}"]`).attr("src", "/img/free-icon-user-9435149.png");
                                                }

                                                if (usernames === "Guest") {
                                                    $(`.unreplysLikes[data-reply-id="${reply.id}"]`).removeAttr("onclick");
                                                    $(`.replysLikes[data-reply-id="${reply.id}"]`).removeAttr("onclick");
                                                }
                                            })
                                        }

                                    });

                                    // 답글을 댓글 아래에 추가
                                    commentWrapper.append(repliesContainer);
                                }
                            });

                            commentsContainer.append(commentWrapper);

                            // 만약 로그인한 유저와 댓글을 단 유저가 일치하지 않을경우 버튼 삭제
                            if (comment.username !== usernames) {
                                $(`.editComments[data-comment-id="${comment.id}"]`).remove();
                                $(`.deleteComments[data-comment-id="${comment.id}"]`).remove();
                            }

                            if (usernames === "Guest") {
                                $(`.editComments[data-comment-id="${comment.id}"]`).remove();
                                $(`.deleteComments[data-comment-id="${comment.id}"]`).remove();
                                $(`.replyCreate[data-comment-id="${comment.id}"]`).remove();
                                $(`.commentunLikes[data-comment-id="${comment.id}"]`).removeAttr("onclick");
                                $(`.commentLikes[data-comment-id="${comment.id}"]`).removeAttr("onclick");
                            }

                            if (comment.emoji === null || comment.emoji === "") {
                                $(`.emoji[data-comment-id="${comment.id}"]`).hide();
                            }

                            if (comment.img === null) {
                                $(`.profile-img[data-comment-id="${comment.id}"]`).attr("src", "/img/free-icon-user-9435149.png");
                            }
                        });
                    }
                });
            },

            timeout: 300000
        });
    }

    // 이전버튼, 다음버튼 클릭
    function updatePageButtons() {
        const pageNumbersContainer = paginationContainer.find('.page-numbers');
        pageNumbersContainer.empty();
        const maxPage = Math.min(totalPages, endPage + 1); // 실제 데이터가 있는 페이지까지만 생성

        for (let i = startPage; i < maxPage; i++) {

            const pageNumber = i + 1; // 페이지 번호는 1부터 시작
            const activeClass = i === currentPage ? 'active' : '';
            const pageButton = `<li class="page-item page-numbers list-inline-item" style="margin-right: 0; cursor: pointer">
                                <a class="page-link page-button ${activeClass} pagenumber" data-page="${i}">
                                    ${pageNumber}
                                </a>
                            </li>`;
            pageNumbersContainer.append(pageButton);
        }

        // console.log(startPage, endPage)
    }

    // 댓글 페이지 추가
    function updatePagination() {
        updatePageButtons();
        paginationContainer.find('.prev-page-button').toggleClass('disabled', currentPage < 10);
        paginationContainer.find('.next-page-button').toggleClass('disabled', currentPage + 1 > totalPages.toString().slice(0, -1) * 10);
        if (totalPages === totalPages.toString().slice(0, -1) * 10) {
            paginationContainer.find('.next-page-button').toggleClass('disabled', currentPage + 1 > totalPages.toString().slice(0, -1) * 10 - 10);
        }
        // console.log(currentPage)
        // console.log(totalPages)
        // console.log(totalPages.toString().slice(0, -1) * 10)
    }

    // 초기 페이지 로드
    updatePagination();
    loadCommentsAndReplies(currentPage);

    // 페이지 번호 클릭 시 해당 페이지 댓글/답글 가져오기
    $('.pagination').on('click', '.pagenumber', function () {
        const page = parseInt($(this).data('page'));
        currentPage = page; // 활성화된 페이지 번호를 업데이트
        updatePagination();
        loadCommentsAndReplies(page);
    });

    // 이전 페이지 버튼 클릭 시 이전 페이지 댓글/답글 가져오기
    $('.prev-page-button').click(function () {
        if (currentPage > 0) {
            if (currentPage < 1) {
                currentPage -= pageSize;
            } else {
                currentPage = currentPage.toString().slice(0, -1) * 10 - pageSize;
                // console.log(currentPage)
            }
            if (currentPage < startPage) {
                startPage -= 10;
                endPage -= 10;
            }
            updatePagination();
            loadCommentsAndReplies(currentPage);
        }
    });

    // 다음 페이지 버튼 클릭 시 다음 페이지 댓글/답글 가져오기
    $('.next-page-button').click(function () {
        if (currentPage < totalPages - 1) {
            if (currentPage < 1) {
                currentPage += pageSize;
            } else {
                currentPage = currentPage.toString().slice(0, -1) * 10 + pageSize;
                // console.log(currentPage)
            }
            if (currentPage > endPage) {
                startPage += 10;
                endPage += 10;
            }
            updatePagination();
            loadCommentsAndReplies(currentPage);
        }
    });

    // 댓글 작성
    $('#createComments').click(function () {
        const postId = getPostIdFromUrl();
        const content = $('#userComment').val();
        const img = $('#img-wrap div img').attr("src")

        if (img === undefined && content.length < 1) {
            Swal.fire({
                icon: 'warning',
                title: '댓글 작성실패',
                text: '이모티콘 없이 댓글을 공백으로 작성할 수 없습니다.',
            });
            $('#userComment').focus();
            return false;
        }

        $.ajax({
            type: 'POST',
            url: `/api/posts/${postId}/comments`,
            headers: {
                "Content-Type": "application/json",
                "Authorization": token
            },
            data: JSON.stringify({
                content: content,
                emoji: img
            }),
            beforeSend: function() {
                $('#eventLoading').show();
                $('body').on('scroll touchmove mousewheel', function(e) {
                    e.stopPropagation();
                    return false;
                });
            },
            complete:function() {
                $('#eventLoading').hide();
                $('body').off('scroll touchmove mousewheel')
            },
            success: function (data) {
                Swal.fire({
                    icon: 'success',
                    text: `${data.message}`
                });
                // 댓글 작성 후 작성된 댓글이 있는 페이지 번호 계산
                // const commentsPerPage = 10; // 페이지당 댓글 수
                // const commentPage = Math.floor(commentIndex / commentsPerPage);
                let commentIndex = parseInt(totalItems.toString().slice(0, -1));
                // console.log("totalPages = " + totalPages)
                // console.log("commentIndex = " + commentIndex)

                // 페이지 이동
                if (isNaN(commentIndex) || isNaN(startPage) || isNaN(endPage)) {
                    commentIndex = 0
                    startPage = 0
                    endPage = 0
                }
                currentPage = commentIndex; // currentPage 업데이트
                startPage = commentIndex.toString().slice(0, -1) * 10;
                endPage = startPage + 9;
                console.log("startPage = " + startPage)
                console.log("endPage = " + endPage)
                updatePagination();
                loadCommentsAndReplies(currentPage);
                $('#userComment').val("");
                $('#img-wrap').remove();

                $.ajax({
                    url: `/api/posts/${postId}/commentlist`,
                    method: 'GET',
                    success: function (data) {
                        const commentsList = data.result.comments.length;

                        $('.comment-h2').text("댓글 ( 댓글수 : " + commentsList + " )");
                    }
                })
            },
            error: function (e) {
                Swal.fire({
                    icon: 'warning',
                    title: '댓글 작성실패',
                    text: `${e.responseJSON.message}`
                });
            },

            timeout: 300000
        })
    })

    // 댓글 수정
    $(document).on('click', '.userEditCommentsComplete', function () {
        const postId = getPostIdFromUrl();
        const commentId = $(this).data('comment-id');
        const content = $(`.userCommentEdit[data-comment-id="${commentId}"]`).val()
        const img = $(`.editemoji-container[data-comment-id="${commentId}"] .editCommentEmoji img`).attr("src")
        console.log(commentId)

        if (img === undefined && content.length < 1) {
            Swal.fire({
                icon: 'warning',
                title: '댓글 수정실패',
                text: '이모티콘 없이 댓글을 공백으로 작성할 수 없습니다.',
            });
            $(`.userCommentEdit[data-comment-id="${commentId}"]`).focus();
            return false;
        }

        Swal.fire({
            title: '댓글을 수정하시겠습니까?',
            text: "확인을 누르시면 수정이 완료됩니다.",
            icon: 'info',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: '확인',
            cancelButtonText: '취소'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    type: 'PUT',
                    url: `/api/posts/${postId}/comments/${commentId}`,
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": token
                    },
                    data: JSON.stringify({
                        content: content,
                        emoji: img
                    }),
                    beforeSend: function() {
                        $('#eventLoading').show();
                        $('body').on('scroll touchmove mousewheel', function(e) {
                            e.stopPropagation();
                            return false;
                        });
                    },
                    complete:function() {
                        $('#eventLoading').hide();
                        $('body').off('scroll touchmove mousewheel')
                    },
                    success: function (data) {
                        const content = $(`.userCommentEdit[data-comment-id="${commentId}"]`).val();
                        const emoji = $(`.editemoji-container[data-comment-id="${commentId}"] .editCommentEmoji img`).attr("src");
                        Swal.fire({
                            icon: 'success',
                            text: `${data.message}`
                        });
                        $(`.userComment[data-comment-id="${commentId}"]`).text(content);
                        $(`.editemoji-container[data-comment-id="${commentId}"]`).remove();
                        $(`.userReply-Form[data-comment-id="${commentId}"]`).hide();
                        $(`.replyCreateClose[data-comment-id="${commentId}"]`).hide();
                        $(`.replyCreate[data-comment-id="${commentId}"]`).show();
                        $(`.userCommentEdit-Form[data-comment-id="${commentId}"]`).fadeOut('fast');
                        $(`.editCommentsClose[data-comment-id="${commentId}"]`).hide();
                        $(`.editComments[data-comment-id="${commentId}"]`).show();
                        $(`.userCommentEdit[data-comment-id="${commentId}"]`).val("");
                        $(`.emoji[data-comment-id="${commentId}"]`).show().attr("src", emoji);

                        // $(`.emoji[data-comment-id="${commentId}"]`).attr("src", emoji);

                        if (emoji === "" || emoji === undefined || emoji === null) {
                            $(`.emoji[data-comment-id="${commentId}"]`).hide().attr("src", "null");
                            // $(`.emoji[data-comment-id="${commentId}"]`).attr("src", "null");
                        }
                        console.log(img)
                    },
                    error: function (e) {
                        console.log(e)
                        alert(e.responseJSON.message)
                        Swal.fire({
                            icon: 'error',
                            title: '댓글 수정실패',
                            text: `${e.responseJSON.message}`
                        });
                    },

                    timeout: 300000
                })
            }
        })
    })

    // 댓글 삭제
    $(document).on('click', '.deleteComments', function () {
        const postId = getPostIdFromUrl();
        const commentId = $(this).data('comment-id');
        // console.log(commentId);
        Swal.fire({
            title: '댓글을 삭제하시겠습니까?',
            text: "확인을 누르시면 삭제가 완료됩니다.",
            icon: 'info',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: '확인',
            cancelButtonText: '취소'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    type: 'DELETE',
                    url: `/api/posts/${postId}/comments/${commentId}`,
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": token
                    },
                    beforeSend: function() {
                        $('#eventLoading').show();
                        $('body').on('scroll touchmove mousewheel', function(e) {
                            e.stopPropagation();
                            return false;
                        });
                    },
                    complete:function() {
                        $('#eventLoading').hide();
                        $('body').off('scroll touchmove mousewheel')
                    },
                    success: function (data) {
                        Swal.fire({
                            icon: 'success',
                            text: `${data.message}`
                        });
                        $(`.commentbox[data-comment-id="${commentId}"]`).remove()
                        totalItems--
                        // updatePagination();
                        // loadCommentsAndReplies(currentPage);
                        // console.log(currentPage)
                        // console.log(totalItems)

                        if ($('.commentbox').length === 0) {
                            --currentPage;
                            // console.log("commentIndex = " + currentPage)
                            startPage = currentPage.toString().slice(0, -1) * 10;
                            endPage = startPage + 9;
                            updatePagination();
                            loadCommentsAndReplies(currentPage);
                        }

                        $.ajax({
                            url: `/api/posts/${postId}/comments?page=${currentPage}&size=${pageSize}`,
                            method: 'GET',
                            success: function (data) {
                                const comments = data.result.comments;
                                totalPages = data.result.paginationInfo.totalPages;
                                displayCommentsAndReplies(comments, currentPage);
                                updatePagination(totalPages, currentPage);
                                // console.log(currentPage,pageSize)
                            }
                        });

                        $.ajax({
                            url: `/api/posts/${postId}/commentlist`,
                            method: 'GET',
                            success: function (data) {
                                const commentsList = data.result.comments.length;

                                $('.comment-h2').text("댓글 ( 댓글수 : " + commentsList + " )");
                            }
                        })
                    },
                    error: function (e) {
                        Swal.fire({
                            icon: 'error',
                            title: '댓글 삭제실패',
                            text: `${e.responseJSON.message}`
                        });
                    },

                    timeout: 300000
                })
            }
        })
    })


});

// 답글 생성
function replyCreate(postId, commentId) {
    const createReplyValue = $(`.userReply[data-comment-id="${commentId}"]`).val();
    const img = $(`.replyemoji-container[data-comment-id="${commentId}"] img`).attr("src")
    console.log(commentId)

    if (img === undefined && createReplyValue.length < 1) {
        Swal.fire({
            icon: 'warning',
            title: '답글 생성실패',
            text: '이모티콘 없이 답글을 공백으로 작성할 수 없습니다.',
        });
        $(`.userReply[data-comment-id="${commentId}"]`).focus();
        return false;
    }
    $.ajax({
        type: 'POST',
        url: `/api/posts/${postId}/comments/${commentId}/reply`,
        headers: {
            "Content-Type": "application/json",
            "Authorization": token
        },
        data: JSON.stringify({
            content: createReplyValue,
            emoji: img
        }),
        beforeSend: function() {
            $('#eventLoading').show();
            $('body').on('scroll touchmove mousewheel', function(e) {
                e.stopPropagation();
                return false;
            });
        },
        complete:function() {
            $('#eventLoading').hide();
            $('body').off('scroll touchmove mousewheel')
        },
        success: function (data) {
            Swal.fire({
                icon: 'success',
                text: `${data.message}`
            });
            const emoji = data.result.emoji;

            const createdAt = data.result.createdAt;

            const date = new Date(createdAt);

            const formattedDate = date.toLocaleString("ko-KR", {
                year: "numeric",
                month: "2-digit",
                day: "2-digit",
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit",
            });

            let temp_html = `
            <div  class="grid1_of_2 left" id="replyList" data-reply-id="${data.result.id}">
                <div class="grid_text">
                    <hr style="border-top: 1px solid #6c757d;">
                    <div class="grid_img">
                        <img class="profile-img" src="${data.result.img}" alt="My Image" style="border-radius: 50%;" data-reply-id="${data.result.id}">
                    </div>
                    <h4 class="style1 list" style="font-family: 'Jua', sans-serif;"><a href="#" style="font-family: 'Jua', sans-serif;" data-reply-id="${data.result.id}">${data.result.nickname}(${maskingName(data.result.username)})</a></h4>
                    <p class="para top" style="font-family: 'Jua', sans-serif;" data-reply-id="${data.result.id}">${formattedDate}</p>
                    <br/>
                    <h4 class="style1 list replyContents" data-reply-id="${data.result.id}">${data.result.content}</h4>
                    <br/>
                    <img class="emoji" src="${data.result.emoji}" alt="emoji" data-reply-id="${data.result.id}">
                    <div class="replyslikes" data-reply-id="${data.result.id}">
                        <a class="unreplysLikes" style="cursor: pointer" data-reply-id="${data.result.id}" onclick="replyLikesClick(${postId}, ${commentId}, ${data.result.id}, ${data.result.likeCnt})">🤍</a>
                        <span class="likecnt" data-reply-id="${data.result.id}">${data.result.likeCnt}</span>
                    </div>
                    <a class="btn1 replyedit" style="font-family: 'Jua', sans-serif; cursor: pointer" data-reply-id="${data.result.id}" onclick="openReplyEdit(${data.result.id})">수정</a>
                    <a class="btn1 replyeditclose" style="font-family: 'Jua', sans-serif; cursor: pointer; display: none;" data-reply-id="${data.result.id}" onclick="closeReplyEdit(${data.result.id})">수정취소</a>
                    <a class="btn1 replydelete" style="font-family: 'Jua', sans-serif; cursor: pointer" data-reply-id="${data.result.id}" onclick="replyDelete(${postId},${data.result.comment_id},${data.result.id})">삭제</a>
                </div>
                <div class="grid_text userReplyEdit-Form" style="display: none; position: relative" data-reply-id="${data.result.id}">
                    <label style="font-family: 'Jua', sans-serif;">답글수정창<span>*</span></label>
                    <textarea type="text" class="userReplyEdit" name="content" placeholder="내용을 입력해주세요." data-reply-id="${data.result.id}"></textarea>
                    <button class="editreplyshow" type="button" data-reply-id="${data.result.id}" onclick="editreplypopup(${data.result.id})">😀</button>
                    <button class="btn btn-secondary btn-replyedit" type="button" data-reply-id="${data.result.id}" onclick="replyEdit(${postId},${commentId},${data.result.id})">답글 수정</button>
                </div>
            </div>
            `

            $(`.replyLists[data-comment-id="${commentId}"]`).append(temp_html);
            $(`.replyRead[data-comment-id="${commentId}"]`).text(`댓글 펼치기 (${$(`.replyLists[data-comment-id="${commentId}"] #replyList`).length}개)`);
            $(`.commentbox[data-comment-id="${commentId}"] #replyList`).show();
            $(`.commentbox[data-comment-id="${commentId}"] .replyRead`).hide();
            $(`.commentbox[data-comment-id="${commentId}"] .replyClose`).show();
            $(`.userReply[data-comment-id="${commentId}"]`).val("");
            $(`.replyemoji-container[data-comment-id="${commentId}"]`).remove();
            $(`.replyshow[data-comment-id="${commentId}"]`).css('top', '86%');
            $(`.btn-replycreate[data-comment-id="${commentId}"]`).css('top', '79.7%');
            $(`.userReply-Form[data-comment-id="${commentId}"]`).hide();
            $(`.replyCreateClose[data-comment-id="${commentId}"]`).hide();
            $(`.replyCreate[data-comment-id="${commentId}"]`).show();

            if (emoji === undefined || emoji === "" || emoji === null) {
                $(`.emoji[data-reply-id="${data.result.id}"]`).hide();
            }

            if (data.result.img === null) {
                console.log(data.result.img)
                $(`.profile-img[data-reply-id="${data.result.id}"]`).attr("src", "/img/free-icon-user-9435149.png");
            }
        },
        error: function (e) {
            console.log(createReplyValue)
            console.log(img)
            console.log(e)
            Swal.fire({
                icon: 'error',
                title: '답글 생성실패',
                text: `${e.responseJSON.message}`
            });
        },

        timeout: 300000
    })
}

// 답글 수정
function replyEdit(postId, commentId, replyId) {
    const replyContexts = $(`.userReplyEdit[data-reply-id="${replyId}"]`).val()
    const img = $(`.editreplyemoji-container[data-reply-id="${replyId}"] img`).attr("src")

    if (img === undefined && replyContexts.length < 1) {
        Swal.fire({
            icon: 'warning',
            title: '답글 수정실패',
            text: '이모티콘 없이 답글을 공백으로 작성할 수 없습니다.',
        });
        $(`.userReplyEdit[data-reply-id="${replyId}"]`).focus();
        return false;
    }

    Swal.fire({
        title: '답글을 수정하시겠습니까?',
        text: "확인을 누르시면 수정이 완료됩니다.",
        icon: 'info',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: '확인',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            // console.log(token);
            $.ajax({
                type: 'PUT',
                url: `/api/posts/${postId}/comments/${commentId}/reply/${replyId}`,
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": token
                },
                data: JSON.stringify({
                    content: replyContexts,
                    emoji: img
                }),
                beforeSend: function() {
                    $('#eventLoading').show();
                    $('body').on('scroll touchmove mousewheel', function(e) {
                        e.stopPropagation();
                        return false;
                    });
                },
                complete:function() {
                    $('#eventLoading').hide();
                    $('body').off('scroll touchmove mousewheel')
                },
                success: function (data) {
                    Swal.fire({
                        icon: 'success',
                        text: `${data.message}`
                    });
                    $(`.replyContents[data-reply-id="${replyId}"]`).text(replyContexts);
                    $(`.userReplyEdit[data-reply-id="${replyId}"]`).val("");
                    $(`.editreplyemoji-container[data-reply-id="${replyId}"]`).remove();

                    $(`.emoji[data-reply-id="${replyId}"]`).show().attr("src", img);

                    // $(`.emoji[data-reply-id="${replyId}"]`).attr("src", img);

                    if (img === "" || img === undefined || img === null) {
                        $(`.emoji[data-reply-id="${replyId}"]`).hide().attr("src", "null");
                        // $(`.emoji[data-reply-id="${replyId}"]`).attr("src", "null");
                        console.log("진입")
                    }
                    closeReplyEdit(replyId)
                },
                error: function (e) {
                    console.log(e.responseJSON.message);
                    Swal.fire({
                        icon: 'error',
                        title: '답글 수정실패',
                        text: `${e.responseJSON.message}`
                    });
                },

                timeout: 300000
            })
        }
    })
}

// 답글 삭제
function replyDelete(postId, commentId, replyId) {

    Swal.fire({
        title: '답글을 삭제하시겠습니까?',
        text: "확인을 누르시면 삭제가 완료됩니다.",
        icon: 'info',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: '확인',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            // console.log(token);
            $.ajax({
                type: 'DELETE',
                url: `/api/posts/${postId}/comments/${commentId}/reply/${replyId}`,
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": token
                },
                data: {},
                beforeSend: function() {
                    $('#eventLoading').show();
                    $('body').on('scroll touchmove mousewheel', function(e) {
                        e.stopPropagation();
                        return false;
                    });
                },
                complete:function() {
                    $('#eventLoading').hide();
                    $('body').off('scroll touchmove mousewheel')
                },
                success: function (data) {
                    Swal.fire({
                        icon: 'success',
                        text: `${data.message}`
                    });
                    $(`#replyList[data-reply-id="${replyId}"]`).remove()
                    $(`.replyRead[data-comment-id="${commentId}"]`).text(`댓글 펼치기 (${$(`.replyLists[data-comment-id="${commentId}"] #replyList`).length}개)`)
                    const replyList = $(`.replyLists[data-comment-id="${commentId}"] #replyList`).length

                    if (replyList === 0) {
                        closeReply(commentId)
                    }
                },
                error: function (e) {
                    console.log(e.responseJSON.message);
                    Swal.fire({
                        icon: 'error',
                        title: '답글 수정실패',
                        text: `${e.responseJSON.message}`
                    });
                },

                timeout: 300000
            })
        }
    })
}

// 댓글 좋아요 추가
function likesClick(postId, commentId, cnt) {

    // console.log(token);
    $.ajax({
        type: 'POST',
        url: `/api/posts/${postId}/comments/${commentId}/likes`,
        headers: {
            "Content-Type": "application/json",
            "Authorization": token
        },
        data: {},
        beforeSend: function() {
            $('#eventLoading').show();
            $('body').on('scroll touchmove mousewheel', function(e) {
                e.stopPropagation();
                return false;
            });
        },
        complete:function() {
            $('#eventLoading').hide();
            $('body').off('scroll touchmove mousewheel')
        },
        success: function (data) {

            const updateCnt = cnt += 1;

            $(`.commentunLikes[data-comment-id=${commentId}]`).empty();
            $(`.commentcnt[data-comment-id=${commentId}]`).empty();

            const temp_html = `
                    <a class="commentLikes" style="cursor: pointer" data-comment-id="${commentId}" onclick="unlikesClick(${postId}, ${commentId}, ${cnt})">❤️</a>
                    <span class="commentcnt" data-comment-id="${commentId}">${updateCnt}</span>
                `

            $(`.commentsLikes[data-comment-id=${commentId}]`).append(temp_html);
        },
        error: function (e) {
            console.log(e);
            Swal.fire({
                icon: 'error',
                title: '좋아요 실패',
                text: `${e.responseJSON.message}`
            });
        },

        timeout: 300000
    })
}

// 댓글 좋아요 취소
function unlikesClick(postId, commentId, cnt) {

    // console.log(token);
    $.ajax({
        type: 'PUT',
        url: `/api/posts/${postId}/comments/${commentId}/likes`,
        headers: {
            "Content-Type": "application/json",
            "Authorization": token
        },
        data: {},
        beforeSend: function() {
            $('#eventLoading').show();
            $('body').on('scroll touchmove mousewheel', function(e) {
                e.stopPropagation();
                return false;
            });
        },
        complete:function() {
            $('#eventLoading').hide();
            $('body').off('scroll touchmove mousewheel')
        },
        success: function (data) {

            const updateCnt = cnt -= 1;

            $(`.commentLikes[data-comment-id=${commentId}]`).empty();
            $(`.commentcnt[data-comment-id=${commentId}]`).empty();

            const temp_html = `
                    <a class="commentunLikes" style="cursor: pointer" data-comment-id="${commentId}" onclick="likesClick(${postId}, ${commentId}, ${cnt})">🤍</a>
                    <span class="commentcnt" data-comment-id="${commentId}">${updateCnt}</span>
                `

            $(`.commentsLikes[data-comment-id=${commentId}]`).append(temp_html);
        },
        error: function (e) {
            console.log(e);
            Swal.fire({
                icon: 'error',
                title: '좋아요 취소 실패',
                text: `${e.responseJSON.message}`
            });
        },

        timeout: 300000
    })
}

// 답글 좋아요 추가
function replyLikesClick(postId, commentId, replyId, cnt) {

    // console.log(token);
    $.ajax({
        type: 'POST',
        url: `/api/posts/${postId}/comments/${commentId}/reply/${replyId}/likes`,
        headers: {
            "Content-Type": "application/json",
            "Authorization": token
        },
        data: {},
        beforeSend: function() {
            $('#eventLoading').show();
            $('body').on('scroll touchmove mousewheel', function(e) {
                e.stopPropagation();
                return false;
            });
        },
        complete:function() {
            $('#eventLoading').hide();
            $('body').off('scroll touchmove mousewheel')
        },
        success: function (data) {
            const updateCnt = cnt += 1;

            $(`.unreplysLikes[data-reply-id=${replyId}]`).empty();
            $(`.likecnt[data-reply-id=${replyId}]`).empty();

            const temp_html = `
                    <a class="replysLikes" style="cursor: pointer" data-reply-id="${replyId}" onclick="replyUnlikesClick(${postId}, ${commentId}, ${replyId}, ${cnt})">❤️</a>
                    <span class="likecnt" data-reply-id="${replyId}">${updateCnt}</span>
                `

            $(`.replyslikes[data-reply-id=${replyId}]`).append(temp_html);
        },
        error: function (e) {
            Swal.fire({
                icon: 'error',
                title: '좋아요 실패',
                text: `${e.responseJSON.message}`
            });
        },

        timeout: 300000
    })
}

// 답글 좋아요 취소
function replyUnlikesClick(postId, commentId, replyId, cnt) {

    $.ajax({
        type: 'PUT',
        url: `/api/posts/${postId}/comments/${commentId}/reply/${replyId}/likes`,
        headers: {
            "Content-Type": "application/json",
            "Authorization": token
        },
        data: {},
        beforeSend: function() {
            $('#eventLoading').show();
            $('body').on('scroll touchmove mousewheel', function(e) {
                e.stopPropagation();
                return false;
            });
        },
        complete:function() {
            $('#eventLoading').hide();
            $('body').off('scroll touchmove mousewheel')
        },
        success: function (data) {

            const updateCnt = cnt -= 1;

            $(`.replysLikes[data-reply-id=${replyId}]`).empty();
            $(`.likecnt[data-reply-id=${replyId}]`).empty();

            const temp_html = `
                    <a class="unreplysLikes" style="cursor: pointer" data-reply-id="${replyId}" onclick="replyLikesClick(${postId}, ${commentId}, ${replyId}, ${cnt})">🤍</a>
                    <span class="likecnt" data-reply-id="${replyId}">${updateCnt}</span>
                `

            $(`.replyslikes[data-reply-id=${replyId}]`).append(temp_html);
        },
        error: function (e) {
            Swal.fire({
                icon: 'error',
                title: '좋아요 취소 실패',
                text: `${e.responseJSON.message}`
            });
        },

        timeout: 300000
    })
}

// 답글 열기버튼
function openReply(commentId) {
    $(`.commentbox[data-comment-id="${commentId}"] #replyList`).show();
    $(`.commentbox[data-comment-id="${commentId}"] .replyRead`).hide();
    $(`.commentbox[data-comment-id="${commentId}"] .replyClose`).show();
}

// 답글 닫기버튼
function closeReply(commentId) {
    $(`.commentbox[data-comment-id="${commentId}"] #replyList`).fadeOut('slow');
    $(`.commentbox[data-comment-id="${commentId}"] .replyRead`).show();
    $(`.commentbox[data-comment-id="${commentId}"] .replyClose`).hide();
}

// 답글수정 열기
function openReplyEdit(replyId) {
    const srcValue = $(`.emoji[data-reply-id="${replyId}"]`).attr("src");

    $(`.userReplyEdit-Form[data-reply-id="${replyId}"]`).show();
    $(`.replyedit[data-reply-id="${replyId}"]`).hide();
    $(`.replyeditclose[data-reply-id="${replyId}"]`).show();

    const temp_html = `
        <div class="editreplyemoji-container" data-reply-id="${replyId}">
            <div class="editreplyCommentEmoji" data-reply-id="${replyId}">
                <img src="${srcValue}" alt="emoji" data-reply-id="${replyId}">
            </div>
            <div class="editreplyCommentEmojiClose" data-reply-id="${replyId}">
                <a class="editreplyclose" data-reply-id="${replyId}" onclick="editreplyEmojiClose(${replyId})">✖️</a>
            </div>
        </div>
    `

    $(`.userReplyEdit-Form[data-reply-id=${replyId}]`).append(temp_html);
    $(`.editreplyshow[data-reply-id="${replyId}"]`).css('top', '55.3%')
    $(`.btn-replyedit[data-reply-id="${replyId}"]`).css('top', '51.7%')

    if (srcValue === undefined || srcValue === "null") {
        $(`.editreplyemoji-container[data-reply-id=${replyId}]`).remove();
        $(`.editreplyshow[data-reply-id="${replyId}"]`).css('top', '85.3%')
        $(`.btn-replyedit[data-reply-id="${replyId}"]`).css('top', '79.7%')
    }
}

// 답글수정 닫기
function closeReplyEdit(replyId) {
    $(`.userReplyEdit-Form[data-reply-id="${replyId}"]`).hide();
    $(`.replyedit[data-reply-id="${replyId}"]`).show();
    $(`.replyeditclose[data-reply-id="${replyId}"]`).hide();
    $(`.editreplyemoji-container[data-reply-id=${replyId}]`).remove();
}

// 댓글 수정버튼클릭
$(document).on('click', '.editComments', function () {
    const commentId = $(this).data('comment-id');
    const srcValue = $(`.emoji[data-comment-id="${commentId}"]`).attr("src");
    $(`.userCommentEdit-Form[data-comment-id="${commentId}"]`).show();
    $(`.editCommentsClose[data-comment-id="${commentId}"]`).show();
    $(`.replyshow[data-comment-id="${commentId}"]`).show();
    $(`.editComments[data-comment-id="${commentId}"]`).hide();

    // 답글 생성 인풋이 켜져있을시 없애줌
    $(`.userReply-Form[data-comment-id="${commentId}"]`).hide();
    $(`.replyCreateClose[data-comment-id="${commentId}"]`).hide();
    $(`.replyCreate[data-comment-id="${commentId}"]`).show();

    const temp_html = `
        <div class="editemoji-container" data-comment-id="${commentId}">
            <div class="editCommentEmoji" data-comment-id="${commentId}">
                <img class="editemoji" src="${srcValue}" alt="emoji" data-comment-id="${commentId}">
            </div>
            <div class="editCommentEmojiClose" data-comment-id="${commentId}">
                <a class="editclose" data-comment-id="${commentId}" onclick="editEmojiClose(${commentId})">✖️</a>
            </div>
        </div>
    `

    // console.log(srcValue)

    $(`.userCommentEdit-Form[data-comment-id=${commentId}]`).append(temp_html);
    $(`.editshow[data-comment-id="${commentId}"]`).css('top', '55.3%')
    $(`.userEditCommentsComplete[data-comment-id="${commentId}"]`).css('top', '51.7%')

    if (srcValue === undefined || srcValue === "null") {
        $(`.editemoji-container[data-comment-id=${commentId}]`).remove();
        $(`.editshow[data-comment-id="${commentId}"]`).css('top', '86%')
        $(`.userEditCommentsComplete[data-comment-id="${commentId}"]`).css('top', '79.7%')
    }
})

// 댓글 수정취소버튼 클릭
$(document).on('click', '.editCommentsClose', function () {
    const commentId = $(this).data('comment-id');
    $(`.userCommentEdit-Form[data-comment-id="${commentId}"]`).fadeOut('slow');
    // $(`.userCommentEdit-Form[data-comment-id="${commentId}"]`).hide();
    $(`.editCommentsClose[data-comment-id="${commentId}"]`).hide();
    $(`.editComments[data-comment-id="${commentId}"]`).show();
    $(`.editemoji-container[data-comment-id=${commentId}]`).remove();
    $(`.editshow[data-comment-id="${commentId}"]`).css('top', '86%')
    $(`.userEditCommentsComplete[data-comment-id="${commentId}"]`).css('top', '79.7%')
})

// 답글 생성버튼 클릭
$(document).on('click', '.replyCreate', function () {
    const commentId = $(this).data('comment-id');
    $(`.userReply-Form[data-comment-id="${commentId}"]`).show();
    $(`.replyCreateClose[data-comment-id="${commentId}"]`).show();
    $(`.replyCreate[data-comment-id="${commentId}"]`).hide();

    // 답글 생성버튼 클릭시 댓글 수정 인풋이 켜져있을시 꺼주는 역할
    $(`.userCommentEdit-Form[data-comment-id="${commentId}"]`).hide();
    $(`.editCommentsClose[data-comment-id="${commentId}"]`).hide();
    $(`.editComments[data-comment-id="${commentId}"]`).show();
    $(`.editemoji-container[data-comment-id=${commentId}]`).remove();
    $(`.editshow[data-comment-id="${commentId}"]`).css('top', '55.3%')
    $(`.userEditCommentsComplete[data-comment-id="${commentId}"]`).css('top', '51.7%')
})

// 답글 생성취소버튼 클릭
$(document).on('click', '.replyCreateClose', function () {
    const commentId = $(this).data('comment-id');
    $(`.userReply-Form[data-comment-id="${commentId}"]`).fadeOut('slow');
    $(`.replyCreateClose[data-comment-id="${commentId}"]`).hide();
    $(`.replyCreate[data-comment-id="${commentId}"]`).show();
    $(`.replyemoji-container[data-comment-id="${commentId}"]`).remove();
    $(`.replyshow[data-comment-id="${commentId}"]`).css('top', '86%')
    $(`.btn-replycreate[data-comment-id="${commentId}"]`).css('top', '79.7%');
})

// 이모티콘 모달창 띄우기
function show() {
    document.querySelector(".background").className = "background show";
}

// 이모티콘 모달창 닫기
function close() {
    document.querySelector(".background").className = "background";
}

// 모달창 띄우고 닫는 역할을 함
document.querySelector("#show").addEventListener("click", show);
document.querySelector("#close").addEventListener("click", close);


// 테스트용 이미지 클릭시 src 경로 가져와줌
$('.popup img').click(function () {
    const srcValue = $(this).attr("src");
    const temp_html = `
         <div id="img-wrap">
             <div>
                 <img src='${srcValue}' alt="selectimg"/>
             </div>
             <div>
                 <a>✖️</a>
             </div>
         </div>
    `
    $('#img-wrap').remove();
    $('.table-form').append(temp_html);
    close();
})

// 댓글 이모티콘 닫기버튼 클릭
$(document).on('click', '#img-wrap div a', function () {
    $('#img-wrap').remove();
    console.log("작동 확인")
})

// 댓글 수정 이모티콘 닫기버튼 클릭
function editEmojiClose(commentid) {
    $(`.editemoji-container[data-comment-id="${commentid}"]`).remove();
    $(`.editshow[data-comment-id="${commentid}"]`).css('top', '86%')
    $(`.userEditCommentsComplete[data-comment-id="${commentid}"]`).css('top', '79.7%')
}

// 댓글 수정 이모티콘 모달창 클릭
function editpopup(commentid) {
    document.querySelector(".edit-background").className = "edit-background show";

    $('.editpopup img').attr('data-comment-id', commentid);
}

// 댓글 수정 이모티콘 모달창 닫기
function editpopupclose() {
    document.querySelector(".edit-background").className = "edit-background";
}

// 댓글 수정 이모티콘 사진 선택
$('.editpopup img').click(function () {
    const srcValue = $(this).attr("src");
    const commentId = $(this).attr("data-comment-id");
    console.log(commentId)
    console.log(srcValue)
    const temp_html = `
            <div class="editemoji-container" data-comment-id="${commentId}">
                 <div class="editCommentEmoji" data-comment-id="${commentId}">
                     <img src='${srcValue}' alt="selectimg" data-comment-id="${commentId}"/>
                 </div>
                 <div class="editCommentEmojiClose" data-comment-id="${commentId}">
                     <a class="editclose" data-comment-id="${commentId}" onclick="editEmojiClose(${commentId})">✖️</a>
                 </div>
            </div>
    `
    $(`.editemoji-container[data-comment-id=${commentId}]`).remove();
    $(`.userCommentEdit-Form[data-comment-id=${commentId}]`).append(temp_html);
    $(`.editshow[data-comment-id=${commentId}]`).css('top', '55.3%')
    $(`.userEditCommentsComplete[data-comment-id="${commentId}"]`).css('top', '51.7%')
    editpopupclose()
})

// 답글 작성 이모티콘 닫기버튼 클릭
function replyEmojiClose(commentid) {
    $(`.replyemoji-container[data-comment-id="${commentid}"]`).remove();
    $(`.replyshow[data-comment-id="${commentid}"]`).css('top', '86%')
    $(`.btn-replycreate[data-comment-id="${commentid}"]`).css('top', '79.7%');
}

// 답글 작성 이모티콘 모달창 클릭
function replypopup(commentid) {
    document.querySelector(".reply-background").className = "reply-background show";

    $('.replypopup img').attr('data-comment-id', commentid);
}

// 답글 작성 이모티콘 모달창 닫기
function replypopupclose() {
    document.querySelector(".reply-background").className = "reply-background";
}

// 답글 작성 이모티콘 사진 선택
$('.replypopup img').click(function () {
    const srcValue = $(this).attr("src");
    const commentId = $(this).attr("data-comment-id");
    console.log(commentId)
    console.log(srcValue)
    const temp_html = `
            <div class="replyemoji-container" data-comment-id="${commentId}">
                 <div class="replyCommentEmoji" data-comment-id="${commentId}">
                     <img src='${srcValue}' alt="selectimg" data-comment-id="${commentId}"/>
                 </div>
                 <div class="replyCommentEmojiClose" data-comment-id="${commentId}">
                     <a class="replyclose" data-comment-id="${commentId}" onclick="replyEmojiClose(${commentId})">✖️</a>
                 </div>
            </div>
    `
    $(`.replyemoji-container[data-comment-id=${commentId}]`).remove();
    $(`.userReply-Form[data-comment-id=${commentId}]`).append(temp_html);
    $(`.replyshow[data-comment-id=${commentId}]`).css('top', '55.3%')
    $(`.btn-replycreate[data-comment-id="${commentId}"]`).css('top', '51.7%');
    replypopupclose()
})

// 답글 수정 이모티콘 닫기버튼 클릭
function editreplyEmojiClose(replyid) {
    $(`.editreplyemoji-container[data-reply-id="${replyid}"]`).remove();
    $(`.editreplyshow[data-reply-id="${replyid}"]`).css('top', '86.3%')
    $(`.btn-replyedit[data-reply-id="${replyid}"]`).css('top', '79.7%')
}

// 답글 수정 이모티콘 모달창 클릭
function editreplypopup(replyid) {
    document.querySelector(".editreply-background").className = "editreply-background show";

    $('.editreplypopup img').attr('data-reply-id', replyid);
}

// 답글 수정 이모티콘 모달창 닫기
function editreplypopupclose() {
    document.querySelector(".editreply-background").className = "editreply-background";
}

// 답글 수정 이모티콘 사진 선택
$('.editreplypopup img').click(function () {
    const srcValue = $(this).attr("src");
    const replyId = $(this).attr("data-reply-id");
    console.log(replyId)
    console.log(srcValue)
    const temp_html = `
            <div class="editreplyemoji-container" data-reply-id="${replyId}">
                 <div class="editreplyCommentEmoji" data-reply-id="${replyId}">
                     <img src='${srcValue}' alt="selectimg" data-reply-id="${replyId}"/>
                 </div>
                 <div class="editreplyCommentEmojiClose" data-reply-id="${replyId}">
                     <a class="editreplyclose" data-reply-id="${replyId}" onclick="editreplyEmojiClose(${replyId})">✖️</a>
                 </div>
            </div>
    `
    $(`.editreplyemoji-container[data-reply-id=${replyId}]`).remove();
    $(`.userReplyEdit-Form[data-reply-id=${replyId}]`).append(temp_html);
    $(`.editreplyshow[data-reply-id=${replyId}]`).css('top', '55.3%')
    $(`.btn-replyedit[data-reply-id="${replyId}"]`).css('top', '51.7%')
    editreplypopupclose()
})