$(document).ready(function() {

    // 모달 초기화
    const $signupModal = $('#signupModal').modal();
    const $loginModal = $('#loginModal').modal();
    const $passwordResetModal = $('#passwordResetModal').modal();

    // 페이지 로드 시 로그인 상태 확인
    updateLoginStatus();

    // 인증번호 전송 버튼 클릭 이벤트
    $('#sendVerificationCode').on('click', function(event) {
        event.preventDefault();

        var phoneNumber = $('input[name="phoneNumber"]').val();
        if (!phoneNumber.trim()) { // 전화번호가 입력되지 않았을 때
            alert("전화번호를 입력해주세요.");
            return;
        }

        $.ajax({
            url: "/auth/send-phone-verification-code",
            type: "POST",
            data: { phoneNumber: phoneNumber },
            success: function(response) {
                alert("인증번호가 전송되었습니다.");
            },
            error: function(error) {
                alert("인증번호 전송 실패. 다시 시도해주세요.");
            }
        });
    });

    // 인증번호 확인 버튼 클릭 이벤트
    $('#verifyCodeButton').on('click', function(event) {
        event.preventDefault();

        var phoneNumber = $('input[name="phoneNumber"]').val();
        var inputCode = $('input[name="phoneVerifyCode"]').val();
        if (!phoneNumber.trim() || !inputCode.trim()) { // 전화번호나 인증 코드가 입력되지 않았을 때
            alert("전화번호와 인증코드를 모두 입력해주세요.");
            return;
        }

        $.ajax({
            url: "/auth/verify-phone-code",
            type: "POST",
            data: { phoneNumber: phoneNumber, inputCode: inputCode },
            success: function(response) {
                if (response.status === 200) {
                    alert("인증 성공");
                } else {
                    alert("존재하지 않는 인증코드입니다. 다시 입력해주세요");
                }
            },
            error: function(error) {
                alert("인증 실패. 다시 시도해주세요.");
            }
        });
    });

    // 회원가입 버튼 클릭 이벤트
    $('#signupModal button[type="submit"]').on('click', function(event) {
        event.preventDefault();

        var username = $('input[name="username"]').val();
        var password = $('input[name="password"]').val();
        var nickname = $('input[name="nickname"]').val();
        var email = $('input[name="email"]').val();
        var phoneNumber = $('input[name="phoneNumber"]').val();

        if (!username || !password || !nickname || !email || !phoneNumber) {
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
            success: function(response) {
                alert("성공적으로 회원가입이 되었습니다!");
                window.location.href = '/';
            },
            error: function(error) {
                alert("회원가입 실패. 다시 확인해주세요.");
            }
        });
    });


    // 파일 선택 input 엘리먼트 가져오기
    const fileInput = document.querySelector('input[name="profileImage"]');
    const selectedImage = document.getElementById('selectedImage');

    if(fileInput) {
        fileInput.addEventListener('change', function() {
            const selectedFile = fileInput.files[0];
            if (selectedFile && selectedImage) {
                selectedImage.src = URL.createObjectURL(selectedFile);
                selectedImage.style.display = 'block';
            }
        });
    }

    // 로그인 버튼 클릭 이벤트:
    $('#loginModal button[type="submit"]').on('click', function(event) {
        event.preventDefault();

        // 사용자가 입력한 아이디와 비밀번호 가져오기
        var username = $('input[name="login-username"]').val();
        var password = $('input[name="login-password"]').val();

        $.ajax({
            url: "/auth/login",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({ username: username, password: password }),
            success: function(response, status, xhr) {
                // HTTP 헤더에서 토큰 가져오기
                var token = xhr.getResponseHeader("Authorization");

                // 서버 응답에서 userId 꺼내오기
                var userId = response.data;

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

                // 메인페이지 이동
                window.location.href="/"

                // 로그인 상태 UI 업데이트
                $('#login-btn').replaceWith('<li class="welcome-msg">' + username + '님 환영합니다.</li>');
            },
            error: function(error) {
                alert("로그인 실패. 다시 시도해주세요.");
            }
        });
    });

    // 카카오 정보 가져오기
    const KAKAO_CLIENT_ID = "09f2acecd9cd8bf7b7d3f6951daf4548";
    const KAKAO_REDIRECT_URL = "http://localhost:8080/api/user/kakao/callback";

    // 카카오 요청 URL 만들기
    const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_CLIENT_ID}&redirect_uri=${KAKAO_REDIRECT_URL}&response_type=code`;

    // 카카오 로그인 버튼 클릭 이벤트
    document.getElementById("kakaoLogin").addEventListener("click", function(e) {

        e.preventDefault();
        window.location.href = KAKAO_AUTH_URL;
    });

    // 로그인 후 Redirect URL로 돌아왔을 때의 처리
    // if (window.location.pathname === "/api/user/kakao/callback") {
    console.log("Current path: " + window.location.pathname)
    const code = new URL(window.location.href).searchParams.get("code");
    console.log("code : " + code)
    if (code == null) {
        return;
    } else {
        $.ajax({
            url: KAKAO_REDIRECT_URL,
            method: "GET",
            data: {code: code},
            success: function(res, xhr) {
                // 헤더에서 토큰 가져오기
                var token = xhr.getResponseHeader("Authorization");

                // 쿠키 만료일 설정
                var expirationDate = new Date();
                expirationDate.setDate(expirationDate.getDate() + 1);

                // 토큰을 쿠키에 저장
                Cookies.set('Authorization', token,  {expires: expirationDate});
                Cookies.set('Username', res.data.kakaoUser.username,  {expires: expirationDate});

                window.location.href = "/";  // 로그인 성공 후 이동할 페이지

                alert("성공적으로 로그인 했습니다!");

                $('#loginModal').modal('hide'); // 로그인 모달 숨기기

                // 로그인 상태 UI 업데이트
                $('#login-btn').replaceWith('<li class="welcome-msg">' + username + '님 환영합니다.</li>');

            },
            error: function(error) {
                console.error("카카오 로그인 에러:", error);
            }
        });
    }
    // }

    // 네이버 로그인 버튼 클릭 이벤트
    document.getElementById("naverLogin").addEventListener("click", function(e) {
        // 네이버 정보 가져오기
        const NAVER_CLIENT_ID = "1y4x9PZB2SmdqeC02T5g";
        const NAVER_REDIRECT_URL = "http://localhost:8080/api/user/naver/callback";

        // 네이버 요청 URL 만들기
        const NAVER_AUTH_URL = `https://nid.naver.com/oauth2.0/authorize?client_id=${NAVER_CLIENT_ID}&redirect_uri=${NAVER_REDIRECT_URL}&response_type=code`;
        e.preventDefault();
        window.location.href = NAVER_AUTH_URL;
        window.location.href = "/"
    });

