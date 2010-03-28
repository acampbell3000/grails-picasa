
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Album" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <title><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.details.legend" default="Album Details" /></title>
    </head>
    <body>
				<h1><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.details.legend" default="Album Details" /></h1>
					<ul id="nav">
						<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
						<li><g:link class="list" action="list"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.legend" default="Photo Albums" /></g:link></li>
					</ul>

                    <g:render template="show" model="['albumInstance': albumInstance]" />
    </body>
</html>
