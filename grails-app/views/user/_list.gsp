
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.User" %>
                    <div id="listUser">
					<g:if test="${flash.message}">
						<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<thead>
								<tr>
								
									<g:remoteSortableColumn action="ajaxList" update="listUser" property="id" title="${message(code: 'user.id.label', default: 'Id')}" />
								
									<g:remoteSortableColumn action="ajaxList" update="listUser" property="name" title="${message(code: 'user.name.label', default: 'Name')}" />
								
									<g:remoteSortableColumn action="ajaxList" update="listUser" property="email" title="${message(code: 'user.email.label', default: 'Email')}" />
								
									<g:remoteSortableColumn action="ajaxList" update="listUser" property="uri" title="${message(code: 'user.uri.label', default: 'Uri')}" />
								
								</tr>
							</thead>
							<tbody>
							<g:each in="${userInstanceList}" status="i" var="userInstance">
								<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
								
									<td><g:link action="show" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "id")}</g:link></td>
								
									<td>${fieldValue(bean: userInstance, field: "name")}</td>
								
									<td>${fieldValue(bean: userInstance, field: "email")}</td>
								
									<td>${fieldValue(bean: userInstance, field: "uri")}</td>
								
								</tr>
							</g:each>
							</tbody>
						</table>
						<div id="pagination">
                            <g:remotePaginate action="ajaxList" update="listUser" max="10" maxsteps="10" total="${userInstanceTotal}" />
						</div>
					</div>
