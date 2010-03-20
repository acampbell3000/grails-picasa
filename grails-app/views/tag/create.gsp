
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Tag" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="entityName" value="${message(code: 'tag.label', default: 'Tag')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
                <h1><g:message code="default.create.label" args="[entityName]" /></h1>
                    <ul id="nav">
                        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
                        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                    </ul>
                    <div id="createTag">
                    <g:if test="${flash.message}">
                        <div id="flashMessage">${flash.message}</div>
                    </g:if>
                    <g:hasErrors bean="${tagInstance}">
                        <div id="errorMessage"><g:message code="error.create" args="['Tag']"
                        default="A problem was encountered when trying to process your form.
                        Please ensure that all the fields are complete and try again."/></div>
                    </g:hasErrors>
                        <g:formRemote id="formTag" name="formTag" action="save"
                        method="post"
                        update="createTag" url="[action:'ajaxSave']"
                        onLoading="displayLoading('createTag')"
                        onLoaded="displayResponse('createTag')">
                            <fieldset>
                                <legend><g:message code="tag.legend" default="Tag form" /></legend>
    
                                <p><label for="tagId"><g:message code="tag.tagId.label" default="Tag Id" />:</label>
                                    <g:remoteText id="tagId" name="tagId" paramName="tagId" action="validate" update="tagIdFlash" value="${tagInstance?.tagId}" /> <span id="tagIdFlash"><g:displayFieldError bean="${tagInstance}" field="tagId">${it}</g:displayFieldError></span></p>
    
                                <p><label for="keyword"><g:message code="tag.keyword.label" default="Keyword" />:</label>
                                    <g:remoteText id="keyword" name="keyword" paramName="keyword" action="validate" update="keywordFlash" maxlength="250" value="${tagInstance?.keyword}" /> <span id="keywordFlash"><g:displayFieldError bean="${tagInstance}" field="keyword">${it}</g:displayFieldError></span></p>
    
                                <p><input id="submit" class="button" type="submit" name="submit" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                                   <input id="reset" class="button" type="reset" name="reset" value="${message(code: 'default.button.reset.label', default: 'Reset')}" /></p>
                            </fieldset>
                        </g:formRemote>
                    </div>
    </body>
</html>
