$(document).ready(function () {
    // 검색 폼 제출 시 처리
    $(".search-form").submit(function (event) {
        event.preventDefault(); // 기본 제출 동작 방지

        var searchType = $("#searchType").val();
        var searchKeyword = $("#searchKeyword").val();
        console.log('searchKeyword는? -> ' + searchKeyword);

        if (searchType === "title") {
            window.location.href = "/view/searchTitle?title=" + encodeURIComponent(searchKeyword);
        } else if (searchType === "contents") {
            window.location.href = "/view/searchContent?contents=" + encodeURIComponent(searchKeyword);
        } else if (searchType === "titleAndContents") {
            window.location.href = "/view/searchKeyword?keyword=" + encodeURIComponent(searchKeyword);
        }
    });

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
});