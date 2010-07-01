<% import grails.persistence.Event %>
<%=packageName%>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label', default: '${className}')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
                <h1><g:message code="default.create.label" args="[entityName]" /></h1>
                    <ul id="nav">
                        <li><a class="home" href="\${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
                        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                    </ul>

                    <g:render template="create" model="['${domainClass.propertyName}': ${domainClass.propertyName}]" />
    </body>
</html>
