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
                    <div id="create${className}">
                    <g:if test="\${flash.message}">
                        <div id="flashMessage">\${flash.message}</div>
                    </g:if>
                    <g:hasErrors bean="\${${propertyName}}">
                        <div id="errorMessage"><g:message code="error.create" args="['${className}']"
                        default="A problem was encountered when trying to process your form.
                        Please ensure that all the fields are complete and try again."/></div>
                    </g:hasErrors>
                        <g:formRemote id="form${className}" name="form${className}" action="save"
                        method="post"<%= multiPart ? ' enctype="multipart/form-data"' : '' %>
                        update="create${className}" url="[action:'ajaxSave']"
                        onLoading="displayLoading('create${className}')"
                        onLoaded="displayResponse('create${className}')">
                            <fieldset>
                                <legend><g:message code="${domainClass.propertyName}.legend" default="${className} form" /></legend>
    <%
        fieldCount = 0
        excludedProps = Event.allEvents.toList() << 'version' << 'id'
        props = domainClass.properties.findAll { !excludedProps.contains(it.name) }
        Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
        props.each { p ->
            if (!Collection.class.isAssignableFrom(p.type)) {
                cp = domainClass.constrainedProperties[p.name]
                display = (cp ? cp.display : true)
                if (display) {
                    ++fieldCount
    %>
                                <p><label for="${p.name}"><g:message code="${domainClass.propertyName}.${p.name}.label" default="${p.naturalName}" />:</label>
                                    ${renderEditor(p)} <span id="${p.name}Flash"><g:displayFieldError bean="\${${propertyName}}" field="${p.name}">\${it}</g:displayFieldError></span></p>
    <%
                }
            }
        }
    %>
                                <p><input id="submit" class="button" type="submit" name="submit" value="\${message(code: 'default.button.create.label', default: 'Create')}" />
                                   <input id="reset" class="button" type="reset" name="reset" value="\${message(code: 'default.button.reset.label', default: 'Reset')}" /></p>
                            </fieldset>
                        </g:formRemote>
                    </div>
    </body>
</html>
