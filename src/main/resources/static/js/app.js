jQuery(document).ready(function($) {
	// $form_modal은 .cd-user-modal 클래스를 가진 요소를 참조하고,
	// $form_modal이 참조하는 cd-**라는 id형태를 가진 요소를 참조할 수 있게 함
	var $form_modal = $('.cd-user-modal'),
		$form_login = $form_modal.find('#cd-login'),
		$form_signup = $form_modal.find('#cd-signup'),
		$signup_submit = $form_signup.find('#signup-submit'),
		$form_forgot_password = $form_modal.find('#cd-reset-password'),
		$form_modal_tab = $('.cd-switcher'),
		$tab_login = $form_modal_tab.children('li').eq(0).children('a'),
		$tab_signup = $form_modal_tab.children('li').eq(1).children('a'),
		$forgot_password_link = $form_login.find('.cd-form-bottom-message a'),
		$back_to_login_link = $form_forgot_password.find('.cd-form-bottom-message a'),
		$main_nav = $('.main-nav'),
		$signup_username = $form_signup.find('#signup-username'),
		$signup_email = $form_signup.find('#signup-email'),
		$signup_password = $form_signup.find('#signup-password'),
		$signup_nickname = $form_signup.find('#signup-nickname'),
		$signup_phoneNumber = $form_signup.find('#signup-phoneNumber'),
		$signup_profileImage = $form_signup.find('#signup-profileImage'),
		$signup_phoneVerifyCode = $form_signup.find('#signup-phoneVerifyCode');




	// 회원가입 버튼 클릭 이벤트
	$signup_submit.on('click', function(event) {
		event.preventDefault();

		var formData = new FormData($form_signup[0]);

		$.ajax({
			url: "/auth/signup",
			type: "POST",
			data: formData,
			processData: false,
			contentType: false,
			success: function(response) {
				window.location.href = 'auth/login-page';
			},
			error: function(error) {
				alert("회원가입 실패. 다시 확인해주세요.");
			}
		});
	});

	// 인증번호 전송 버튼 클릭 이벤트
	$('#sendVerificationCode').on('click', function(event){
		event.preventDefault();

		var phoneNumber = $signup_phoneNumber.val();

		$.ajax({
			url: "/send/code",
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
	$('#verifyCodeButton').on('click', function(event){
		event.preventDefault();

		var phoneNumber = $signup_phoneNumber.val();
		var inputCode = $signup_phoneVerifyCode.val();

		console.log("Input Code:", inputCode);

		$.ajax({
			url: "/verify/code",
			type: "POST",
			data: { phoneNumber: phoneNumber, inputCode: inputCode },
			success: function(response) {
				alert("인증 성공");
			},
			error: function(error) {
				alert("인증 실패. 다시 시도해주세요.");
			}
		});
	});


	// 로그인 버튼 클릭 이벤트
	$form_login.find('input[type="submit"]').on('click', function(event){
		event.preventDefault();

		var usenname = $signup_username.val();
		var password = $signup_password.val();

		$.ajax({
			url: "/auth/login",
			type: "POST",
			contentType: "application/json",
			data: JSON.stringify({ username: username, password: password }),
			success: function(response) {
				// 로그인 성공시 처리 로직 (예: 페이지 리디렉션)

				// 토큰을 쿠키에 저장
				Cookies.set('authToken', response.token);

				// 다른 로직 추가 (예: 메인 페이지로 리디렉션)
				window.location.href = '/';
			},
			error: function(error) {
				// 로그인 실패시 처리 로직
				alert("Login failed. Please check your email and password.");
			}
		});
	});



	//open modal
	$main_nav.on('click', function(event){

		if( $(event.target).is($main_nav) ) {
			// on mobile open the submenu
			$(this).children('ul').toggleClass('is-visible');
		} else {
			// on mobile close submenu
			$main_nav.children('ul').removeClass('is-visible');
			//show modal layer
			$form_modal.addClass('is-visible');	
			//show the selected form
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