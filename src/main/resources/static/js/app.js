$(document).ready(function () {

    // 아이콘 버튼과 메뉴 요소를 가져옵니다.
    const userIcon = document.getElementById('user-icon');
    const userMenu = document.getElementById('user-menu');

    // 아이콘 버튼을 클릭할 때 메뉴를 토글합니다.
    userIcon.addEventListener('click', () => {
        console.log("start")
        userMenu.classList.toggle('active');
    });

    // 다른 곳을 클릭하면 메뉴를 닫습니다.
    document.addEventListener('click', (event) => {
        if (!userIcon.contains(event.target) && !userMenu.contains(event.target)) {
            userMenu.classList.remove('active');
        }
    });

    // 페이지 로드 시 로그인 상태 확인
    updateLoginStatus();

    updateProfileImage();

    function updateLoginStatus() {
        var token = Cookies.get('Authorization');
        if (!token) {
            $('.item:contains("로그아웃")').hide();
            $('.item:contains("로그인")').show();
            $('.item:contains("회원가입")').show();
            $('.item:contains("프로필")').hide();
            $('.item:contains("회원탈퇴")').hide();
            $('.item:contains("채팅방")').hide();
        } else {
            var nickname = Cookies.get('nickname');
            if (nickname) {
                $('#menu-header').replaceWith('<li class="welcome-msg">' + nickname + '님 환영합니다.</li>');
                $('.item:contains("회원가입")').hide();
                $('.item:contains("로그아웃")').show();
                $('.item:contains("로그인")').hide();
                $('.item:contains("회원탈퇴")').show();
                $('.item:contains("프로필")').show();
                $('.item:contains("채팅방")').show();
            }
        }
    }

    function updateProfileImage() {
        var token = Cookies.get('Authorization');
        var profileImage = Cookies.get('profileImage');

        if (token && profileImage) {
            $('#profile-picture').attr("src", profileImage).show();
            $('#user-icon .user.icon').hide();
        } else {
            $('#user-icon .user.icon').show();
        }

        if (token && !profileImage || profileImage === null) {
            $('#profile-picture').attr("src", "/img/defaultImg/tomato.png").show();
            $('#user-icon .user.icon').hide();
        }
    }

    // 토글 버튼에 클릭 이벤트 핸들러 추가
    $('#toggleSidebarButton').click(function () {
        $('.ui.sidebar').sidebar('toggle'); // 사이드바를 토글합니다.
    });


    // 모달 초기화
    const $signupModal = $('#signupModal').modal({
        onShow: function () {
            $('#user-menu').hide();
        },
        onHide: function () {
            $('#user-icon').show();
        }
    });

    const $loginModal = $('#loginModal').modal({
        onShow: function () {
            $('#user-menu').hide();
        },
        onHide: function () {
            $('#user-icon').show();
        }
    });

    const $passwordResetModal = $('#passwordResetModal').modal({
        onShow: function () {
            $('#user-menu').hide();
        },
        onHide: function () {
            $('#user-icon').show();
        }
    });

    const $usernameFindModal = $('#usernameFindModal').modal({
        onShow: function () {
            $('#user-menu').hide();
        },
        onHide: function () {
            $('#user-icon').show();
        }
    });

    const $deactivationModal = $('#deactivationModal').modal({
        onShow: function () {
            $('#user-menu').hide();
        },
        onHide: function () {
            $('#user-icon').show();
        }
    });

    const $logoutModal = $('#logout-confirm-modal').modal({
        onShow: function () {
            $('#user-menu').hide();
        },
        onHide: function () {
            $('#user-icon').show();
        }
    });

    // 프로필 모달이 열릴 때 한 번만 호출되도록 플래그 설정
    var isProfileModalInitialized = false;

    // 프로필 모달이 열릴 때 초기화 함수를 호출하여 지도를 생성
    const $profileModal = $('#profileModal').modal({
        onShow: function () {
            $('#user-menu').hide();
            if (!isProfileModalInitialized) {
                // 초기화 함수를 호출하여 지도를 생성
                initializeMap();
                isProfileModalInitialized = true;
            }
        },
        onHide: function () {
            $('#user-icon').show();
        }
    });

    const $deactivationConfirmModal = $('#deactivationConfirmModal').modal({
        onShow: function () {
            $('#user-menu').hide();
        },
        onHide: function () {
            $('#user-icon').show();
        }
    });

// user-icon 버튼 클릭 시 user-menu 토글
    $('#user-icon').on('click', function (event) {
        event.stopPropagation();
        $('#user-menu').toggle();
    });


    // 페이지 로드 시 '저장' 버튼 숨기기
    $('#saveProfile').hide();

    // 로그인 모달 표시
    $('.item:contains("로그인")').on('click', function (event) {
        event.preventDefault();
        event.stopPropagation(); // 이벤트 버블링 중지
        $signupModal.modal('hide');
        $deactivationModal.modal('hide');
        $loginModal.modal('show');
        $logoutModal.modal('hide');
        $profileModal.modal('hide');
        $deactivationConfirmModal.modal('hide');
    });

    // 회원가입 모달 표시
    $('.item:contains("회원가입")').on('click', function (event) {
        event.preventDefault();
        event.stopPropagation(); // 이벤트 버블링 중지
        handleLocationClick();
        $loginModal.modal('hide');
        $deactivationModal.modal('hide');
        $deactivationConfirmModal.modal('hide');
        $signupModal.modal('show');
        $logoutModal.modal('hide');
        $profileModal.modal('hide');
    });


    // 회원탈퇴 모달 표시
    $('.item:contains("회원탈퇴")').on('click', function (event) {
        event.preventDefault();
        event.stopPropagation();
        $deactivationModal.modal('show');
        // $deactivationConfirmModal.modal('hide');
        $loginModal.modal('hide');
        $signupModal.modal('hide');
        $logoutModal.modal('hide');
        $profileModal.modal('hide');
    });

    // 로그아웃 모달 표시
    $('.item:contains("로그아웃")').on('click', function (event) {
        console.log("로그아웃 클릭 이벤트 시작");
        event.preventDefault();
        event.stopPropagation();
        $logoutModal.modal('show');
        $loginModal.modal('hide');
        $signupModal.modal('hide');
        $deactivationModal.modal('hide');
        $deactivationConfirmModal.modal('hide');
        $profileModal.modal('hide');
    });

    // 비밀번호 재설정 모달 표시
    $('.item:contains("비밀번호 찾기")').on('click', function (event) {
        console.log("비밀번호 찾기 클릭 이벤트 시작");
        event.preventDefault();
        event.stopPropagation();
        $passwordResetModal.modal('show');
        console.log("비밀번호 찾기 클릭 이벤트 종료");
    });

    // 아이디 찾기 모달 표시
    $('.item:contains("아이디 찾기")').on('click', function (event) {
        console.log("아이디 찾기 클릭 이벤트 시작");
        event.preventDefault();
        event.stopPropagation(); // 이벤트 버블링 중지
        $usernameFindModal.modal('show');
        console.log("아이디 찾기 클릭 이벤트 종료");
    });

    // 위치 설정 버튼 클릭 시 현재 위치 정보만 가져옴
    var currentLatitude, currentLongitude;
    var circle; // circle 변수를 전역 스코프로 이동

    function handleLocationClick() {
        if ("geolocation" in navigator) {
            navigator.geolocation.getCurrentPosition(function (position) {
                currentLatitude = position.coords.latitude;
                currentLongitude = position.coords.longitude;
                $('#signup-setLocation').off('click', handleLocationClick);
            }, function (error) {
                alert(`ERROR(${error.code}): ${error.message}`);
            });
        } else {
            alert("브라우저가 위치 정보를 지원하지 않습니다.");
        }
    }

// 프로필 모달이 열릴 때 지도 초기화 함수를 호출
    function initializeMap() {
        var container = document.getElementById('map'); // 지도를 담을 영역의 DOM 레퍼런스
        const setLocation = $('#radiusSelect').val();

        let setRadius = 5 + parseInt(setLocation);

        if (setLocation > 3) {
            setRadius = 8;
        }

        var options = {
            center: new kakao.maps.LatLng(currentLatitude, currentLongitude),
            level: setRadius
        };

        // 지도 객체를 초기화
        map = new kakao.maps.Map(container, options);
    }

// 위치설정 버튼 클릭 시 원을 초기화하고 새로운 반경에 따라 원을 추가
    $('#setLocation').click(function () {
        handleLocationClick();
        var container = document.getElementById('map');

        // 기존 원을 지도에서 제거
        if (circle) {
            circle.setMap(null);
        }

        // 이미 생성된 지도 객체를 초기화하여 크기만 조절
        initializeMap();

        // 선택한 반경 값 가져오기
        var selectedRadius = parseInt($('#radiusSelect').val());

        // 카카오 지도에 원을 추가하여 새로운 반경을 표시
        circle = new kakao.maps.Circle({
            center: map.getCenter(),
            radius: selectedRadius * 1000,
            strokeWeight: 5,
            strokeColor: '#75B8FA',
            strokeOpacity: 1,
            strokeStyle: 'dashed',
            fillColor: '#CFE7FF',
            fillOpacity: 0.7
        });

        circle.setMap(map);
    });

    $('#signup-setLocation').click(function (event) {
        event.preventDefault();
        handleLocationClick();

        if (currentLatitude === undefined && currentLongitude === undefined) {
            handleLocationClick();
        }

        if(currentLatitude !== undefined && currentLongitude !== undefined) {
            $('#signup-map').show();
        }

        const getRedius = $('#signup-radiusSelect').val();

        let setRadius = 5 + parseInt(getRedius);

        if (getRedius > 3) {
            setRadius = 8;
        }

        var container = document.getElementById('signup-map'); //지도를 담을 영역의 DOM 레퍼런스
        var options = { //지도를 생성할 때 필요한 기본 옵션
            center: new kakao.maps.LatLng(currentLatitude, currentLongitude), //지도의 중심좌표.
            level: setRadius //지도의 레벨(확대, 축소 정도)
        };

        var map = new kakao.maps.Map(container, options); //지도 생성 및 객체 리턴

        // // 마커가 표시될 위치입니다
        // var markerPosition = new kakao.maps.LatLng(currentLatitude, currentLongitude);
        //
        // // 마커를 생성합니다
        // var marker = new kakao.maps.Marker({
        //     position: markerPosition
        // });
        //
        // // 마커가 지도 위에 표시되도록 설정합니다
        // marker.setMap(map);

        // 선택한 반경 값을 가져옵니다.
        var selectedRadius = parseInt(getRedius); // 선택한 반경 값을 정수로 변환합니다.

        // 카카오 지도의 중심 좌표를 가져옵니다.
        var center = map.getCenter();

        // 카카오 지도에 원을 추가하여 반경을 표시합니다.
        var circle = new kakao.maps.Circle({
            center: center,
            radius: selectedRadius * 1000, // 선택한 반경(km)을 미터로 변환합니다.
            strokeWeight: 5, // 원의 선 두께
            strokeColor: '#75B8FA', // 선의 색깔
            strokeOpacity: 1, // 선의 불투명도
            strokeStyle: 'dashed', // 선의 스타일
            fillColor: '#CFE7FF', // 채우기 색깔
            fillOpacity: 0.7 // 채우기 불투명도
        });
        // 원을 지도에 추가합니다.
        circle.setMap(map);
    });

    // 프로필 버튼 클릭 시 모달 표시
    $('#profile-btn').on('click', function () {
        handleLocationClick();
        $('#profileModal').modal('show');
        $signupModal.modal('hide');
        $loginModal.modal('hide');
        $deactivationModal.modal('hide');
        $logoutModal.modal('hide');

        var token = Cookies.get('Authorization');
        // "Bearer "가 붙어 있지 붙여줌
        if (!token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        // 프로필 조회 요청
        $.ajax({
            url: "/api/users/profile",
            type: "GET",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", token);
            },
            success: function (res) {
                // 응답 데이터를 이용하여 프로필 정보 필드에 채움
                $("input[name='profile-usernameInput']").val(res.username);
                $("input[name='profile-nicknameInput']").val(res.nickname);
                $("input[name='profile-emailInput']").val(res.email);
                $("input[name='profile-phoneNumberInput']").val(res.phoneNumber);
                $("input[name='profile-passwordInput']").val(res.password);

                const selectItem = $('.location-input-button-container .menu .item').data();

                if (res.radius !== selectItem) {
                    $('.location-input-button-container .selected').attr('class', 'item');
                    $(`.location-input-button-container .item[data-value=${res.radius}]`).attr('class', 'item active selected');

                    $('.location-input-button-container .text').text(res.radius + "km")
                    if ($('.location-input-button-container .text').text() === "nullkmnullkm") {
                        $(`.location-input-button-container .item[data-value="1"]`).attr('class', 'item active selected');
                        $('.location-input-button-container .text').text("1km")
                    }
                }

                // $('#radiusSelect').val(res.radius);

                // 위치 정보 필드 처리

                currentLatitude = res.latitude;
                currentLongitude = res.longitude;

                if (currentLongitude === 0 && currentLatitude === 0 || currentLongitude === undefined && currentLatitude === undefined) {
                    handleLocationClick();
                }
                const currentRadius = res.radius;
                let setRadius = 5 + res.radius;

                if (currentRadius > 3) {
                    setRadius = 8;
                }

                var container = document.getElementById('map'); //지도를 담을 영역의 DOM 레퍼런스
                var options = { //지도를 생성할 때 필요한 기본 옵션
                    center: new kakao.maps.LatLng(currentLatitude, currentLongitude), //지도의 중심좌표.
                    level: setRadius //지도의 레벨(확대, 축소 정도)
                };

                var map = new kakao.maps.Map(container, options); //지도 생성 및 객체 리턴

                // // 마커가 표시될 위치입니다
                // var markerPosition = new kakao.maps.LatLng(currentLatitude, currentLongitude);
                //
                // // 마커를 생성합니다
                // var marker = new kakao.maps.Marker({
                //     position: markerPosition
                // });
                //
                // // 마커가 지도 위에 표시되도록 설정합니다
                // marker.setMap(map);

                // 선택한 반경 값을 가져옵니다.
                var selectedRadius = parseInt(currentRadius); // 선택한 반경 값을 정수로 변환합니다.

                // 카카오 지도의 중심 좌표를 가져옵니다.
                var center = map.getCenter();

                // 카카오 지도에 원을 추가하여 반경을 표시합니다.
                var circle = new kakao.maps.Circle({
                    center: center,
                    radius: selectedRadius * 1000, // 선택한 반경(km)을 미터로 변환합니다.
                    strokeWeight: 5, // 원의 선 두께
                    strokeColor: '#75B8FA', // 선의 색깔
                    strokeOpacity: 1, // 선의 불투명도
                    strokeStyle: 'dashed', // 선의 스타일
                    fillColor: '#CFE7FF', // 채우기 색깔
                    fillOpacity: 0.7 // 채우기 불투명도
                });
                // 원을 지도에 추가합니다.
                circle.setMap(map);


                // 아래 코드는 지도 위의 마커를 제거하는 코드입니다
                // marker.setMap(null);


            },
            error: function (error) {
                alert("프로필 조회 실패. 다시 시도해주세요.");
            }
        });
    });

    // 프로필 정보 모달 열기
    $('#profileModal').modal('attach events', '#editProfile', 'show');

    // 프로필 정보 수정 버튼 클릭 이벤트
    $('#editProfile').click(function () {
        // 반경 설정 필드와 버튼을 보이게 하고, 수정 버튼 숨김
        $('#radiusSelect, #setLocation').show();
        $('#profileModal input').css('border', '');
        $('#profile-imageInput').css('display','none');
        $('.temp-none').show();
        $('.divider').hide();
        $('#radius-id').show();
        $('#profile-image-id').show();

        // 수정 가능한 필드들의 readonly 속성 제거
        $('#profileModal input').prop('readonly', false);

        // username 입력 필드만 다시 읽기 전용으로 설정합니다.
        $("input[name='profile-usernameInput']").prop('readonly', true);
        $('#customRadiusInput').prop('readonly', false);
        $('#radiusSelect').prop('disabled', false);
        $('#setLocation').prop('disabled', false);

        // 수정 완료 버튼 표시, 수정 버튼 숨김
        $('#saveProfile').show();
        $('#editProfile').hide();
    });

    // 프로필 정보 저장 버튼 클릭 이벤트
    $('#saveProfile').click(function () {
        var newPassword = $("input[name='profile-passwordInput']").val();
        var phoneNumber = $("input[name='profile-phoneNumberInput']").val()
        var profileImage = $("input[name='profile-imageInput']")[0];
        var email = $("input[name='profile-emailInput']").val()
        var formData = new FormData();

        if (profileImage && profileImage.files.length > 0) {
            formData.append('profileImage', profileImage.files[0]);
        }
        if (newPassword !== "********") {
            formData.append('password', newPassword);
        }

        formData.append('radius', $('#radiusSelect').val());// 결정된 반경 값 추가
        formData.append('nickname', $("input[name='profile-nicknameInput']").val());
        formData.append('email', email);
        formData.append('phoneNumber', phoneNumber);
        formData.append('latitude', currentLatitude); // 위도 추가
        formData.append('longitude', currentLongitude); // 경도 추가

        var container = document.getElementById('map'); //지도를 담을 영역의 DOM 레퍼런스
        var options = { //지도를 생성할 때 필요한 기본 옵션
            center: new kakao.maps.LatLng(currentLatitude, currentLongitude), //지도의 중심좌표.
            level: 3 //지도의 레벨(확대, 축소 정도)
        };

        var map = new kakao.maps.Map(container, options); //지도 생성 및 객체 리턴

        // // 마커가 표시될 위치입니다
        // var markerPosition = new kakao.maps.LatLng(currentLatitude, currentLongitude);
        //
        // // 마커를 생성합니다
        // var marker = new kakao.maps.Marker({
        //     position: markerPosition
        // });

        // 마커가 지도 위에 표시되도록 설정합니다
        // marker.setMap(map);

        // 아래 코드는 지도 위의 마커를 제거하는 코드입니다
        // marker.setMap(null);

        console.log("latitude : " + currentLatitude)
        console.log("longitude : " + currentLongitude)

        $.ajax({
            url: `/api/users/profile`,
            type: 'PUT',
            data: formData, // FormData 객체 전송
            processData: false, // 데이터를 처리하지 않도록 설정
            contentType: false, // 컨텐츠 타입을 설정하지 않도록 설정
            beforeSend: function (xhr) {
                var token = decodeURIComponent(Cookies.get('Authorization'));

                // 토큰을 Authorization 헤더에 설정
                xhr.setRequestHeader('Authorization', token);
            },
            success: function (res) {
                // 사용자 이름 변경 후
                var newNickname = $("input[name='profile-nicknameInput']").val();

                // 이미지URL 가져오기
                const newprofileImage = res.data.profileImage;
                console.log("profileImage : " + profileImage)

                // 현재 시간을 가져옵니다.
                const currentTime = new Date();

                // 만료 시간을 3시간 뒤로 설정합니다.
                const expirationTime = new Date(currentTime.getTime() + 3 * 60 * 60 * 1000);

                if (newNickname) {
                    Cookies.set('nickname', newNickname, {expires: expirationTime});
                }

                if (newprofileImage) {
                    Cookies.set('profileImage', newprofileImage, {expires: expirationTime});
                }


                // 수정 가능한 필드들의 readonly 속성 추가
                $('#profileModal input').prop('readonly', true);
                $('#customRadiusInput').prop('readonly', true);
                $('#radiusSelect').prop('disabled', true);
                $('#setLocation').prop('disabled', true);

                // 수정 완료 버튼 숨김, 수정 버튼 표시
                alert("프로필 정보가 성공적으로 업데이트되었습니다.");
                $('#editProfile').show();
                $('#saveProfile').hide();

                window.location.href = '/';
            },
            error: function (res) {
                console.log(res)
                if (res.responseJSON.message === "중복된 전화번호입니다.") {
                    alert("중복된 전화번호입니다.")
                } else if (res.responseJSON.message === "중복된 이메일입니다.") {
                    alert("중복된 이메일입니다.")
                } else if (res.responseJSON.message === "중복된 닉네임입니다.") {
                    alert("중복된 닉네임입니다.")
                } else if (res.responseJSON.message === "중복된 전화번호, 이메일, 닉네임입니다.") {
                    alert("중복된 전화번호, 이메일, 닉네임입니다.");
                } else if (res.responseJSON.message === "중복된 전화번호, 이메일입니다.") {
                    alert("중복된 전화번호, 이메일입니다.");
                } else if (res.responseJSON.message === "중복된 전화번호, 닉네임입니다.") {
                    alert("중복된 전화번호, 닉네임입니다.")
                } else if (res.responseJSON.message === "중복된 이메일, 닉네임입니다.") {
                    alert("중복된 이메일, 닉네임입니다.")
                } else {
                    alert("알 수 없는 오류가 발생하였습니다.")
                }
            }
        });
    });

    // 인증번호 전송 버튼 클릭 이벤트
    $('#signup-sendVerificationCode').on('click', function (event) {
        event.preventDefault();

        var phoneNumber = $('#signup-phoneNumberInput').val();
        console.log("phoneNumber : " + phoneNumber)
        if (!phoneNumber) { // 전화번호가 입력되지 않았을 때
            alert("전화번호를 입력해주세요.");
            return;
        } else {
            $.ajax({
                url: `/api/users/sms/codes?` + $.param({phonenumber: phoneNumber}),
                type: "POST",
                success: function (res) {
                    alert("인증번호가 전송되었습니다.");
                },
                error: function (error) {
                    alert("인증번호 전송 실패. 다시 시도해주세요.");
                }
            });
        }
    });

    // 인증번호 확인 버튼 클릭 이벤트
    $('#signup-verificationCode').on('click', function (event) {
        event.preventDefault();

        var phoneNumber = $('#signup-phoneNumberInput').val();
        var inputCode = $('#signup-verifyCodeInput').val();
        if (!phoneNumber || !inputCode) { // 전화번호나 인증 코드가 입력되지 않았을 때
            alert("전화번호와 인증코드를 모두 입력해주세요.");
            return;
        }

        $.ajax({
            url: `/api/users/sms/verify-codes?` + $.param({phonenumber: phoneNumber, inputcode: inputCode}),
            type: "POST",
            success: function (res) {
                if (res.status === 200) {
                    alert("인증 성공");
                    // 인증 성공 시 인증 완료 여부를 저장
                    $('#verificationCompleted').val('true');
                } else {
                    alert("존재하지 않는 인증코드입니다. 다시 입력해주세요");
                }
            },
            error: function (error) {
                alert("인증 실패. 다시 시도해주세요.");
            }
        });
    });

    // 체크박스 상태 변경 이벤트 리스너 추가
    $('#accept-terms').on('change', function () {
        var isChecked = $(this).prop('checked');
        if (!isChecked) {
            $('#signup-submit').prop('disabled', true); // 체크가 해제될 때 버튼 비활성화
        } else {
            $('#signup-submit').prop('disabled', false); // 체크되었을 때 버튼 활성화
        }
    });

    // 초기 상태에서 버튼 비활성화
    $('#signup-submit').prop('disabled', true);

    // 회원가입 버튼 클릭 이벤트
    $('#signupModal button[type="submit"]').on('click', function (event) {
        event.preventDefault();

        var username = $('#signup-usernameInput').val();
        var password = $('#signup-passwordInput').val();
        var nickname = $('#signup-nicknameInput').val();
        var email = $('#signup-emailInput').val();
        var phoneNumber = $('#signup-phoneNumberInput').val();
        var inputCode = $('#signup-verifyCodeInput').val();
        var selectedRadiusValue = $('#signup-radiusSelect').val();

        if (!username || !password || !nickname || !email || !phoneNumber || !inputCode) {
            alert("모든 필드를 입력해주세요.");
            return;
        } else {
        }

        if (username.length < 4) {
            alert("아이디를 최소 4글자 이상으로 작성해주세요.");
            return false;
        } else if (username.length > 20) {
            alert("아이디를 최소 20자 이하로 작성해주세요.");
            return false;
        }

        // 인증이 완료되지 않았을 때 알림을 표시하고 회원가입을 중지
        if ($('#verificationCompleted').val() !== 'true') {
            alert("휴대폰 인증을 먼저 해주세요.");
            return;
        }

        // currentLatitude, currentLongitude가 비어있는 경우 알림을 띄웁니다.
        if (!currentLatitude || !currentLongitude || !selectedRadiusValue) {
            alert("위치 설정을 먼저 해주세요.");
            return;
        }

        var formData = new FormData();
        var profileImage = $('input[name="signup-profileImageInput"]')[0];
        if (profileImage && profileImage.files.length > 0) {
            formData.append('profileImage', profileImage.files[0]);
        }

        formData.append('username', username);
        formData.append('password', password);
        formData.append('nickname', nickname);
        formData.append('email', email);
        formData.append('phoneNumber', phoneNumber);
        formData.append('latitude', currentLatitude); // 위도 추가
        formData.append('longitude', currentLongitude); // 경도 추가
        formData.append('radius', selectedRadiusValue); // 반경 추가

        $.ajax({
            url: "/api/users/signup",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (res) {
                alert("성공적으로 회원가입이 되었습니다!");
                $('.item:contains("회원가입")').hide();
                window.location.href = '/';
            },
            error: function (res) {
                console.log(res)
                alert(res.responseText.message)
            }
        });
    });


    // 파일 선택 input 엘리먼트 가져오기
    const fileInput = document.querySelector('input[name="profile-imageInput"]');
    const signupFileInput = document.querySelector('input[name="signup-profileImageInput"]');
    const selectedFileNameSpan = document.getElementById('selectedFileName');
    const signupSelectedFileNameSpan = document.getElementById('signup-selectedFileName');

    if (fileInput) {
        fileInput.addEventListener('change', function () {
            const selectedFile = fileInput.files[0];
            if (selectedFile) {
                selectedFileNameSpan.textContent = selectedFile.name;
            } else {
                selectedFileNameSpan.textContent = ''; // 파일 선택 취소 시 텍스트 제거
            }
        });
    }

    if (signupFileInput) {
        signupFileInput.addEventListener('change', function () {
            const selectedFile = signupFileInput.files[0];
            if (selectedFile) {
                signupSelectedFileNameSpan.textContent = selectedFile.name;
            } else {
                signupSelectedFileNameSpan.textContent = '';
            }
        })
    }

    // 초기 상태에서 버튼 비활성화
    $('#login-submit').prop('disabled', true);

    // 아이디와 비밀번호 입력 필드의 input 이벤트 모니터링
    $('input[name="login-usernameInput"], input[name="login-passwordInput"]').on('input', function () {
        // 아이디와 비밀번호 필드 중 하나라도 비어있으면 로그인 버튼 비활성화
        const $usernameInput = $('input[name="login-usernameInput"]');
        const $passwordInput = $('input[name="login-passwordInput"]');

        if ($usernameInput.val().trim() === '' || $passwordInput.val().trim() === '') {
            $('#login-submit').prop('disabled', true);
        } else {
            $('#login-submit').prop('disabled', false);
        }
    })

    // 로그인 버튼 클릭 이벤트
    $('#loginModal button[type="submit"]').on('click', function (event) {
        event.preventDefault();

        // 사용자가 입력한 아이디와 비밀번호 가져오기
        const username = $('input[name="login-usernameInput"]').val();
        const password = $('input[name="login-passwordInput"]').val();

        // 아이디와 비밀번호 필드 중 하나라도 비어있으면 로그인 버튼 비활성화
        if (!username || !password) {
            $('#login-submit').prop('disabled', true);
            alert("아이디와 비밀번호를 입력해주세요.");
            return;
        } else {
            $('#login-submit').prop('disabled', false);
        }

        $.ajax({
            url: "/api/users/login",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({username: username, password: password}),
            success: function (res, status, xhr) {
                console.log(res.data)
                console.log("status : " + status)

                // HTTP 헤더에서 토큰 가져오기
                const token = xhr.getResponseHeader("Authorization");
                console.log("token : " + token)

                // 서버 응답에서 userId, nickname 꺼내오기
                const userId = res.data.userId;
                const nickname = res.data.nickname;
                const profileImage = res.data.profileImage;
                console.log("profileImage : " + profileImage)

                // 현재 시간을 가져옵니다.
                const currentTime = new Date();

                // 만료 시간을 3시간 뒤로 설정합니다.
                const expirationTime = new Date(currentTime.getTime() + 3 * 60 * 60 * 1000);

                // 토큰을 쿠키에 저장하고, 만료 시간을 설정합니다.
                Cookies.set('Authorization', token, {expires: expirationTime});
                Cookies.set('nickname', nickname, {expires: expirationTime});
                Cookies.set('userId', userId, {expires: expirationTime});

                if (profileImage !== null) {
                    Cookies.set('profileImage', profileImage, {expires: expirationTime});
                }

                $loginModal.modal('hide');
                $signupModal.modal('hide');

                alert("성공적으로 로그인 했습니다!");

                // 로그인 상태 UI ni
                $('#login-btn').replaceWith('<li class="welcome-msg">' + nickname + '님 환영합니다.</li>');

                window.location.href = '/';

            },
            error: function (error) {
                alert("로그인 실패. 다시 시도해주세요.");
            }
        });
    });

    // 네이버 정보 가져오기
    const NAVER_CLIENT_ID = "1y4x9PZB2SmdqeC02T5g";
    const NAVER_REDIRECT_URL = "https://myplacetomato.site/api/user/naver/callback";

    // 네이버 요청 URL 만들기
    const NAVER_AUTH_URL = `https://nid.naver.com/oauth2.0/authorize?client_id=${NAVER_CLIENT_ID}&redirect_uri=${NAVER_REDIRECT_URL}&response_type=code`;

    // 네이버 로그인 버튼 클릭 이벤트
    document.getElementById("naverLogin").addEventListener("click", function (e) {
        e.preventDefault();
        window.location.href = NAVER_AUTH_URL;
    });


    // 네이버 로그인 요청이 완료되면 실행되는 코드
    if (window.location.href.includes("success=naver")) {
        const urlParams = new URLSearchParams(window.location.search);

        const nickname = urlParams.get("nickname");
        const userId = urlParams.get("userId");
        const token = urlParams.get("token");
        const email = urlParams.get('email');
        const phoneNumber = urlParams.get('phonenumber');
        const currentLatitude = urlParams.get('latitude');
        const currentLongitude = urlParams.get('longitude');
        const defaultProfileImagePath = "/img/defaultImg/tomato.png";

        console.log(nickname)
        console.log(email)
        console.log(phoneNumber)

        // 현재 시간을 가져옵니다.
        const currentTime = new Date();

        // 만료 시간을 3시간 뒤로 설정합니다.
        const expirationTime = new Date(currentTime.getTime() + 3 * 60 * 60 * 1000);

        // 토큰을 쿠키에 저장하고, 만료 시간을 설정합니다.
        Cookies.set('Authorization', token, {expires: expirationTime});
        Cookies.set('nickname', nickname, {expires: expirationTime});
        Cookies.set('userId', userId, {expires: expirationTime});
        Cookies.set("profileImage", defaultProfileImagePath, {expires: expirationTime});

        // 로그인 상태 UI 업데이트
        $('#login-btn').replaceWith('<li class="welcome-msg">' + nickname + '님 환영합니다.</li>');

        if (email === "null" && phoneNumber === "null" && currentLatitude === "0.0" && currentLongitude === "0.0") {
            alert("로그인 성공! 프로필에서 이메일, 전화번호, 위치설정을 바로 설정해주세요!")
        } else if (email === "null" && phoneNumber !== "null" && currentLatitude !== "0.0" && currentLongitude !== "0.0") {
            alert("로그인 성공! 프로필에서 지금 바로 이메일을 설정해주세요!")
        } else if (phoneNumber === "null" && email !== "null" && currentLatitude !== "0.0" && currentLongitude !== "0.0") {
            alert("로그인 성공! 프로필에서 지금 바로 전화번호를 설정해주세요!")
        } else if (currentLatitude === 0.0 && currentLongitude === "0.0" && phoneNumber !== "null" && email !== "null") {
            alert("로그인 성공! 프로필에서 지금 바로 위치를 설정해주세요!")
        } else if (email === "null" && phoneNumber === "null" && currentLatitude !== "0.0" && currentLongitude !== "0.0") {
            alert("로그인 성공! 프로필에서 지금 바로 이메일, 전화번호를 설정해주세요!")
        } else if (email === "null" && currentLatitude === "0.0" && currentLongitude === "0.0" && phoneNumber !== "null") {
            alert("로그인 성공! 프로필에서 지금 바로 이메일, 위치를 설정해주세요!")
        } else if (phoneNumber === "null" && currentLatitude === "0.0" && currentLongitude === "0.0" && email === "null") {
            alert("로그인 성공! 프로필에서 지금 바로 전화번호, 위치를 설정해주세요!")
        } else if (email !== "null" && phoneNumber !== "null" && currentLatitude !== "0.0" && currentLongitude !== "0.0") {
            alert("로그인 성공!")
        }

        // 모달 숨기기
        $loginModal.modal('hide');
        $signupModal.modal('hide');
        $deactivationModal.modal('hide');

        // 현재 페이지의 URL에서 'success=naver'를 제거
        const newURL = window.location.href.split("?")[0];
        window.history.replaceState({}, document.title, newURL);

        window.location.href = "/"
    }


    // 카카오 정보 가져오기
    const KAKAO_CLIENT_ID = "09f2acecd9cd8bf7b7d3f6951daf4548";
    const KAKAO_REDIRECT_URL = "https://myplacetomato.site/api/user/kakao/callback";

    // 카카오 요청 URL 만들기
    const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_CLIENT_ID}&redirect_uri=${KAKAO_REDIRECT_URL}&response_type=code`;

    // 카카오 로그인 버튼 클릭 이벤트
    document.getElementById("kakaoLogin").addEventListener("click", function (e) {
        e.preventDefault();
        window.location.href = KAKAO_AUTH_URL;
    });

    // 카카오 로그인 요청이 완료되면 실행되는 코드
    if (window.location.href.includes("success=kakao")) {
        const urlParams = new URLSearchParams(window.location.search);

        const nickname = urlParams.get("nickname");
        const userId = urlParams.get("userId");
        const token = urlParams.get("token");
        const email = urlParams.get('email');
        const phoneNumber = urlParams.get('phonenumber');
        const currentLatitude = urlParams.get('latitude');
        const currentLongitude = urlParams.get('longitude');
        const defaultProfileImagePath = "/img/defaultImg/tomato.png";
        console.log(nickname)
        console.log(email)
        console.log(phoneNumber)

        // 현재 시간을 가져옵니다.
        const currentTime = new Date();

        // 만료 시간을 3시간 뒤로 설정합니다.
        const expirationTime = new Date(currentTime.getTime() + 3 * 60 * 60 * 1000);

        // 로그인 정보를 쿠키에 저장
        Cookies.set("nickname", nickname, {expires: expirationTime});
        Cookies.set("userId", userId, {expires: expirationTime});
        Cookies.set("Authorization", token, {expires: expirationTime});
        Cookies.set("profileImage", defaultProfileImagePath, {expires: expirationTime});

        // 로그인 상태 UI 업데이트
        $('#login-btn').replaceWith('<li class="welcome-msg">' + nickname + '님 환영합니다.</li>');

        console.log("latitude : " + currentLatitude)
        console.log("longitude : " + currentLongitude)

        if (!email && !phoneNumber && currentLatitude === "0.0" && currentLongitude === "0.0") {
            alert("로그인 성공! 프로필에서 이메일, 전화번호, 위치설정을 바로 설정해주세요!")
        } else if (!email && phoneNumber && currentLatitude !== "0.0" && currentLongitude !== "0.0") {
            alert("로그인 성공! 프로필에서 지금 바로 이메일을 설정해주세요!")
        } else if (!phoneNumber && email && currentLatitude !== "0.0" && currentLongitude !== "0.0") {
            alert("로그인 성공! 프로필에서 지금 바로 전화번호를 설정해주세요!")
        } else if (currentLatitude === "0.0" && currentLongitude === "0.0" && phoneNumber && email) {
            alert("로그인 성공! 프로필에서 지금 바로 위치를 설정해주세요!")
        } else if (!email && !phoneNumber && currentLatitude !== "0.0" && currentLongitude !== "0.0") {
            alert("로그인 성공! 프로필에서 지금 바로 이메일, 전화번호를 설정해주세요!")
        } else if (!email && currentLatitude === "0.0" && currentLongitude === "0.0" && phoneNumber) {
            alert("로그인 성공! 프로필에서 지금 바로 이메일, 위치를 설정해주세요!")
        } else if (!phoneNumber && currentLatitude === "0.0" && currentLongitude === "0.0" && email) {
            alert("로그인 성공! 프로필에서 지금 바로 전화번호, 위치를 설정해주세요!")
        } else if (email && phoneNumber && currentLatitude !== "0.0" && currentLongitude !== "0.0") {
            alert("로그인 성공!")
        }

        // 모달 숨기기
        $loginModal.modal('hide');
        $signupModal.modal('hide');
        $deactivationModal.modal('hide');

        // 현재 페이지의 URL에서 'success=kakao'를 제거
        const newURL = window.location.href.split("?")[0];
        window.history.replaceState({}, document.title, newURL);

        window.location.href = "/"
    }

    // 로그아웃 확인 모달의 확인 버튼 클릭 시 로그아웃 이벤트 실행
    $('#logout-confirm-modal .ui.green.ok.inverted.button').on('click', function () {
        // 확인 버튼을 클릭하면 로그아웃을 실행
        console.log("로그아웃 시작");

        var token = Cookies.get('Authorization');
        if (!token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }

        console.log("token : " + token);

        $.ajax({
            url: "/api/users/logout",
            type: "POST",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", token);
            },
            success: function (res) {
                if (res.status === 200) {
                    alert(res.message);
                    Cookies.remove('Authorization');
                    Cookies.remove('nickname');
                    Cookies.remove('userId');
                    Cookies.remove('profileImage');
                    window.location.href = "/";
                } else {
                    alert("로그아웃 실패: " + res.message);
                }
            },
            error: function (error) {
                console.log("로그아웃 종료");
                alert("로그아웃 요청 실패: " + error.statusText);
            }
        });
    });

    // 회원탈퇴 인증번호 전송
    $('#deactive-sendVerificationCode').on('click', function () {
        let phoneNumber = $('#deactive-phoneNumberInput').val();
        if (phoneNumber) {
            $.ajax({
                url: `/api/users/sms/codes?` + $.param({phonenumber: phoneNumber}),
                type: "POST",
                success: function (res) {
                    alert("인증번호가 전송되었습니다.");
                },
                error: function (error) {
                    alert(error.resText);
                }
            });
        }
    });

    // 회원탈퇴 인증번호 확인
    $('#deactive-verificationCode').on('click', function () {
        let phoneNumber = $('#deactive-phoneNumberInput').val();
        let inputCode = $('#deactive-verifyCodeInput').val();
        if (phoneNumber || inputCode) {
            $.ajax({
                url: `/api/users/sms/verify-codes?` + $.param({phonenumber: phoneNumber, inputcode: inputCode}),
                type: "POST",
                success: function (res) {
                    alert("인증 성공!");
                },
                error: function (error) {
                    alert(error.resText);
                }
            });
        }

    });

    // 회원탈퇴 인증코드 전송
    $('#deactive-sendDeactiveCode').on('click', function () {
        let phoneNumber = $('#deactive-phoneNumberInput').val();
        let email = $('#deactive-emailInput').val();
        let inputCode = $('#deactive-verifyCodeInput').val();
        if (phoneNumber || inputCode || email) {
            $.ajax({
                url: `/api/users/email/deactivate/verify-codes?` + $.param({phonenumber: phoneNumber, email: email}),
                type: "POST",
                success: function (res) {
                    alert("이메일로 회원탈퇴 인증코드가 전송되었습니다.");
                },
                error: function (error) {
                    alert(error.resText);
                }
            });
        }
    });

    // 회원탈퇴 버튼 활성화/비활성화 처리
    $('#deactivationButton').on('click', function (event) {
        event.preventDefault();
        event.stopPropagation(); // 이벤트 버블링 중지
        console.log("start")
        let phoneNumber = $('#deactive-phoneNumberInput').val();
        let inputCode = $('#deactive-verifyCodeInput').val();
        let email = $('#deactive-emailInput').val();
        let deactivationCode = $('#deactive-deactivationCodeInput').val();

        if (!email || !phoneNumber || !inputCode || !deactivationCode) {
            alert("모든 필드를 입력해주세요.");
            return;
        } else {
            $deactivationConfirmModal.modal('show');
        }
    });

    // 체크박스 상태에 따라 회원탈퇴 버튼 활성화/비활성화 처리
    $('#confirmDeactivation').on('change', function () {

        if ($(this).prop('checked')) {
            $('#confirmDeactivationButton').prop('disabled', false);
        } else {
            $('#confirmDeactivationButton').prop('disabled', true);
        }
    });

    // 최종 회원탈퇴 처리
    $('#confirmDeactivationButton').on('click', function () {
        let inputCode = $('#deactive-deactivationCodeInput').val();
        let token = Cookies.get('Authorization');
        console.log("inputCode : " + inputCode)
        console.log("token : " + token)

        $.ajax({
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", token);
            },
            url: `/api/users/account/deactivate?` + $.param({inputcode: inputCode}),
            type: 'DELETE',
            success: function (res) {
                // 쿠키삭제
                Cookies.remove('Authorization');
                Cookies.remove('nickname');
                Cookies.remove('userId');
                Cookies.remove('profileImage');

                alert("회원탈퇴가 완료되었습니다.");
                window.location.href = '/';
            },
            error: function (error) {
                alert("인증이 되지 않았거나 알 수 없는 이유로 회원탈퇴가 실패되었습니다. 다시 시도해주세요");
            }
        });
    });

    $('#cancelDeactivation').on('click', function () {
        $deactivationModal.modal('show');

        $loginModal.modal('hide');
        $signupModal.modal('hide');
        $logoutModal.modal('hide');
        $profileModal.modal('hide');
    })

    // 아이디 찾기
    // 인증번호 보내기 버튼 클릭 이벤트
    $('#usernameFind-sendVerificationCode').on('click', function () {
        let phoneNumber = $('#usernameFind-phoneNumberInput').val();

        $.ajax({
            url: `/api/account-recovery/usernames/sms/codes?` + $.param({phonenumber: phoneNumber}),
            type: 'POST',
            success: function (res) {
                alert('인증번호가 전송되었습니다.');
            },
            error: function (error) {
                console.log(error);
                alert('오류가 발생했습니다.');
            }
        });
    });

    // 인증번호 확인 버튼 클릭 이벤트
    $('#usernameFind-verifyPhoneNumber').on('click', function () {
        let phoneNumber = $('#usernameFind-phoneNumberInput').val();
        let inputCode = $('#usernameFind-verifyCodeInput').val();

        $.ajax({
            url: `/api/account-recovery/sms/verify-codes?` + $.param({phonenumber: phoneNumber, inputcode: inputCode}),
            type: 'POST',
            success: function (res) {
                alert('인증번호가 확인되었습니다.');
            },
            error: function (error) {
                alert('오류가 발생했습니다.');
            }
        });
    });

    // 아이디 찾기 버튼 클릭 이벤트
    $('#usernameFind-sendUsername').on('click', function () {
        let phoneNumber = $('#usernameFind-phoneNumberInput').val();
        let email = $('#usernameFind-emailInput').val();

        $.ajax({
            url: `/api/account-recovery/usernames?` + $.param({phonenumber: phoneNumber, email: email}),
            type: 'POST',
            success: function (res) {
                alert('아이디가 ' + email + '로 전송되었습니다.');
            },
            error: function (error) {
                alert('오류가 발생했습니다.');
            }
        });
    });


    // 비밀번호 재설정
    // 아이디 인증 상태를 추적하는 변수
    var isUsernameVerified = false;

    // 0. 아이디 인증
    $('#passwordReset-verifyUsername').on('click', function (event) {
        event.preventDefault();

        var username = $('#passwordReset-usernameInput').val();
        console.log("username : " + username)
        if (!username.trim()) {
            alert("아이디를 입력해주세요.");
            return;
        }
        $.ajax({
            url: `/api/account-recovery/verify-usernames?` + $.param({username: username}),
            type: "POST",
            success: function (res) {
                if (res.status === 200) {
                    alert("아이디가 인증되었습니다.");
                    // 아이디 인증 상태를 true로 설정
                    isUsernameVerified = true;
                } else {
                    alert("아이디 인증 실패. 다시 시도해주세요.");
                    isUsernameVerified = false;
                }
            },
            error: function (error) {
                alert("아이디 인증 실패. 다시 시도해주세요.");
            }
        });
    });

    // 1. 인증 코드 발송
    $('#passwordReset-sendVerificationCode').on('click', function (event) {
        event.preventDefault();

        // 아이디가 인증되지 않았다면 인증 코드를 보내지 않음
        if (!isUsernameVerified) {
            alert("먼저 아이디를 인증해주세요.");
            return;
        }

        var phoneNumber = $('#passwordReset-phoneNumberInput').val();
        if (!phoneNumber.trim()) { // 전화번호가 입력되지 않았을 때
            alert("전화번호를 입력해주세요.");
            return;
        }

        $.ajax({
            url: `/api/account-recovery/password/sms/codes?` + $.param({phonenumber: phoneNumber}),
            type: "POST",
            success: function (res) {
                alert("인증번호가 전송되었습니다.");
            },
            error: function (error) {
                alert("인증번호 전송 실패. 다시 시도해주세요.");
            }
        });
    });

    // 2. 전화번호 인증
    $('#passwordReset-verificationCode').on('click', function (event) {
        event.preventDefault();

        var phoneNumber = $('#passwordReset-phoneNumberInput').val();
        var inputCode = $('#passwordReset-verifyCodeInput').val();

        if (!phoneNumber.trim() || !inputCode.trim()) { // 전화번호나 인증 코드가 입력되지 않았을 때
            alert("전화번호와 인증코드를 모두 입력해주세요.");
            return;
        }
        $.ajax({
            url: `/api/account-recovery/sms/verify-codes?` + $.param({phonenumber: phoneNumber, inputcode: inputCode}),
            type: "POST",
            success: function (res) {
                if (res.status === 200) {
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
    $('#passwordReset-sendTempPassword').on('click', function (event) {
        var phoneNumber = $('#passwordReset-phoneNumberInput').val();
        var email = $('#passwordReset-emailInput').val();
        $.ajax({
            url: `/api/account-recovery/temp-passwords?` + $.param({phonenumber: phoneNumber, email: email}),
            type: "POST",
            success: function (res) {
                alert("임시 비밀번호가 전송되었습니다.");
            },
            error: function (error) {
                alert("임시 비밀번호 전송 실패. 다시 시도해주세요.");
            }
        });
    });

    // 4. 임시 비밀번호로 로그인
    $('#passwordReset-tempLoginButton').on('click', function (event) {
        event.preventDefault();

        // 임시 로그인 폼을 표시
        $('#tempLoginModal').modal('show');
    });

    $('#tempLoginModal form').on('submit', function (event) {
        event.preventDefault();

        // 폼에서 입력한 아이디와 임시 비밀번호 가져오기
        var username = $('input[name="tempLogin-usernameInput"]').val();
        var tempPassword = $('input[name="tempLogin-passwordInput"]').val();
        console.log("username : " + username)
        console.log("tempPassword : " + tempPassword)

        $.ajax({
            url: `/api/account-recovery/temp-login?` + $.param({username: username, temppassword: tempPassword}),
            type: "POST",
            success: function (res, status, xhr) {
                console.log("status : " + status)

                // HTTP 헤더에서 토큰 가져오기
                var token = xhr.getResponseHeader("Authorization");
                console.log("token : " + token)

                // 서버 응답에서 userId 꺼내오기
                console.log("tempuser : " + res.data)
                var userId = res.data.userId;
                var nickname = res.data.nickname;
                var profileImage = res.data.profileImage;

                // 현재 시간을 가져옵니다.
                const currentTime = new Date();

                // 만료 시간을 3시간 뒤로 설정합니다.
                const expirationTime = new Date(currentTime.getTime() + 3 * 60 * 60 * 1000);

                // 토큰을 쿠키에 저장
                Cookies.set('Authorization', token, {expires: expirationTime});
                Cookies.set('nickname', nickname, {expires: expirationTime});
                Cookies.set('userId', userId, {expires: expirationTime});
                if (profileImage) {
                    Cookies.set('profileImage', profileImage, {expires: expirationTime});
                }

                $loginModal.modal('hide');
                $signupModal.modal('hide');

                // 로그인 상태 UI 업데이트
                $('#login-btn').replaceWith('<li class="welcome-msg">' + nickname + '님 환영합니다.</li>');

                alert("성공적으로 로그인 했습니다! 즉시 비밀번호를 변경해주세요!");

                window.location.href = '/';
            },
            error: function (error) {
                alert(error.responseJSON.message)
            }
        });
    });
});

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