// 로그인 후 Redirect URL로 돌아왔을 때의 처리
    if (window.location.pathname === "/api/user/naver/callback") {
        const code = new URL(window.location.href).searchParams.get("code");

        if (code) {
            $.ajax({
                url: NAVER_REDIRECT_URL,
                method: "GET",
                data: {code: code},
                success: function(res, xhr) {
                    // 헤더에서 토큰 가져오기
                    var token = xhr.getResponseHeader("Authorization");

                    // 쿠키 만료일 설정
                    var expirationDate = new Date();
                    expirationDate.setDate(expirationDate.getDate() + 1);

                    // 토큰을 쿠키에 저장
                    Cookies.set('Authorization', token,  {expires: expirationDate});
                    Cookies.set('Username', res.data.naverUser.username,  {expires: expirationDate});

                    window.location.href = "/";  // 로그인 성공 후 이동할 페이지

                    alert("성공적으로 로그인 했습니다!");

                    $('#loginModal').modal('hide'); // 로그인 모달 숨기기

                    // 로그인 상태 UI 업데이트
                    var username = Cookies.get('Username');
                    $('#login-btn').replaceWith('<li class="welcome-msg">' + username + '님 환영합니다.</li>');

                },
                error: function(error) {
                    console.error("네이버 로그인 에러:", error);
                }
            });
        }
    }


    // 로그아웃 버튼 클릭 이벤트
    $('.item:contains("로그아웃")').on('click', function(event) {
        event.preventDefault();

        var token = Cookies.get('Authorization');
        $.ajax({
            url: "/auth/logout",
            type: "POST",
            beforeSend: function(xhr) {
                xhr.setRequestHeader("Authorization", token);
            },
            success: function(response) {
                if (response.status === 200) {
                    alert(response.message);
                    Cookies.remove('Authorization');
                    Cookies.remove('Username');
                    updateLoginStatus();
                } else {
                    alert("로그아웃 실패: " + response.message);
                }
            },
            error: function(error) {
                alert("로그아웃 요청 실패: " + error.statusText);
            }
        });
    });

    // 1. 인증 코드 발송
    $('#reset-sendVerificationCode').on('click', function(event) {
        event.preventDefault();

        var phoneNumber = $('#reset-phoneNumber').val();

        if (!phoneNumber.trim()) { // 전화번호가 입력되지 않았을 때
            alert("전화번호를 입력해주세요.");
            return;
        }

        $.ajax({
            url: "/code/send-phone-verification-code",
            type: "POST",
            data: { phoneNumber: phoneNumber },
            success: function(response) {
                alert("인증번호가 전송되었습니다.");
            },
            error: function(error) {
                alert("인증번호 전송 실패. 다시 시도해주세요.");
            }
        });
    });

    // 2. 전화번호 인증
    $('#verifyPhoneNumber').on('click', function(event) {
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
            data: { phoneNumber: phoneNumber, inputCode: inputCode },
            success: function(response) {
                if (response.status === 200) {
                    alert("인증 성공");
                } else {
                    alert("존재하지 않는 인증코드입니다. 다시 입력해주세요");
                }
            },
            error: function(error) {
                alert("인증 실패. 다시 시도해주세요.");
            }
        });
    });

    // 3. 임시 비밀번호 전송
    $('#sendTempPassword').on('click', function(event) {
        var phoneNumber = $('#reset-phoneNumber').val();
        var email = $('#reset-email').val();
        $.ajax({
            url: "/code/send-temp-password",
            type: "POST",
            data: { phoneNumber: phoneNumber, email: email },
            success: function(response) {
                alert("임시 비밀번호가 전송되었습니다.");
            },
            error: function(error) {
                alert("임시 비밀번호 전송 실패. 다시 시도해주세요.");
            }
        });
    });

    // 4. 임시 비밀번호로 로그인
    $('#tempLoginButton').on('click', function(event) {
        event.preventDefault();
        var username = $('#username').val(); // TODO: 필요에 따라 올바른 입력 필드의 ID 또는 name으로 변경해야 합니다.
        var tempPassword = $('#tempPassword').val(); // TODO: 필요에 따라 올바른 입력 필드의 ID 또는 name으로 변경해야 합니다.
        $.ajax({
            url: "/code/login-with-temp-password",
            type: "POST",
            data: { username: username, tempPassword: tempPassword },
            success: function(response) {
                alert("로그인 성공");
                window.location.href = '/dashboard'; // 대시보드 페이지 또는 원하는 페이지로 리다이렉트
            },
            error: function(error) {
                alert("로그인 실패. 다시 시도해주세요.");
            }
        });
    });

    // 반경 설정 드롭다운 초기화 및 직접 입력 선택 시 입력 필드 보이기
    $('#radiusSelect').dropdown({
        onChange: function(value) {
            if (value === "custom") {
                $('#customRadiusInput').show();
            } else {
                $('#customRadiusInput').hide();
            }
        }
    });

    // 수정 버튼 클릭 시 모든 입력 필드를 수정 가능하게 설정
    $('#editProfile').click(function() {
        $('#profileModal input').prop('readonly', false);
    });
