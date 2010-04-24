
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Comment" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="entityName" value="${message(code: 'comment.label', default: 'Comment')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
                <h1><g:message code="default.create.label" args="[entityName]" /></h1>
                    <ul id="nav">
                        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
                        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                    </ul>
                    <div id="createComment">
                    <g:if test="${flash.message}">
                        <div id="flashMessage">${flash.message}</div>
                    </g:if>
                    <g:hasErrors bean="${commentInstance}">
                        <div id="errorMessage"><g:message code="error.create" args="['Comment']"
                        default="A problem was encountered when trying to process your form.
                        Please ensure that all the fields are complete and try again."/></div>
                    </g:hasErrors>
                        <g:formRemote id="formComment" name="formComment" action="save"
                        method="post"
                        update="createComment" url="[action:'ajaxSave']"
                        onLoading="displayLoading('createComment')"
                        onLoaded="displayResponse('createComment')">
                            <fieldset>
                                <legend><g:message code="comment.legend" default="Comment form" /></legend>
    
                                <p><label for="commentId"><g:message code="comment.commentId.label" default="Comment Id" />:</label>
                                    <g:remoteText id="commentId" name="commentId" paramName="commentId" action="validate" update="commentIdFlash" value="${commentInstance?.commentId}" /> <span id="commentIdFlash"><g:displayFieldError bean="${commentInstance}" field="commentId">${it}</g:displayFieldError></span></p>
    
                                <p><label for="albumId"><g:message code="comment.albumId.label" default="Album Id" />:</label>
                                    <g:remoteText id="albumId" name="albumId" paramName="albumId" action="validate" update="albumIdFlash" value="${commentInstance?.albumId}" /> <span id="albumIdFlash"><g:displayFieldError bean="${commentInstance}" field="albumId">${it}</g:displayFieldError></span></p>
    
                                <p><label for="photoId"><g:message code="comment.photoId.label" default="Photo Id" />:</label>
                                    <g:remoteText id="photoId" name="photoId" paramName="photoId" action="validate" update="photoIdFlash" value="${commentInstance?.photoId}" /> <span id="photoIdFlash"><g:displayFieldError bean="${commentInstance}" field="photoId">${it}</g:displayFieldError></span></p>
    
                                <p><label for="message"><g:message code="comment.message.label" default="Message" />:</label>
                                    <g:remoteArea id="message" name="message" paramName="message" action="validate" update="messageFlash" cols="40" rows="5" value="${commentInstance?.message}" /> <span id="messageFlash"><g:displayFieldError bean="${commentInstance}" field="message">${it}</g:displayFieldError></span></p>
    
                                <p><label for="dateCreated"><g:message code="comment.dateCreated.label" default="Date Created" />:</label>
                                    <g:datePicker name="dateCreated" precision="day" value="${commentInstance?.dateCreated}"  /> <span id="dateCreatedFlash"><g:displayFieldError bean="${commentInstance}" field="dateCreated">${it}</g:displayFieldError></span></p>
    
                                <p><input id="submit" class="button" type="submit" name="submit" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                                   <input id="reset" class="button" type="reset" name="reset" value="${message(code: 'default.button.reset.label', default: 'Reset')}" /></p>
                            </fieldset>
                        </g:formRemote>
                    </div>
    </body>
</html>