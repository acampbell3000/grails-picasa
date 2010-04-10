
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Album" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <title><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.legend" default="Photo Albums" /></title>
    </head>
    <body>
				<h1><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.legend" default="Photo Albums" /></h1>
					<ul id="nav">
						<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
						<li><g:link class="list" action="list"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.legend" default="Photo Albums" /></g:link></li>
					</ul>

                    <g:render template="list" model="['albumInstanceList': albumInstanceList, 'albumInstanceTotal': albumInstanceTotal]" />

                    <div id="listTag">
                    <g:if test="${tagInstanceList != null && tagInstanceList.size > 0}">
                        <g:each in="${tagInstanceList}" status="tagIndex" var="tagInstance">${(tagIndex > 0) ? ", " : ""}<g:link controller="tag" action="show" id="${tagInstance?.keyword}">${tagInstance?.keyword}</g:link></g:each>
                    </g:if>
                    </div>
    </body>
</html>
