
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Comment" %>

<g:oauthLink consumer="google"
             returnTo="[controller: 'user', action: 'oauthComplete']">Authorize</g:oauthLink>

                    <div id="createComment">
                    <g:if test="${flash.message}">
                        <div id="flashMessage">${flash.message}</div>
                    </g:if>
                    <g:hasErrors bean="${commentInstance}">
                        <div id="errorMessage"><g:message code="error.create" args="['Comment']"
                        default="A problem was encountered when trying to process your form.
                        Please ensure that all the fields are complete and try again."/></div>
                    </g:hasErrors>
                        <g:formRemote id="formComment" name="formComment" controller="comment" action="save"
                        method="post"
                        update="createComment" url="[controller: 'comment', action:'ajaxSave']"
                        onLoading="displayLoading('createComment')"
                        onLoaded="displayResponse('createComment')">
                            <fieldset>
                                <legend><g:message code="comment.legend" default="Comment form" /></legend>
    
                                <p><label for="message"><g:message code="comment.message.label" default="Message" />:</label>
                                    <g:remoteArea id="message" name="message" paramName="message" action="validate" update="messageFlash" cols="40" rows="5" value="${commentInstance?.message}" /> <span id="messageFlash"><g:displayFieldError bean="${commentInstance}" field="message">${it}</g:displayFieldError></span></p>
    
                                <p><input id="submit" class="button" type="submit" name="submit" value="${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Comment.button.create', default: 'Post comment')}" /></p>
                            </fieldset>
                        </g:formRemote>
                    </div>
