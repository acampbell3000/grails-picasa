<%@ page import="uk.co.anthonycampbell.grails.picasa.Tag" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="keyword" value="${tagKeyword}" />
        <title><g:message code="uk.co.anthonycampbell.grails.picasa.Tag.details.legend" default="Tag Listing" args="[keyword]" /></title>
    </head>
    <body>
				<h1><g:message code="uk.co.anthonycampbell.grails.picasa.Tag.details.legend" default="Tag Listing" args="[keyword]" /></h1>
					<ul id="nav">
						<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
						<li><g:link class="list" controller="album" action="list"><g:message code="uk.co.anthonycampbell.grails.picasa.Album.legend" default="Photo Albums" /></g:link></li>
					</ul>
                
                    <g:render template="show" model="['photoInstanceList': photoInstanceList, 'photoInstanceTotal': photoInstanceTotal, 'tagKeyword': tagKeyword]" />
    </body>
</html>
