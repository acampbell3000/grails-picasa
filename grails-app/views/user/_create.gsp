
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.User" %>
                    <div id="createUser">
                    <g:if test="${flash.message}">
                        <div id="flashMessage">${flash.message}</div>
                    </g:if>
                    <g:hasErrors bean="${userInstance}">
                        <div id="errorMessage"><g:message code="error.create" args="['User']"
                        default="A problem was encountered when trying to process your form.
                        Please ensure that all the fields are complete and try again."/></div>
                    </g:hasErrors>
                        <g:formRemote id="formUser" name="formUser" action="save"
                        method="post"
                        update="createUser" url="[action:'ajaxSave']"
                        onLoading="displayLoading('createUser')"
                        onLoaded="displayResponse('createUser')">
                            <fieldset>
                                <legend><g:message code="user.legend" default="User form" /></legend>
    
                                <p><label for="name"><g:message code="user.name.label" default="Name" />:</label>
                                    <g:remoteText id="name" name="name" paramName="name" action="validate" update="nameFlash" value="${userInstance?.name}" /> <span id="nameFlash"><g:displayFieldError bean="${userInstance}" field="name">${it}</g:displayFieldError></span></p>
    
                                <p><label for="email"><g:message code="user.email.label" default="Email" />:</label>
                                    <g:remoteText id="email" name="email" paramName="email" action="validate" update="emailFlash" value="${userInstance?.email}" /> <span id="emailFlash"><g:displayFieldError bean="${userInstance}" field="email">${it}</g:displayFieldError></span></p>
    
                                <p><label for="uri"><g:message code="user.uri.label" default="Uri" />:</label>
                                    <g:remoteText id="uri" name="uri" paramName="uri" action="validate" update="uriFlash" value="${userInstance?.uri}" /> <span id="uriFlash"><g:displayFieldError bean="${userInstance}" field="uri">${it}</g:displayFieldError></span></p>
    
                                <p><input id="submit" class="button" type="submit" name="submit" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                                   <input id="reset" class="button" type="reset" name="reset" value="${message(code: 'default.button.reset.label', default: 'Reset')}" /></p>
                            </fieldset>
                        </g:formRemote>
                    </div>
