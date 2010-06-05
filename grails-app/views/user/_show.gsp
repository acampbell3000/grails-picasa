
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.User" %>
                    <div id="showUser">
					<g:if test="${flash.message}">
    					<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<tbody>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="user.id.label" default="Id" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: userInstance, field: "id")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="user.name.label" default="Name" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: userInstance, field: "name")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="user.email.label" default="Email" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: userInstance, field: "email")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="user.uri.label" default="Uri" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: userInstance, field: "uri")}</td>
									
								</tr>
							
							</tbody>
						</table>
						<g:form>
							<p><g:actionSubmit class="button" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" />
							   <g:actionSubmit class="button" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
							   <g:hiddenField name="id" value="${userInstance?.id}" /></p>
						</g:form>
					 </div>
