
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Tag" %>
<html>
    <head>
        <meta name="layout" content="remote-forms" />
        <g:set var="entityName" value="${message(code: 'tag.label', default: 'Tag')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
				<h1><g:message code="default.show.label" args="[entityName]" /></h1>
					<ul id="nav">
						<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" default="Home" /></a></li>
						<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
						<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
					</ul>
                    <div id="showTag">
					<g:if test="${flash.message}">
    					<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<tbody>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="tag.id.label" default="Id" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: tagInstance, field: "id")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="tag.tagId.label" default="Tag Id" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: tagInstance, field: "tagId")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="tag.keyword.label" default="Keyword" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: tagInstance, field: "keyword")}</td>
									
								</tr>
							
							</tbody>
						</table>
						<g:form>
							<p><g:actionSubmit class="button" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" />
							   <g:actionSubmit class="button" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
							   <g:hiddenField name="id" value="${tagInstance?.id}" /></p>
						</g:form>
					 </div>
    </body>
</html>
