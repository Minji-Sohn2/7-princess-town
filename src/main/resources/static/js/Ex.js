
$(document).ready(function() {

    // 페이지 로드 시 로그인 상태 확인
    updateLoginStatus();

    function updateLoginStatus() {
        var token = Cookies.get('Authorization');
        if (!token) {
            $('.item:contains("로그아웃")').hide();
            $('.item:contains("로그인")').show();
            $('.item:contains("회원가입")').show();
        } else {
            var username = Cookies.get('Username');
            if (username) {
                $('#login-btn').replaceWith('<li class="welcome-msg">' + username + '님 환영합니다.</li>');
                $('.item:contains("회원가입")').hide();
                $('.item:contains("로그아웃")').show();
            }
        }
    }

    // 모달 초기화
    const $signupModal = $('#signupModal').modal();
    const $loginModal = $('#loginModal').modal();
    const $passwordResetModal = $('#passwordResetModal').modal();

    // 페이지 로드 시 '저장' 버튼 숨기기
    $('#saveProfile').hide();

    // 로그인 모달 표시
    $('.item:contains("로그인")').on('click', function (event) {
        event.preventDefault();
        event.stopPropagation(); // 이벤트 버블링 중지
        $signupModal.modal('hide');
        $loginModal.modal('show');
    });

    // 회원가입 모달 표시
    $('.item:contains("회원가입")').on('click', function (event) {
        event.preventDefault();
        event.stopPropagation(); // 이벤트 버블링 중지
        $loginModal.modal('hide');
        $signupModal.modal('show');
    });

    // 비밀번호 재설정 모달 표시
    $('.item:contains("비밀번호 찾기")').on('click', function (event) {
        console.log("비밀번호 찾기 클릭 이벤트 시작");
        event.preventDefault();
        event.stopPropagation(); // 이벤트 버블링 중지
        $passwordResetModal.modal('show');
        console.log("비밀번호 찾기 클릭 이벤트 종료");
    });

    // 프로필 버튼 클릭 시 모달 표시
    $('#profile-btn').on('click', function () {
        $('#profileModal').modal('show');
        $signupModal.modal('hide');
        $loginModal.modal('hide');
        $passwordResetModal.modal('hide');

        var token = Cookies.get('Authorization');

        // 프로필 조회 요청
        $.ajax({
            url: "/api/auth/profile",
            type: "GET",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", token);
            },
            success: function (response) {
                // 응답 데이터를 이용하여 프로필 정보 필드에 채움
                $("input[name='username']").val(response.username);
                $("input[name='nickname']").val(response.nickname);
                $("input[name='email']").val(response.email);
                $("input[name='phoneNumber']").val(response.phoneNumber);
                // 기타 필드 채움

                // 비밀번호 필드 수정하여 앞 4글자만 표시
                var passwordField = $("input[name='password']");
                passwordField.attr('type', 'text');
                passwordField.val(response.password.substring(0, 4) + "****");

                // 위치 정보 필드 처리
                currentLatitude = response.latitude;
                currentLongitude = response.longitude;
                // 기타 위치 정보 처리
            },
            error: function (error) {
                alert("프로필 조회 실패. 다시 시도해주세요.");
            }
        });
    });


    // 인증번호 전송 버튼 클릭 이벤트
    $('#sendVerificationCode').on('click', function (event) {
        event.preventDefault();

        var phoneNumber = $('#phoneNumber').val();
        console.log("phoneNumber : " + phoneNumber)
        if (!phoneNumber) { // 전화번호가 입력되지 않았을 때
            alert("전화번호를 입력해주세요.");
            return;
        } else {
            $.ajax({
                url: "/auth/send-phone-verification-code",
                contentType: 'application/json',
                type: "POST",
                data: {phoneNumber: phoneNumber},
                success: function (response) {
                    alert("인증번호가 전송되었습니다.");
                },
                error: function (error) {
                    alert("인증번호 전송 실패. 다시 시도해주세요.");
                }
            });
        }
    });

    // 인증번호 확인 버튼 클릭 이벤트
    $('#verifyCodeButton').on('click', function (event) {
        event.preventDefault();

        var phoneNumber = $('#phoneNumber').val();
        var inputCode = $('#phoneVerifyCode').val();
        if (!phoneNumber || !inputCode) { // 전화번호나 인증 코드가 입력되지 않았을 때
            alert("전화번호와 인증코드를 모두 입력해주세요.");
            return;
        }

        $.ajax({
            url: "/auth/verify-phone-code",
            type: "POST",
            data: {phoneNumber: phoneNumber, inputCode: inputCode},
            success: function (response) {
                if (response.status === 200) {
                    alert("인증 성공");
                } else {
                    alert("존재하지 않는 인증코드입니다. 다시 입력해주세요");
                }
            },
            error: function (error) {
                alert("인증 실패. 다시 시도해주세요.");
            }
        });
    });

    // 회원가입 버튼 클릭 이벤트
    $('#signupModal button[type="submit"]').on('click', function (event) {
        event.preventDefault();

        var username = $('#username').val();
        var password = $('#password').val();
        var nickname = $('#nickname').val();
        var email = $('#email').val();
        var phoneNumber = $('#phoneNumber').val();
        var inputCode = $('#phoneVerifyCode').val();

        if (!username || !password || !nickname || !email || !phoneNumber || !inputCode) {
            alert("모든 필드를 입력해주세요.");
            return;
        }

        var formData = new FormData();
        var profileImage = $('input[name="profileImage"]')[0];
        if (profileImage && profileImage.files.length > 0) {
            formData.append('profileImage', profileImage.files[0]);
        }
        formData.append('username', username);
        formData.append('password', password);
        formData.append('nickname', nickname);
        formData.append('email', email);
        formData.append('phoneNumber', phoneNumber);

        $.ajax({
            url: "/auth/signup",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                alert("성공적으로 회원가입이 되었습니다!");
                $('.item:contains("회원가입")').hide();
                window.location.href = '/';
            },
            error: function (error) {
                alert("회원가입 실패. 다시 확인해주세요.");
            }
        });
    });

    // 파일 선택 input 엘리먼트 가져오기
    const fileInput = document.querySelector('input[name="profileImage"]');
    const selectedImage = document.getElementById('selectedImage');

    if (fileInput) {
        fileInput.addEventListener('change', function () {
            const selectedFile = fileInput.files[0];
            if (selectedFile && selectedImage) {
                selectedImage.src = URL.createObjectURL(selectedFile);
                selectedImage.style.display = 'block';
            }
        });
    }

    // 로그인 버튼 클릭 이벤트
    $('#loginModal button[type="submit"]').on('click', function (event) {
        event.preventDefault();

        // 사용자가 입력한 아이디와 비밀번호 가져오기
        var username = $('input[name="login-username"]').val();
        var password = $('input[name="login-password"]').val();

        $.ajax({
            url: "/auth/login",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({username: username, password: password}),
            success: function (res, status, xhr) {
                console.log("status : " + status)

                // HTTP 헤더에서 토큰 가져오기
                var token = xhr.getResponseHeader("Authorization");
                console.log("token : " + token)

                // 서버 응답에서 userId 꺼내오기
                console.log("userId : " + res.data)
                var userId = res.data;

                // 쿠키 만료일 설정
                var expirationDate = new Date();
                expirationDate.setDate(expirationDate.getDate() + 1);

                // 토큰을 쿠키에 저장
                Cookies.set('Authorization', token, {expires: expirationDate});
                Cookies.set('Username', username, {expires: expirationDate});
                Cookies.set('userId', userId, {expires: expirationDate})

                $loginModal.modal('hide');
                $signupModal.modal('hide');

                alert("성공적으로 로그인 했습니다!");

                // 로그인 상태 UI 업데이트
                $('#login-btn').replaceWith('<li class="welcome-msg">' + username + '님 환영합니다.</li>');

                // window.location.href = '/';
            },
            error: function (error) {
                alert("로그인 실패. 다시 시도해주세요.");
            }
        });

        // 이벤트 전파 중지
        return false;
    });

    // 로그아웃 버튼 클릭 이벤트
    $(document).on('click', '#logout-btn', function (event) {
        event.preventDefault();

        console.log("logout start")

        var token = Cookies.get('Authorization');

        console.log("token : " + token)
        $.ajax({
            url: "/auth/logout",
            type: "POST",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", token);
            },
            success: function (response) {

                // console.log(`response : ${response}`)

                if (response.status === 200) {
                    alert(response.message);
                    Cookies.remove('Authorization');
                    Cookies.remove('Username');
                    updateLoginStatus();
                    window.location.href = "/"
                } else {
                    alert("로그아웃 실패: " + response.message);
                }
            },
            error: function (error) {
                console.log("end logout")
                alert("로그아웃 요청 실패: " + error.statusText);
            }
        });
    });


    // 1. 인증 코드 발송
    $('#reset-sendVerificationCode').on('click', function (event) {
        event.preventDefault();

        var phoneNumber = $('#reset-phoneNumber').val();

        if (!phoneNumber.trim()) { // 전화번호가 입력되지 않았을 때
            alert("전화번호를 입력해주세요.");
            return;
        }

        $.ajax({
            url: "/code/send-phone-verification-code",
            type: "POST",
            data: {phoneNumber: phoneNumber},
            success: function (response) {
                alert("인증번호가 전송되었습니다.");
            },
            error: function (error) {
                alert("인증번호 전송 실패. 다시 시도해주세요.");
            }
        });
    });

    // 2. 전화번호 인증
    $('#verifyPhoneNumber').on('click', function (event) {
        event.preventDefault();

        var phoneNumber = $('#reset-phoneNumber').val();
        var inputCode = $('#inputCode').val();

        if (!phoneNumber.trim() || !inputCode.trim()) { // 전화번호나 인증 코드가 입력되지 않았을 때
            alert("전화번호와 인증코드를 모두 입력해주세요.");
            return;
        }
        $.ajax({
            url: "/code/verify-phone",
            type: "POST",
            data: {phoneNumber: phoneNumber, inputCode: inputCode},
            success: function (response) {
                if (response.status === 200) {
                    alert("인증 성공");
                } else {
                    alert("존재하지 않는 인증코드입니다. 다시 입력해주세요");
                }
            },
            error: function (error) {
                alert("인증 실패. 다시 시도해주세요.");
            }
        });
    });

    // 3. 임시 비밀번호 전송
    $('#sendTempPassword').on('click', function (event) {
        var phoneNumber = $('#reset-phoneNumber').val();
        var email = $('#reset-email').val();
        $.ajax({
            url: "/code/send-temp-password",
            type: "POST",
            data: {phoneNumber: phoneNumber, email: email},
            success: function (response) {
                alert("임시 비밀번호가 전송되었습니다.");
            },
            error: function (error) {
                alert("임시 비밀번호 전송 실패. 다시 시도해주세요.");
            }
        });
    });

    // 4. 임시 비밀번호로 로그인
    $('#tempLoginButton').on('click', function (event) {
        event.preventDefault();

        // 임시 로그인 폼을 표시
        $('#tempLoginModal').modal('show');
    });

    $('#tempLoginModal form').on('submit', function (event) {
        event.preventDefault();

        // 폼에서 입력한 아이디와 임시 비밀번호 가져오기
        var username = $('input[name="temp-login-username"]').val();
        var tempPassword = $('input[name="temp-login-password"]').val();
        console.log("username : " + username)
        console.log("tempPassword : " + tempPassword)

        $.ajax({
            url: "/code/login-with-temp-password",
            type: "POST",
            data: {username: username, tempPassword: tempPassword},
            success: function (res, status, xhr) {
                console.log("status : " + status)

                // HTTP 헤더에서 토큰 가져오기
                var token = xhr.getResponseHeader("Authorization");
                console.log("token : " + token)

                // 서버 응답에서 userId 꺼내오기
                console.log("userId : " + res.data)
                var userId = res.data;

                // 쿠키 만료일 설정
                var expirationDate = new Date();
                expirationDate.setDate(expirationDate.getDate() + 1);

                // 토큰을 쿠키에 저장
                Cookies.set('Authorization', token, {expires: expirationDate});
                Cookies.set('Username', username, {expires: expirationDate});
                Cookies.set('userId', userId, {expires: expirationDate})

                $loginModal.modal('hide');
                $signupModal.modal('hide');

                // 로그인 상태 UI 업데이트
                $('#login-btn').replaceWith('<li class="welcome-msg">' + username + '님 환영합니다.</li>');

                alert("성공적으로 로그인 했습니다!");

                // window.location.href = '/';
            },
            error: function (error) {
                alert("로그인 실패. 다시 시도해주세요.");
            }
        });
    });

