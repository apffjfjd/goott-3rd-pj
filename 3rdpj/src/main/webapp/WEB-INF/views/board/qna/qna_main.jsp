<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.3.0/css/all.min.css"/>
    <link rel="stylesheet" href="/resources/css/common/style.css"/>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<title>Insert title here</title>
</head>
<body>
<%@ include file="/WEB-INF/views/common/layout.jsp" %>
<main class="qna">
      <hgroup class="qna__title">
        <h1>Q&A</h1>
      </hgroup>
      <section class="qna__grid">
        <article class="qna__card">
          <hgroup class="qna__card--title">
            <h1>공지사항</h1>
          </hgroup>
          <ul class="qna__card--list">
            <c:forEach items="${data_n}" var="list" varStatus="status">
            	<c:if test="${status.count <= 5}">
            		<li><input type="hidden" name="qna_idx" value="${list.qna_idx}"></li>
            		<li><a href="./qna_notice.html">${list.qna_title}</a></li>
            	</c:if>
            </c:forEach>
        		<li><a href="/qna/list_n">더보기</a></li>
          </ul>
        </article>
        <article class="qna__card">
          <hgroup class="qna__card--title">
            <h1>관광지 문의</h1>
          </hgroup>
          <ul class="qna__card--list">
            <c:forEach items="${data_u}" var="list" varStatus="status">
            	<c:if test="${status.count <= 5}">
            		<li><input type="hidden" name="qna_idx" value="${list.qna_idx}"></li>
            		<li><a href="./qna_notice.html">${list.qna_title}</a></li>
            	</c:if>
            </c:forEach>
        		<li><a href="/qna/list_u">더보기</a></li>
          </ul>
        </article>
        <article class="qna__card">
          <hgroup class="qna__card--title">
            <h1>여행 문의</h1>
          </hgroup>
          <ul class="qna__card--list">
            <c:forEach items="${data_r}" var="list" varStatus="status">
            	<c:if test="${status.count <= 5}">
            		<li><input type="hidden" name="qna_idx" value="${list.qna_idx}"></li>
            		<li><a href="./qna_notice.html">${list.qna_title}</a></li>
            	</c:if>
            </c:forEach>
        		<li><a href="/qna/list_r">더보기</a></li>
          </ul>
        </article>
        <article class="qna__card">
          <hgroup class="qna__card--title">
            <h1>플래너 문의</h1>
          </hgroup>
          <ul class="qna__card--list">
            <c:forEach items="${data_e}" var="list" varStatus="status">
            	<c:if test="${status.count <= 5}">
            		<li><input type="hidden" name="qna_idx" value="${list.qna_idx}"></li>
            		<li><a href="./qna_notice.html">${list.qna_title}</a></li>
            	</c:if>
            </c:forEach>
        		<li><a href="/qna/list_e">더보기</a></li>
          </ul>
        </article>
      </section>
      <dialog class="qna__guide">
        <hgroup class="qna__guide--title">
          <h1>도움이 더 필요하신가요?</h1>
        </hgroup>
        <article class="qna__guide--list">
          <a href="">- 이용가이드 바로가기</a><br />
          <a href="">- Q&A 문의글 작성</a>
        </article>
        <button class="qna__guide--btn-close">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </dialog>
    </main>
    
	<script src="/resources/js/common/layout.js"></script>
	<script src="/resources/js/common/qna_main.js"></script>
    <script>
        $(function() {
            $('.title').click(function(e) {
                const idx = e.target.parentElement.children[0].children[0].value
                location.href='/qna/detail/'+idx
            })
        })
    </script>
</body>
</html>