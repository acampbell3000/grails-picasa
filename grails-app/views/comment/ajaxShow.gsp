
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Comment" %>
                    <div id="showComment">
					<g:if test="${flash.message}">
    					<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<tbody>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="comment.id.label" default="Id" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: commentInstance, field: "id")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="comment.commentId.label" default="Comment Id" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: commentInstance, field: "commentId")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="comment.albumId.label" default="Album Id" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: commentInstance, field: "albumId")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="comment.photoId.label" default="Photo Id" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: commentInstance, field: "photoId")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="comment.message.label" default="Message" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: commentInstance, field: "message")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="comment.dateCreated.label" default="Date Created" /></td>
									
									<td valign="top" class="value"><g:formatDate date="${commentInstance?.dateCreated}" /></td>
									
								</tr>
							
							</tbody>
						</table>
						<g:form>
							<p><g:actionSubmit class="button" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" />
							   <g:actionSubmit class="button" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
							   <g:hiddenField name="id" value="${commentInstance?.id}" /></p>
						</g:form>
					 </div>