// 반경 설정 드롭다운 초기화 및 직접 입력 선택 시 입력 필드 보이기
    $('#radiusSelect').dropdown({
        onChange: function (value) {
            if (value === "custom") {
                $('#customRadiusInput').show();
            } else {
                $('#customRadiusInput').hide();
            }
        }
    });

// 위치 설정 버튼 클릭 시 현재 위치 정보만 가져옴
    var currentLatitude, currentLongitude;

    $('#setLocation').click(function () {
        if ("geolocation" in navigator) {
            navigator.geolocation.getCurrentPosition(function (position) {
                currentLatitude = position.coords.latitude;
                currentLongitude = position.coords.longitude;

                alert("위치 설정이 완료되었습니다!");

            }, function (error) {
                alert(`ERROR(${error.code}): ${error.message}`);
            });
        } else {
            alert("브라우저가 위치 정보를 지원하지 않습니다.");
        }
    });

    // 프로필 정보 모달 열기
    $('#profileModal').modal('attach events', '#editProfile', 'show');

    // 비밀번호 보기 버튼 클릭 이벤트
    $('#showPassword').click(function () {
        var passwordField = $("input[name='password']");
        if (passwordField.attr('type') === 'password') {
            passwordField.attr('type', 'text');
            $('#showPassword').text('비밀번호 숨기기');
        } else {
            passwordField.attr('type', 'password');
            $('#showPassword').text('비밀번호 보기');
        }
    });

    // 프로필 정보 수정 버튼 클릭 이벤트
    $('#editProfile').click(function () {
        // 수정 가능한 필드들의 readonly 속성 제거
        $('#profileModal input').prop('readonly', false);
        $('#customRadiusInput').prop('readonly', false);
        $('#radiusSelect').prop('disabled', false);
        $('#setLocation').prop('disabled', false);

        // 수정 완료 버튼 표시, 수정 버튼 숨김
        $('#saveProfile').show();
        $('#editProfile').hide();
    });


    // 프로필 정보 저장 버튼 클릭 이벤트
    $('#saveProfile').click(function () {
        var formData = new FormData();
        var profileImage = $('input[name="profileImage"]')[0];
        if (profileImage && profileImage.files.length > 0) {
            formData.append('profileImage', profileImage.files[0]);
        }
        formData.append('username', $("input[name='username']").val());
        formData.append('password', $("input[name='password']").val());
        formData.append('nickname', $("input[name='nickname']").val());
        formData.append('email', $("input[name='email']").val());
        formData.append('phoneNumber', $("input[name='phoneNumber']").val());
        formData.append('latitude', currentLatitude); // 위도 추가
        formData.append('longitude', currentLongitude); // 경도 추가

        var token = decodeURIComponent(Cookies.get('Authorization'));

        $.ajax({
            url: `/api/auth/profile`,
            type: 'PUT',
            data: formData, // FormData 객체 전송
            processData: false, // 데이터를 처리하지 않도록 설정
            contentType: false, // 컨텐츠 타입을 설정하지 않도록 설정
            beforeSend: function (xhr) {
                // 토큰을 Authorization 헤더에 설정
                xhr.setRequestHeader('Authorization', token);
            },
            success: function (response) {

                // 사용자 이름 변경 후
                var newUsername = $("input[name='username']").val();

                // 기존 사용자 이름 쿠키 삭제
                Cookies.remove('Username');

                // 새로운 사용자 이름 쿠키 저장
                Cookies.set('Username', newUsername);

                // 수정 가능한 필드들의 readonly 속성 추가
                $('#profileModal input').prop('readonly', true);
                $('#customRadiusInput').prop('readonly', true);
                $('#radiusSelect').prop('disabled', true);
                $('#setLocation').prop('disabled', true);

                // 수정 완료 버튼 숨김, 수정 버튼 표시
                alert("프로필 정보가 성공적으로 업데이트되었습니다.");
                $('#editProfile').show();
                $('#saveProfile').hide();
            },
            error: function (error) {
                alert("프로필 정보 업데이트 중 오류가 발생했습니다.");
            }
        });
    });
});




