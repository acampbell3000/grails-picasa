
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Photo" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="entityName" value="${message(code: 'photo.label', default: 'Photo')}" />
        <title><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.legend" default="Photo Listing" /></title>
    </head>
    <body>
				<h1><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.legend" default="Photo Listing" /></h1>
					<ul id="nav">
						<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
						<li><g:link class="list" controller="album" action="list"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.legend" default="Photo Albums" /></g:link></li>
					</ul>
                
                    <g:render template="/photo/list" model="['photoInstanceList': photoInstanceList, 'photoInstanceTotal': photoInstanceTotal, 'albumId': albumId]" />

                    <div id="listTag">
                        <g:each in="${tagInstanceList}" status="tagIndex" var="tagInstance">${(tagIndex > 0) ? ", " : ""}<g:link controller="tag" action="show" id="${tagInstance?.keyword}">${tagInstance?.keyword}</g:link></g:each>
                    </div>
    </body>
</html>
