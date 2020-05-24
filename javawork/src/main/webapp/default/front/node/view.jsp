<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../layout/header.jsp" %>
<%--ljy美化页面样式--%>
<style>
.panelheadstyle{
	/*background-color:#228fbd;*/
	background-color:#fff;
	border-bottom:2px solid #ccc;
}
.node_header_tabs a{
	color:#000 !important;
	display:inline-block;
	line-height:100%;
	padding:5px 10px;
	border-bottom:3px none #228fbd;
	text-decoration:none;
}
.node_header_tabs a:hover{
	color:#000 !important;
	display:inline-block;
	line-height:100%;
	padding:5px 10px;
	border-bottom:3px solid #228fbd;
	text-decoration:none;
}
.node_header_tabs a:visited{
	color:#000 !important;
	display:inline-block;
	line-height:100%;
	padding:5px 10px;
	border-bottom:3px solid #228fbd;
	text-decoration:none;
}
.node_header_tabs a:active{
	color:#000 !important;
	display:inline-block;
	line-height:100%;
	padding:5px 10px;
	border-bottom:3px solid #228fbd;
	text-decoration:none;
}
.node_header_tab_current:link{
	color:#000 !important;
	display:inline-block;
	line-height:100%;
	padding:5px 10px;
	border-bottom:3px solid #228fbd;
	text-decoration:none;
} 
.node_header_tab_current:visited{
	color:#000 !important;
	display:inline-block;
	line-height:100%;
	padding:5px 10px;
	border-bottom:3px solid #228fbd;
	text-decoration:none;
}
.omit
{
overflow:hidden; /*自动隐藏文字 */
text-overflow: ellipsis; /*文字隐藏后添加省略号 */
white-space:nowrap; /*强制不换行 */
}
.dist
{
display:inline-block;
margin-bottom:5px;
white-space:nowrap; /*强制不换行 */
}
</style>
<div class="row">
    <div class="col-md-9">
        <div class="panel panel-default">
            <div class="node_header panelheadstyle">
                <div class="node_avatar">
                    <div style="float: left; display: inline-block; margin-right: 10px; margin-bottom: initial!important;">
                        <img src="${node.avatarNormal}" border="0" align="default" width="72">
                    </div>
                </div>
                <div class="node_info" id="node_info" style="color:#000;" >
                    <div class="fr f12"><span>主题总数</span> <strong>${countTopicByNode}</strong></div>
                    <a href="/" style="color:#000;">主页</a> <span class="chevron">&nbsp;›&nbsp;</span> ${node.nodeTitle}
                    <div class="sep10"></div>
                    <div class="sep5"></div>
                    <span class="f12" style="color:#000">${node.nodeDesc}</span>
                    <div class="sep10"></div>
                    <div class="node_header_tabs" id="node_header_tabs" style="color:#000">
                        <c:if test="${fn:length(nodeTabList) > 0}">
                            <c:forEach var="item" items="${nodeTabList}" varStatus="status">
                                <a href="${node.url}?s=${item.nodeTabCode}" id="${item.nodeTabCode}"
                                   class="node_header_tab">${item.nodeTabTitle}</a>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>
            </div>
            <div class="panel-body paginate-bot" style="color:#fff;">
                <c:forEach var="item" items="${page.list}">
                    <div class="media">
                        <c:if test="${fn:length(item.avatar) > 0}">
                            <div class="media-left">
                                <img src="${item.avatar}" class="avatar img-circle" alt="">
                            </div>
                        </c:if>
                        <div class="media-body">
                            <div class="title omit">
                                <c:choose>
                                    <c:when test="${item.url != null}">
                                        <a href="${item.url}" target="_blank">${item.title}</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="/topic/${item.topicId}">${item.title}</a>
                                    </c:otherwise>
                                </c:choose>&nbsp;
                                <span style="font-size:16px;"><i class="glyphicon glyphicon-thumbs-up"></i>&nbsp;${item.postGoodCount}</span>
                            </div>
                            <div class="tip">
                                <p class="gray" style="font-size:12.5px;">
                                    <c:if test="${item.top}">
                                        <span class="label label-primary dist" style="background-color:#3f51b5eb;">置顶</span> <span>•</span>
                                    </c:if>
                                    <c:if test="${item.good}">
                                        <span class="label label-success dist" style="background-color:#ff5722e6;">精华<i class="glyphicon glyphicon-fire"></i></span> <span>•</span>
                                    </c:if>
                                    <span class="dist"><a href="/n/${item.nodeTitle}" class="node">${item.nodeTitle}</a></span>
                                    <span class="dist">•</span>
                                    <span class="dist"><a href="/user/${item.author}" style="font-size:17px;">${item.author}</a></span>
                                    <span>•</span>
                                    <span class="dist"><fmt:formatDate type="date"
                                                          value="${item.createDate}"/></span>
                                </p>
                            </div>
                        </div>
                        <div class="media-body">
                        	<p style="font-size:15px;float:right;line-height:15px;">
                               	<c:if test="${item.viewCount >= 0}">
                                  		<!--<span class="hidden-sm hidden-xs">•</span>  -->
                                   	<span class="hidden-sm hidden-xs" style="text-align:center;display:inline-block;padding:12px;"><span style="font-size:29px;">${item.viewCount}</span><small>浏览</small></span>
                               	</c:if>
                                <!-- 评论数量 -->
                               	<c:if test="${item.replyCount >= 0}">
                                   	<span class="hidden-sm hidden-xs"><a
                                           href="/topic/${item.topicId}" style="text-align:center;display:inline-block;background:#337ab7;color:white;padding:12px;"><span style="font-size:29px;color:white;">${item.replyCount}</span><small>评论</small></a></span>
                               	</c:if>
                             </p>
                        </div>
                        <%-- <div class="media-right">
                        	<span class="badge badge-default">
                        		<a href="/topic/${item.topicId}">${item.replyCount}</a>
                        	</span>
                        </div> --%>
                        <div class="divide mar-top-5"></div>
                    </div>
                </c:forEach>
            </div>
            <div class="panel-footer" id="paginate"></div>
        </div>
    </div>
    <div class="col-md-3 hidden-sm hidden-xs">
        <div class="panel panel-default" id="session">
            <%--<div class="panel-body" id="nologin">
                <h5>n/${node.nodeTitle}</h5>
                <p>${node.nodeDesc}</p>
                <p><a href="/topic/create?n=${node.nodeTitle}" style="font-size: 14px;">
                    <button class="btn btn-success">发布话题</button>
                </a>
                </p>
            </div>--%>
        </div>
        <!-- 相邻节点开始 -->
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="row">
                    <c:if test="${parentNode != null}">
                        <div class="cell" style="border-bottom: 0px solid #e2e2e2;">
                            <strong class="gray">父节点</strong>
                            <div class="sep10"></div>
                            <img src="${parentNode.avatarMini}" border="0" align="absmiddle" width="24">&nbsp;
                            <a href="${parentNode.url}">${parentNode.nodeTitle}</a>
                        </div>
                    </c:if>
                    <c:if test="${fn:length(adjacencyNode) > 0}">
                        <div class="cell" style="border-bottom: 0px solid #e2e2e2;border-top: 1px solid #e2e2e2;">
                            <strong class="gray">相关分类</strong>
                            <c:forEach var="item" items="${adjacencyNode}" varStatus="status">
                                <div class="sep10"></div>
                                <img src="${item.avatarMini}" border="0" align="absmiddle" width="24">&nbsp;
                                <a href="${item.url}">${item.nodeTitle}</a>
                                <div class="sep10"></div>
                            </c:forEach>
                        </div>
                    </c:if>
                    <c:if test="${fn:length(childrenNode) > 0}">
                        <div class="cell" style="border-bottom: 0px solid #e2e2e2;border-top: 1px solid #e2e2e2;">
                            <strong class="gray">子节点</strong>
                            <c:forEach var="item" items="${childrenNode}" varStatus="status">
                                <div class="sep10"></div>
                                <img src="${item.avatarMini}" border="0" align="absmiddle" width="24">&nbsp;
                                <a href="${item.url}">${item.nodeTitle}</a>
                                <div class="sep10"></div>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
        <!-- 相邻节点结束 -->
    </div>
</div>
<script src="/default/front/node/js/changeSectionClass.js"></script>
<script type="text/javascript">
    var nodeTitle = "${node.nodeTitle}";//节点名称
    var nodeCode = "${node.nodeCode}";//节点编码
    var nodeURL = "${node.url}";//节点url
    var avatarLarge = "${node.avatarLarge}";//节点背景
    $(".wrapper").css({"background-image": "url(" + avatarLarge + ")"});
    // console.log(avatarLarge)
    var nodeTabCode = "${nodeTab}";//节点板块
    var count = ${page.totalRow};//数据总量
    var limit = ${page.pageSize};//每页显示的条数
    var url = nodeURL + "?s=" + nodeTabCode + "&p=";//url
    function page() {
        var page = location.search.match(/p=(\d+)/);
        return page ? page[1] : 1;
    }

    var p = page();//当前页数
    paginate(count, limit, p, url);
</script>
<%@ include file="../layout/footer.jsp" %>