// // 게시글 조회
// $('#viewPosts').click(function() {
//     const radius = $('#radiusSelect').val() === "custom" ? $('#customRadiusInput').val() : $('#radiusSelect').val();
//
//     const requestData = {
//         latitude: currentLatitude,  // 위도 사용
//         longitude: currentLongitude, // 경도 사용
//         radius: radius
//     };
//
//     // 쿠키에서 userId 꺼내오기
//     var userId = Cookies.get('userId');
//
//     // GET 요청으로 게시글 조회
//     $.ajax({
//         url: `/auth/location/posts/${userId}`,
//         type: 'GET',
//         data: requestData,
//         success: function(response) {
//             // TODO: 받아온 게시글 정보 처리
//         },
//         error: function(error) {
//             alert("게시글 조회 중 오류가 발생했습니다.");
//         }
//     });
// });

// // 카카오 정보 가져오기
// const KAKAO_CLIENT_ID = "09f2acecd9cd8bf7b7d3f6951daf4548";
// const KAKAO_REDIRECT_URL = "http://localhost:8080/api/user/kakao/callback";
//
// // 카카오 요청 URL 만들기
// const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_CLIENT_ID}&redirect_uri=${KAKAO_REDIRECT_URL}&response_type=code`;
//
// // 카카오 로그인 버튼 클릭 이벤트
// document.getElementById("kakaoLogin").addEventListener("click", function(e) {
//     e.preventDefault();
//     window.location.href = KAKAO_AUTH_URL;
//
//         // 토큰을 쿠키에 저장하거나 다른 필요한 작업을 수행
//         alert("성공적으로 로그인 했습니다!");
//
//         // 모달 숨기기
//         $('#loginModal').modal('hide');
//
//         // 로그인 상태 UI 업데이트
//         $('#login-btn').replaceWith('<li class="welcome-msg">' + username + '님 환영합니다.</li>');
// });


