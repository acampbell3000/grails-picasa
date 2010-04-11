
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Comment" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="entityName" value="${message(code: 'comment.label', default: 'Comment')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
				<h1><g:message code="default.list.label" args="[entityName]" /></h1>
					<ul id="nav">
						<li><a class="home" href="${createLink(uri: '/')}">Home</a></li>
						<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
					</ul>
                    <div id="listComment">
					<g:if test="${flash.message}">
						<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<thead>
								<tr>
								
									<g:sortableColumn property="id" title="${message(code: 'comment.id.label', default: 'Id')}" />
								
									<g:sortableColumn property="commentId" title="${message(code: 'comment.commentId.label', default: 'Comment Id')}" />
								
									<g:sortableColumn property="albumId" title="${message(code: 'comment.albumId.label', default: 'Album Id')}" />
								
									<g:sortableColumn property="photoId" title="${message(code: 'comment.photoId.label', default: 'Photo Id')}" />
								
									<g:sortableColumn property="message" title="${message(code: 'comment.message.label', default: 'Message')}" />
								
									<g:sortableColumn property="dateCreated" title="${message(code: 'comment.dateCreated.label', default: 'Date Created')}" />
								
								</tr>
							</thead>
							<tbody>
							<g:each in="${commentInstanceList}" status="i" var="commentInstance">
								<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
								
									<td><g:link action="show" id="${commentInstance.id}">${fieldValue(bean: commentInstance, field: "id")}</g:link></td>
								
									<td>${fieldValue(bean: commentInstance, field: "commentId")}</td>
								
									<td>${fieldValue(bean: commentInstance, field: "albumId")}</td>
								
									<td>${fieldValue(bean: commentInstance, field: "photoId")}</td>
								
									<td>${fieldValue(bean: commentInstance, field: "message")}</td>
								
									<td><g:formatDate date="${commentInstance.dateCreated}" /></td>
								
								</tr>
							</g:each>
							</tbody>
						</table>
						<div id="pagination">
							<g:paginate total="${commentInstanceTotal}" />
						</div>
					</div>
    </body>
</html>
