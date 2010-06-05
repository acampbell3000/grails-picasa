
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.User" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
				<h1><g:message code="default.list.label" args="[entityName]" /></h1>
					<ul id="nav">
						<li><a class="home" href="${createLink(uri: '/')}">Home</a></li>
						<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
					</ul>

                    <g:render template="list" model="['userList': userList, 'userTotal': userTotal]" />
    </body>
</html>