// // TODO: 카카오 로그인 구현
//
// // 카카오 정보 가져오기
// const KAKAO_CLIENT_ID = "09f2acecd9cd8bf7b7d3f6951daf4548";
// const KAKAO_REDIRECT_URL = "http://localhost:8080/api/user/kakao/callback";
//
// // 카카오 요청 URL 만들기
// const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_CLIENT_ID}&redirect_uri=${KAKAO_REDIRECT_URL}&response_type=code`;
//
// // 카카오 로그인 버튼 클릭 이벤트
// document.getElementById("kakaoLogin").addEventListener("click", function(e) {
//
//     e.preventDefault();
//     window.location.href = KAKAO_AUTH_URL;
//
//     window.location.href = "/";
//
//     // 로그인 모달 숨기기
//     $('#loginModal').modal('hide');
//
//     // 로그인 상태 UI 업데이트
//     $('#login-btn').replaceWith('<li class="welcome-msg">' + username + '님 환영합니다.</li>');
//
//     alert("성공적으로 로그인 했습니다!");
//
// });



//         if (code) {
//                     console.log("code : " + code)
//                     console.log("token : " + res.data.jwtToken)
//                     // // 헤더에서 토큰 가져오기
//                     // var token = xhr.getResponseHeader("Authorization");
//                     //
//                     // // 쿠키 만료일 설정
//                     // var expirationDate = new Date();
//                     // expirationDate.setDate(expirationDate.getDate() + 1);
//                     //
//                     // // 토큰을 쿠키에 저장
//                     // Cookies.set('Authorization', token, {expires: expirationDate});
//                     // Cookies.set('Username', res.data.kakaoUser.username, {expires: expirationDate});
//                     //
//                     // window.location.href = "/";  // 로그인 성공 후 이동할 페이지
//
//                     alert("성공적으로 로그인 했습니다!");
//
//                     $('#loginModal').modal('hide'); // 로그인 모달 숨기기
//
//                     // 로그인 상태 UI 업데이트
//                     $('#login-btn').replaceWith('<li class="welcome-msg">' + username + '님 환영합니다.</li>');
//
//                 },
//                 error: function (error) {
//                     console.error("카카오 로그인 에러:", error);
//                 }
//             });
//         }
// }


