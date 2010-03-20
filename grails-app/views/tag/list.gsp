
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Tag" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="entityName" value="${message(code: 'tag.label', default: 'Tag')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
				<h1><g:message code="default.list.label" args="[entityName]" /></h1>
					<ul id="nav">
						<li><a class="home" href="${createLink(uri: '/')}">Home</a></li>
						<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
					</ul>
                    <div id="listTag">
					<g:if test="${flash.message}">
						<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<thead>
								<tr>
								
									<g:sortableColumn property="id" title="${message(code: 'tag.id.label', default: 'Id')}" />
								
									<g:sortableColumn property="tagId" title="${message(code: 'tag.tagId.label', default: 'Tag Id')}" />
								
									<g:sortableColumn property="keyword" title="${message(code: 'tag.keyword.label', default: 'Keyword')}" />
								
								</tr>
							</thead>
							<tbody>
							<g:each in="${tagInstanceList}" status="i" var="tagInstance">
								<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
								
									<td><g:link action="show" id="${tagInstance.id}">${fieldValue(bean: tagInstance, field: "id")}</g:link></td>
								
									<td>${fieldValue(bean: tagInstance, field: "tagId")}</td>
								
									<td>${fieldValue(bean: tagInstance, field: "keyword")}</td>
								
								</tr>
							</g:each>
							</tbody>
						</table>
						<div id="pagination">
							<g:paginate total="${tagInstanceTotal}" />
						</div>
					</div>
    </body>
</html>
