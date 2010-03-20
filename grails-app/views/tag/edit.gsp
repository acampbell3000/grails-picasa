
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Tag" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="entityName" value="${message(code: 'tag.label', default: 'Tag')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
                <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
					<ul id="nav">
						<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
						<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
						<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
					</ul>
                    <div id="editTag">
                    <g:if test="${flash.message}">
                        <div id="flashMessage">${flash.message}</div>
                    </g:if>
                    <g:hasErrors bean="${tagInstance}">
                        <div id="errorMessage"><g:message code="error.edit" args="['Tag']"
                        default="A problem was encountered when trying to process your form.
                        Please ensure that all the fields are complete and try again."/></div>
                    </g:hasErrors>
                        <g:formRemote id="formTag" name="formTag" action="update"
                        method="post"
                        update="editTag" url="[action:'ajaxUpdate']"
                        onLoading="displayLoading('editTag')"
                        onLoaded="displayResponse('editTag')">
                            <fieldset>
                                <legend><g:message code="tag.legend" default="Tag form" /></legend>
    
                                <p><label for="tagId"><g:message code="tag.tagId.label" default="Tag Id" />:</label>
                                    <g:remoteText id="tagId" name="tagId" paramName="tagId" action="validate" update="tagIdFlash" value="${tagInstance?.tagId}" /> <span id="tagIdFlash"><g:displayFieldError bean="${tagInstance}" field="tagId">${it}</g:displayFieldError></span></p>
    
                                <p><label for="keyword"><g:message code="tag.keyword.label" default="Keyword" />:</label>
                                    <g:remoteText id="keyword" name="keyword" paramName="keyword" action="validate" update="keywordFlash" maxlength="250" value="${tagInstance?.keyword}" /> <span id="keywordFlash"><g:displayFieldError bean="${tagInstance}" field="keyword">${it}</g:displayFieldError></span></p>
    
								<p><input id="submit" class="button" type="submit" name="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
								   <input id="reset" class="button" type="reset" name="reset" value="${message(code: 'default.button.reset.label', default: 'Reset')}" />
                                   <g:hiddenField name="id" value="${tagInstance?.id}" />
                                   <g:hiddenField name="version" value="${tagInstance?.version}" /></p>
                            </fieldset>
                        </g:formRemote>
                    </div>
    </body>
</html>