//     // 네이버 로그인 버튼 클릭 이벤트
//     document.getElementById("naverLogin").addEventListener("click", function(e) {
//         // 네이버 정보 가져오기
//         const NAVER_CLIENT_ID = "1y4x9PZB2SmdqeC02T5g";
//         const NAVER_REDIRECT_URL = "http://localhost:8080/api/user/naver/callback";
//
//         // 네이버 요청 URL 만들기
//         const NAVER_AUTH_URL = `https://nid.naver.com/oauth2.0/authorize?client_id=${NAVER_CLIENT_ID}&redirect_uri=${NAVER_REDIRECT_URL}&response_type=code`;
//         e.preventDefault();
//         window.location.href = NAVER_AUTH_URL;
//         window.location.href = "/"
//     });
//
// // 로그인 후 Redirect URL로 돌아왔을 때의 처리
//     if (window.location.pathname === "/api/user/naver/callback") {
//         const code = new URL(window.location.href).searchParams.get("code");
//
//         if (code) {
//             $.ajax({
//                 url: NAVER_REDIRECT_URL,
//                 method: "GET",
//                 data: {code: code},
//                 success: function(res, xhr) {
//                     // 헤더에서 토큰 가져오기
//                     var token = xhr.getResponseHeader("Authorization");
//
//                     // 쿠키 만료일 설정
//                     var expirationDate = new Date();
//                     expirationDate.setDate(expirationDate.getDate() + 1);
//
//                     // 토큰을 쿠키에 저장
//                     Cookies.set('Authorization', token,  {expires: expirationDate});
//                     Cookies.set('Username', res.data.naverUser.username,  {expires: expirationDate});
//
//                     window.location.href = "/";  // 로그인 성공 후 이동할 페이지
//
//                     alert("성공적으로 로그인 했습니다!");
//
//                     $('#loginModal').modal('hide'); // 로그인 모달 숨기기
//
//                     // 로그인 상태 UI 업데이트
//                     var username = Cookies.get('Username');
//                     $('#login-btn').replaceWith('<li class="welcome-msg">' + username + '님 환영합니다.</li>');
//
//                 },
//                 error: function(error) {
//                     console.error("네이버 로그인 에러:", error);
//                 }
//             });
//         }
//     }

