
<%@ page import="uk.co.anthonycampbell.grails.picasa.Photo" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <title><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.details.legend" default="Photo Details" /></title>
    </head>
    <body>
				<h1><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.details.legend" default="Photo Details" /></h1>
					<ul id="nav">
						<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
						<li><a class="list" href="${createLink(action: 'list')}/${params.albumId}"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.legend" default="Photo Listing" /></a></li>
					</ul>

                    <g:render template="show" model="['photoInstance': photoInstance]" />
    </body>
</html>
