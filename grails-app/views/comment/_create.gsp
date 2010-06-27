<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Comment"
         import="uk.co.anthonycampbell.grails.plugins.picasa.CommentController" %>
                        <div id="createComment">
                        <g:if test="${flash.oauthError}">
                            <div id="flashMessage">${flash.oauthError}</div>
                        </g:if>

                        <g:if test="${session.oAuthLoggedIn}">
                        <g:hasErrors bean="${commentInstance}">
                            <div id="errorMessage"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Comment.error.post"
                            default="A problem was encountered when trying to post your comment.
                            Please ensure that all the fields are complete and try again." /></div>
                        </g:hasErrors>
                            <g:formRemote id="formComment" name="formComment" controller="comment" action="save"
                            method="post" update="comments" url="[controller: 'comment', action: 'ajaxSave']"
                            onLoading="displayLoading('comments')"
                            onLoaded="displayResponse('comments')">
                                <fieldset>
                                    <legend><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Comment.label"
                                                       default="Comment form" /></legend>
                                    
                                    <p><strong>${session.oAuthNickname}</strong> (<g:remoteLink id="commentLogout"
                                        name="commentLogout" controller="comment"
                                        action="logout" method="post" update="createComment"
                                        url="[controller: 'comment', action: 'ajaxLogout', id: albumId + CommentController.ID_SEPARATOR + photoId]"><g:message
                                            code="uk.co.anthonycampbell.grails.plugins.picasa.oauth.logout.link"
                                            default="Logout" /></g:remoteLink>)</p>

                                    <p><label for="message"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Comment.message.label"
                                                                       default="Message" />:</label>
                                        <g:remoteArea id="message" name="message" paramName="message"
                                            action="validate" update="messageFlash" cols="40" rows="5"
                                            value="${commentInstance?.message}" />
                                        <span id="messageFlash"><g:displayFieldError
                                            bean="${commentInstance}"
                                            field="message">${it}</g:displayFieldError></span></p>

                                    <p><input id="submit" class="button" type="submit" name="submit"
                                        value="${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Comment.button.create', default: 'Post comment')}" />
                                    <input id="commentId" type="hidden" name="commentId" value="-1" />
                                    <input id="albumId" type="hidden" name="albumId" value="${albumId}" />
                                    <input id="photoId" type="hidden" name="photoId" value="${photoId}" /></p>
                                </fieldset>
                            </g:formRemote>
                        </g:if>
                        <g:else>
                            <p><g:oauthLink consumer="picasa"
                                returnTo="[controller: 'comment', action: 'login',
                                    id: albumId + CommentController.ID_SEPARATOR + photoId]">${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.oauth.login.link', default: 'Login')}</g:oauthLink>
                                ${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.oauth.login',
                                    default: 'to your Google Picasa Web Albums account to post a comment.')}</p>
                        </g:else>
                    </div>