// 만료시간 화면에 표시
// function updateExpiryTimeDisplay(decodedToken) {
//     const currentTime = Date.now() / 1000;
//     const remainingTime = decodedToken.exp - currentTime;
//
//     // 초 단위로 남은 시간 계산
//     const hours = Math.floor(remainingTime / 3600);
//     const minutes = Math.floor((remainingTime % 3600) / 60);
//     const seconds = Math.floor(remainingTime % 60);
//
//     // 시간, 분, 초를 포맷에 맞게 변환
//     const formattedTime = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
//     document.getElementById('expiry-time').textContent = formattedTime;
// }

// 1초마다 토큰의 만료 시간을 체크
// setInterval(() => {
//     const token = Cookies.get('Authorization');
//
//     // 토큰이 없으면 로직을 실행하지 않음
//     if (!token) {
//         return;
//     }
//
//     if (isTokenExpired(token)) {
//         // alert("세션이 만료되었습니다. 다시 로그인 해주세요.");
//         // Cookies.remove('Authorization');
//         // window.location.href = '/';
//     } else {
//         const decodedToken = jwt_decode(token);
//         updateExpiryTimeDisplay(decodedToken);
//     }
// }, 1000);  // 1초마다 체크
//
// // 토큰의 만료 시간을 체크하는 함수
// function isTokenExpired(token) {
//     try {
//         // JWT 토큰을 디코드
//         const decodedToken = jwt_decode(token);
//         console.log("decodedToken : " + decodedToken);
//         const currentTime = Date.now() / 1000;
//
//         // 만료 시간을 비교
//         if (decodedToken.exp < currentTime) {
//             return true;
//         }
//         return false;
//     } catch (e) {
//         console.error("Error decoding token:", e);
//         return true;
//     }
// }

