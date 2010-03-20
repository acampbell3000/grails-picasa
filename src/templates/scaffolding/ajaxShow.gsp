<% import grails.persistence.Event %>
<%=packageName%>
                    <div id="show${className}">
					<g:if test="\${flash.message}">
    					<div id="flashMessage">\${flash.message}</div>
					</g:if>
						<table>
							<tbody>
							<%
								excludedProps = Event.allEvents.toList() << 'version'
								props = domainClass.properties.findAll { !excludedProps.contains(it.name) }
								Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
								props.each { p ->
							%>
								<tr class="prop">
									<td valign="top" class="name"><g:message code="${domainClass.propertyName}.${p.name}.label" default="${p.naturalName}" /></td>
									<%  if (p.isEnum()) { %>
									<td valign="top" class="value">\${${propertyName}?.${p.name}?.encodeAsHTML()}</td>
									<%  } else if (p.oneToMany || p.manyToMany) { %>
									<td valign="top" style="text-align: left;" class="value">
										<ul>
										<g:each in="\${${propertyName}.${p.name}}" var="${p.name[0]}">
											<li><g:link controller="${p.referencedDomainClass?.propertyName}" action="show" id="\${${p.name[0]}.id}">\${${p.name[0]}?.encodeAsHTML()}</g:link></li>
										</g:each>
										</ul>
									</td>
									<%  } else if (p.manyToOne || p.oneToOne) { %>
									<td valign="top" class="value"><g:link controller="${p.referencedDomainClass?.propertyName}" action="show" id="\${${propertyName}?.${p.name}?.id}">\${${propertyName}?.${p.name}?.encodeAsHTML()}</g:link></td>
									<%  } else if (p.type == Boolean.class || p.type == boolean.class) { %>
									<td valign="top" class="value"><g:formatBoolean boolean="\${${propertyName}?.${p.name}}" /></td>
									<%  } else if (p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class) { %>
									<td valign="top" class="value"><g:formatDate date="\${${propertyName}?.${p.name}}" /></td>
									<%  } else { %>
									<td valign="top" class="value">\${fieldValue(bean: ${propertyName}, field: "${p.name}")}</td>
									<%  } %>
								</tr>
							<%  } %>
							</tbody>
						</table>
						<g:form>
							<p><g:actionSubmit class="button" action="edit" value="\${message(code: 'default.button.edit.label', default: 'Edit')}" />
							   <g:actionSubmit class="button" action="delete" value="\${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
							   <g:hiddenField name="id" value="\${${propertyName}?.id}" /></p>
						</g:form>
					 </div>
