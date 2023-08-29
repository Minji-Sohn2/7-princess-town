jQuery(document).ready(function($) {
	// 페이지 로드 시 로그인 상태 확인
	updateLoginStatus();

	// $form_modal은 .cd-user-modal 클래스를 가진 요소를 참조하고,
	// $form_modal이 참조하는 cd-**라는 id형태를 가진 요소를 참조할 수 있게 함
	var $form_modal = $('.cd-user-modal'),
		$form_login = $form_modal.find('#cd-login'),
		$form_signup = $form_modal.find('#cd-signup'),
		$form_forgot_password = $form_modal.find('#cd-reset-password'),
		$form_modal_tab = $('.cd-switcher'),
		$tab_login = $form_modal_tab.children('li').eq(0).children('a'),
		$tab_signup = $form_modal_tab.children('li').eq(1).children('a'),
		$forgot_password_link = $form_login.find('.cd-form-bottom-message a'),
		$back_to_login_link = $form_forgot_password.find('.cd-form-bottom-message a'),
		$main_nav = $('.main-nav')


	// 회원가입 버튼 클릭 이벤트
	$('#signup-submit').on('click', function (event) {
		event.preventDefault();
		console.log(event);
		var formData = new FormData();

		var profileImage = $('#signup-profileImage')[0];
		if (profileImage.files.length > 0) {
			formData.append('profileImage', profileImage.files[0]); // 프로필 이미지 파일
		}


		// 각 필드의 값을 FormData에 추가
		formData.append('username', $('#signup-username').val());
		formData.append('password', $('#signup-password').val());
		formData.append('nickname', $('#signup-nickname').val());
		formData.append('email', $('#signup-email').val());
		formData.append('phoneNumber', $('#signup-phoneNumber').val());


		/* key 확인하기 */
		for (let key of formData.keys()) {
			console.log("Key = " + key);
		}

		/* value 확인하기 */
		for (let value of formData.values()) {
			console.log("value = " + value);
		}

		$.ajax({
			url: "/auth/signup",
			type: "POST",
			data: formData,
			processData: false,
			contentType: false,
			success: function (response) {
				window.location.href = '/auth/login-page';
				console.log(response)
				alert("성공적으로 회원가입이 되었습니다!")
			},
			error: function (error) {
				alert("회원가입 실패. 다시 확인해주세요.");
			}
		});
	});


	// 로그인 버튼 클릭 이벤트
	$('#signin-submit').on('click', function (event) {
		event.preventDefault();

		var username = $('#signin-username').val();
		var password = $('#signin-password').val();

		$.ajax({
			url: "/auth/login",
			type: "POST",
			contentType: "application/json",
			data: JSON.stringify({username: username, password: password}),
			success: function (response, status, xhr) {
				// HTTP 헤더에서 토큰 가져오기
				var token = xhr.getResponseHeader("Authorization");

				// 쿠키 만료일 설정
				var expirationDate = new Date();
				expirationDate.setDate(expirationDate.getDate() + 1);

				// 토큰을 쿠키에 저장
				if (token) {
					// 쿠키에 담길때 인코딩 과정에서 공백이 %20으로 변환
					Cookies.set('Authorization', token, {expires: expirationDate});
					Cookies.set('Username', username, {expires: expirationDate});
				}

				// 로그인 상태 UI 업데이트
				// "로그인" 버튼을 메시지로 교체
				$('.cd-signin').replaceWith('<li class="welcome-msg">' + username + '님 환영합니다.</li>');

				console.log(response)
				console.log(response)
				console.log("아이디 : " + username)
				console.log("비밀번호 : " + password)
				console.log("Authorization : " + token)

				alert("성공적으로 로그인 했습니다!");
			},
			error: function (error) {
				// 로그인 실패시 처리 로직
				alert("Login failed. Please check your email and password.");
			}
		});
	});

	function updateLoginStatus() {
		var token = Cookies.get('Authorization');
		if (!token) {
			// 토큰이 없으면 로그아웃 상태로 간주하고 "로그인" 버튼 표시
			$('.cd-signin').addClass('is-selected');
			$('.cd-signup').removeClass('is-selected');
			$('.welcome-msg').remove(); // 기존 메시지 제거
		} else {
			// 사용자가 로그인한 경우
			var username = Cookies.get('Username');
			if (username) {
				$('.cd-signin').replaceWith('<li class="welcome-msg">' + username + '님 환영합니다.</li>');
			}
			$('.cd-signin').removeClass('is-selected');
			$('.cd-signup').removeClass('is-selected');
		}
	}

	// 로그아웃 버튼 클릭 이벤트
	$('.cd-logout').on('click', function(event) {
		event.preventDefault();

		// 쿠키에서 토큰 가져오기
		var token = Cookies.get('Authorization');

		$.ajax({
			url: "/auth/logout",
			type: "POST",
			// ajax요청이 전송되기 직전에 헤더에 토큰을 전달
			beforeSend: function(xhr) {
				xhr.setRequestHeader("Authorization", token);
			},
			success: function(response) {
				if (response.status === 200) {  // 성공적으로 로그아웃되었을 때의 HTTP 상태 코드
					// 로그아웃 성공 메시지
					alert(response.message);

					// 토큰을 쿠키에서 삭제
					Cookies.remove('Authorization');
					Cookies.remove('Username');

					// 3초 후에 '/'로 리다이렉트
					setTimeout(function() {
						window.location.href = '/';
					}, 3000);
				} else {
					// 로그아웃 실패 메시지
					alert("로그아웃 실패: " + response.message);
				}
			},
			error: function(error) {
				// 로그아웃 요청 실패시 처리 로직
				alert("로그아웃 요청 실패: " + error.statusText);
			}
		});
	});

	function autoLogout() {
		// 토큰 제거 및 UI 업데이트
		Cookies.remove('Authorization');
		Cookies.remove('Username');

		$('.cd-signin').addClass('is-selected');
		$('.cd-signup').removeClass('is-selected');

		// 로그아웃 경고 표시 및 리다이렉션 수행
		alert("세션이 만료되었습니다. 다시 로그인해주세요.");
		window.location.href = '/auth/login-page';
	}
// 로그인 성공 후에 아래 코드 실행
	setTimeout(autoLogout, 24 * 60 * 60 * 1000);  // 24시간 후에 autoLogout 함수 실행


	// 파일 선택 input 엘리먼트 가져오기
	const fileInput = document.getElementById('signup-profileImage');

	// 선택한 이미지의 URL을 표시할 img 엘리먼트 가져오기
	const selectedImage = document.getElementById('selectedImage');

	// 파일을 선택할 때 실행되는 함수
	fileInput.addEventListener('change', function() {

		// 선택한 파일 가져오기
		const selectedFile = fileInput.files[0];

		// 선택한 파일이 있는 경우
		if (selectedFile) {
			// 선택한 파일의 URL을 img 엘리먼트의 src 속성에 설정하여 표시
			selectedImage.src = URL.createObjectURL(selectedFile);

			// img 엘리먼트 보이도록 설정
			selectedImage.style.display = 'block';
		}
	});


	// // 인증번호 전송 버튼 클릭 이벤트
	// $('#sendVerificationCode').on('click', function(event){
	// 	event.preventDefault();
	//
	// 	var phoneNumber = $signup_phoneNumber.val();
	//
	// 	$.ajax({
	// 		url: "/send/code",
	// 		type: "POST",
	// 		data: { phoneNumber: phoneNumber },
	// 		success: function(response) {
	// 			alert("인증번호가 전송되었습니다.");
	// 		},
	// 		error: function(error) {
	// 			alert("인증번호 전송 실패. 다시 시도해주세요.");
	// 		}
	// 	});
	// });
	//
	// // 인증번호 확인 버튼 클릭 이벤트
	// $('#verifyCodeButton').on('click', function(event){
	// 	event.preventDefault();
	//
	// 	var phoneNumber = $signup_phoneNumber.val();
	// 	var inputCode = $signup_phoneVerifyCode.val();
	//
	// 	console.log("Input Code:", inputCode);
	//
	// 	$.ajax({
	// 		url: "/verify/code",
	// 		type: "POST",
	// 		data: { phoneNumber: phoneNumber, inputCode: inputCode },
	// 		success: function(response) {
	// 			alert("인증 성공");
	// 		},
	// 		error: function(error) {
	// 			alert("인증 실패. 다시 시도해주세요.");
	// 		}
	// 	});
	// });


	//open modal
	$main_nav.on('click', function(event){
		// 로그인 후 '***님 환영합니다.' 메시지 클릭 방지
		if($(event.target).is('.welcome-msg')) {
			event.stopPropagation();
			return;
		}

		// 추가: 로그아웃 버튼 클릭 시 이벤트 전파 중단
		if($(event.target).is('.cd-logout')) {
			event.stopPropagation();
			return;
		}

		if( $(event.target).is($main_nav) ) {
			$(this).children('ul').toggleClass('is-visible');
		} else {
			$main_nav.children('ul').removeClass('is-visible');
			$form_modal.addClass('is-visible');
			( $(event.target).is('.cd-signup') ) ? signup_selected() : login_selected();
		}
	});

	//close modal
	$('.cd-user-modal').on('click', function(event){
		if( $(event.target).is($form_modal) || $(event.target).is('.cd-close-form') ) {
			$form_modal.removeClass('is-visible');
		}
	});
	//close modal when clicking the esc keyboard button
	$(document).keyup(function(event){
    	if(event.which=='27'){
    		$form_modal.removeClass('is-visible');
	    }
    });

	//switch from a tab to another
	$form_modal_tab.on('click', function(event) {
		event.preventDefault();
		( $(event.target).is( $tab_login ) ) ? login_selected() : signup_selected();
	});

	//hide or show password
	$('.hide-password').on('click', function(){
		var $this= $(this),
			$password_field = $this.prev('input');

		( 'password' == $password_field.attr('type') ) ? $password_field.attr('type', 'text') : $password_field.attr('type', 'password');
		( 'Hide' == $this.text() ) ? $this.text('Show') : $this.text('Hide');
		//focus and move cursor to the end of input field
		$password_field.putCursorAtEnd();
	});

	//show forgot-password form
	$forgot_password_link.on('click', function(event){
		event.preventDefault();
		forgot_password_selected();
	});

	//back to login from the forgot-password form
	$back_to_login_link.on('click', function(event){
		event.preventDefault();
		login_selected();
	});

	function login_selected(){
		$form_login.addClass('is-selected');
		$form_signup.removeClass('is-selected');
		$form_forgot_password.removeClass('is-selected');
		$tab_login.addClass('selected');
		$tab_signup.removeClass('selected');
	}

	function signup_selected(){
		$form_login.removeClass('is-selected');
		$form_signup.addClass('is-selected');
		$form_forgot_password.removeClass('is-selected');
		$tab_login.removeClass('selected');
		$tab_signup.addClass('selected');
	}

	function forgot_password_selected(){
		$form_login.removeClass('is-selected');
		$form_signup.removeClass('is-selected');
		$form_forgot_password.addClass('is-selected');
	}

	//IE9 placeholder fallback
	//credits http://www.hagenburger.net/BLOG/HTML5-Input-Placeholder-Fix-With-jQuery.html
	if(!Modernizr.input.placeholder){
		$('[placeholder]').focus(function() {
			var input = $(this);
			if (input.val() == input.attr('placeholder')) {
				input.val('');
		  	}
		}).blur(function() {
		 	var input = $(this);
		  	if (input.val() == '' || input.val() == input.attr('placeholder')) {
				input.val(input.attr('placeholder'));
		  	}
		}).blur();
		$('[placeholder]').parents('form').submit(function() {
		  	$(this).find('[placeholder]').each(function() {
				var input = $(this);
				if (input.val() == input.attr('placeholder')) {
			 		input.val('');
				}
		  	})
		});
	}

});


//credits https://css-tricks.com/snippets/jquery/move-cursor-to-end-of-textarea-or-input/
jQuery.fn.putCursorAtEnd = function() {
	return this.each(function() {
    	// If this function exists...
    	if (this.setSelectionRange) {
      		// ... then use it (Doesn't work in IE)
      		// Double the length because Opera is inconsistent about whether a carriage return is one character or two. Sigh.
      		var len = $(this).val().length * 2;
      		this.setSelectionRange(len, len);
    	} else {
    		// ... otherwise replace the contents with itself
    		// (Doesn't work in Google Chrome)
      		$(this).val($(this).val());
    	}
	});
};
