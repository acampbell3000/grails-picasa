<% import grails.persistence.Event %>
<%=packageName%>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label', default: '${className}')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
                <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
					<ul id="nav">
						<li><a class="home" href="\${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
						<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
						<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
					</ul>
                    <div id="edit${className}">
                    <g:if test="\${flash.message}">
                        <div id="flashMessage">\${flash.message}</div>
                    </g:if>
                    <g:hasErrors bean="\${${propertyName}}">
                        <div id="errorMessage"><g:message code="error.edit" args="['${className}']"
                        default="A problem was encountered when trying to process your form.
                        Please ensure that all the fields are complete and try again."/></div>
                    </g:hasErrors>
                        <g:formRemote id="form${className}" name="form${className}" action="update"
                        method="post"<%= multiPart ? ' enctype="multipart/form-data"' : '' %>
                        update="edit${className}" url="[action:'ajaxUpdate']"
                        onLoading="displayLoading('edit${className}')"
                        onLoaded="displayResponse('edit${className}')">
                            <fieldset>
                                <legend><g:message code="${domainClass.propertyName}.legend" default="${className} form" /></legend>
    <%
        fieldCount = 0
        excludedProps = Event.allEvents.toList() << 'version' << 'id'
        props = domainClass.properties.findAll { !excludedProps.contains(it.name) }
        Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
        props.each { p ->
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
    %>
								<p><input id="submit" class="button" type="submit" name="submit" value="\${message(code: 'default.button.update.label', default: 'Update')}" />
								   <input id="reset" class="button" type="reset" name="reset" value="\${message(code: 'default.button.reset.label', default: 'Reset')}" />
                                   <g:hiddenField name="id" value="\${${propertyName}?.id}" />
                                   <g:hiddenField name="version" value="\${${propertyName}?.version}" /></p>
                            </fieldset>
                        </g:formRemote>
                    </div>
    </body>
</html>
