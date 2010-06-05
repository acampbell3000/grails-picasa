<% import grails.persistence.Event %>
<%=packageName%>
                    <div id="list${className}">
					<g:if test="\${flash.message}">
						<div id="flashMessage">\${flash.message}</div>
					</g:if>
						<table>
							<thead>
								<tr>
								<%  excludedProps = Event.allEvents.toList() << 'version'
									props = domainClass.properties.findAll { !excludedProps.contains(it.name) && it.type != Set.class }
									Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
									props.eachWithIndex { p, i ->
										if (i < 6) {
											if (p.isAssociation()) { %>
									<th><g:message code="${domainClass.propertyName}.${p.name}.label" default="${p.naturalName}" /></th>
								<%      } else { %>
									<g:remoteSortableColumn action="ajaxList" update="list${className}" property="${p.name}" title="\${message(code: '${domainClass.propertyName}.${p.name}.label', default: '${p.naturalName}')}" />
								<%  }   }   } %>
								</tr>
							</thead>
							<tbody>
							<g:each in="\${${propertyName}List}" status="i" var="${propertyName}">
								<tr class="\${(i % 2) == 0 ? 'odd' : 'even'}">
								<%  props.eachWithIndex { p, i ->
										cp = domainClass.constrainedProperties[p.name]
										if (i == 0) { %>
									<td><g:link action="show" id="\${${propertyName}.id}">\${fieldValue(bean: ${propertyName}, field: "${p.name}")}</g:link></td>
								<%      } else if (i < 6) {
											if (p.type == Boolean.class || p.type == boolean.class) { %>
									<td><g:formatBoolean boolean="\${${propertyName}.${p.name}}" /></td>
								<%          } else if (p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class) { %>
									<td><g:formatDate date="\${${propertyName}.${p.name}}" /></td>
								<%          } else { %>
									<td>\${fieldValue(bean: ${propertyName}, field: "${p.name}")}</td>
								<%  }   }   } %>
								</tr>
							</g:each>
							</tbody>
						</table>
						<div id="pagination">
                            <g:remotePaginate action="ajaxList" update="list${className}" max="10" maxsteps="10" total="\${${propertyName}Total}" />
						</div>
					</div>
