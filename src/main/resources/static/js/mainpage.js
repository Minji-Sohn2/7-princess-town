$(document).ready(function () {

    // 현재 로그인 중인 인증정보 가져오기
    const authorization = Cookies.get('Authorization');

    // 	메인페이지
    // 인기검색어 데이터를 가져와서 HTML에 렌더링하는 함수
    function renderPopularSearches(data) {
        var searchRanking = $(".search-ranking");
        searchRanking.empty(); // 기존 목록 비우기

        $.each(data, function (index, popularSearch) {
            var listItem = $("<li>").text(popularSearch.keyword);
            listItem.on("click", function () {
                // 검색어 클릭 시 검색 결과 페이지로 이동
                var keyword = encodeURIComponent(popularSearch.keyword);
                var searchType = "titleAndContents"; // 예시로 제목+내용 검색 유형 사용
                window.location.href = "/view/searchKeyword?searchType=" + searchType + "&keyword=" + keyword;
            });
            searchRanking.append(listItem);
        });
    }

    // 백엔드 API 호출하여 인기검색어 데이터 가져오기
    $.ajax({
        url: "/api/popular-searches",
        method: "GET",
        success: function (data) {
            renderPopularSearches(data);
        },
        error: function () {
            console.error("Error fetching popular searches data.");
        }
    });

    // 드롭다운 초기화
    $('.ui.dropdown').dropdown();

    // 드롭다운 선택 항목이 변경될 때 이벤트 처리
    $("#searchTypeDropdown .item").on("click", function () {
        var selectedOption = $(this).text();
        $("#searchTypeDropdown .text").text(selectedOption);
    });

    // 검색 폼 제출 시 처리
    $(".search-form").submit(function (event) {
        event.preventDefault(); // 기본 제출 동작 방지

        // 현재 선택된 드롭다운 항목 가져오기
        var searchType = $("#searchTypeDropdown .menu .item.active").text();
        var defaultType = $("#defaultitem").text();
        var searchKeyword = $("#searchKeyword").val();

        // 선택된 드롭다운 항목을 기반으로 리다이렉션 처리
        if (searchType === "제목") {
            window.location.href = "/view/searchTitle?title=" + encodeURIComponent(searchKeyword);
        } else if (searchType === "내용") {
            window.location.href = "/view/searchContent?contents=" + encodeURIComponent(searchKeyword);
        } else if (searchType === "제목+내용") {
            window.location.href = "/view/searchKeyword?keyword=" + encodeURIComponent(searchKeyword);
        } else if (defaultType === "제목") {
            window.location.href = "/view/searchTitle?title=" + encodeURIComponent(searchKeyword);
        }
    });

    if (authorization == null) {
        document.getElementById('myChatRoom-btn2').style.display = 'none';
    } else {
        document.getElementById('myChatRoom-btn2').style.display = 'block';
    }

});