// 위치 설정 버튼 클릭 시 현재 위치 정보만 가져옴
    var currentLatitude, currentLongitude;

    $('#setLocation').click(function() {
        if ("geolocation" in navigator) {
            navigator.geolocation.getCurrentPosition(function(position) {
                currentLatitude = position.coords.latitude;
                currentLongitude = position.coords.longitude;

                alert("위치 설정이 완료되었습니다!");

            }, function(error) {
                alert(`ERROR(${error.code}): ${error.message}`);
            });
        } else {
            alert("브라우저가 위치 정보를 지원하지 않습니다.");
        }
    });

    // 프로필 정보 수정 처리
    $('#updateProfile').click(function() {
        const requestData = {
            username: $("input[name='username']").val(),
            password: $("input[name='password']").val(),
            nickname: $("input[name='nickname']").val(),
            email: $("input[name='email']").val(),
            phoneNumber: $("input[name='phoneNumber']").val(),
            latitude: currentLatitude,  // 위도 추가
            longitude: currentLongitude // 경도 추가
        };

        // PUT 요청으로 프로필 정보 업데이트
        $.ajax({
            url: `/api/auth/profile`,
            type: 'PUT',
            data: requestData,
            success: function(response) {
                // 프로필 정보 업데이트 성공 시 처리
                alert("프로필 정보가 성공적으로 업데이트되었습니다.");
            },
            error: function(error) {
                alert("프로필 정보 업데이트 중 오류가 발생했습니다.");
            }
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

    function updateLoginStatus() {
        var token = Cookies.get('Authorization');
        if (!token) {
            $('.item:contains("로그아웃")').hide();
            $('.item:contains("로그인")').show();
            $('.item:contains("회원가입")').show();
        } else {
            var username = Cookies.get('Username');
            if (username) {
                $('.item:contains("로그인")').hide();
                $('.item:contains("회원가입")').hide();
                $('.item:contains("로그아웃")').show();
            }
        }
    }

    setTimeout(autoLogout, 24 * 60 * 60 * 1000);  // 24시간 후에 autoLogout 함수 실행

    function autoLogout() {
        Cookies.remove('Authorization');
        Cookies.remove('Username');
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        window.location.href = '/auth/login-page';
    }

    // 로그인 모달 표시
    $('.item:contains("로그인")').on('click', function(event) {
        event.preventDefault();
        event.stopPropagation(); // 이벤트 버블링 중지
        $signupModal.modal('hide');
        $loginModal.modal('show');
    });

    // 회원가입 모달 표시
    $('.item:contains("회원가입")').on('click', function(event) {
        event.preventDefault();
        event.stopPropagation(); // 이벤트 버블링 중지
        $loginModal.modal('hide');
        $signupModal.modal('show');
    });

    // 비밀번호 재설정 모달 표시
    $('.item:contains("비밀번호 찾기")').on('click', function(event) {
        console.log("비밀번호 찾기 클릭 이벤트 시작");
        event.preventDefault();
        event.stopPropagation(); // 이벤트 버블링 중지
        $passwordResetModal.modal('show');
        console.log("비밀번호 찾기 클릭 이벤트 종료");
    });
});
