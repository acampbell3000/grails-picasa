
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Photo" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="entityName" value="${message(code: 'photo.label', default: 'Photo')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
				<h1><g:message code="default.show.label" args="[entityName]" /></h1>
					<ul id="nav">
						<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
						<li><a class="list" href="${createLink(action: 'list')}/${params.albumId}"><g:message code="default.list.label" args="[entityName]" /></a></li>
					</ul>

                    <g:render template="show" model="['photoInstance': photoInstance]" />
    </body>
</html>
