
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Album" %>
                    <div id="listAlbum">
					<g:if test="${flash.message}">
						<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<thead>
								<tr>
                                    <th><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.albumId.label" default="Album ID" /></th>
								
									<g:sortableColumn property="name" title="${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Album.name.label', default: 'Name')}" />
								
									<g:sortableColumn property="description" title="${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Album.description.label', default: 'Description')}" />
                                    
                                    <th><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.image.label" default="Image" /></th>

									<g:sortableColumn property="location" title="${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Album.location.label', default: 'Location')}" />

									<th><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.geoLocation.label" default="Geo Location" /></th>

									<g:sortableColumn property="dateCreated" title="${message(code: 'uk.co.anthonycampbell.grails.plugins.picasa.Album.dateCareated.label', default: 'Date Created')}" />
								</tr>
							</thead>
							<tbody>
							<g:each in="${albumInstanceList}" status="i" var="albumInstance">
								<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
								
									<td><g:link action="show" id="${albumInstance.albumId}">${fieldValue(bean: albumInstance, field: "albumId")}</g:link></td>
								
									<td>${fieldValue(bean: albumInstance, field: "name")}</td>

									<td>${fieldValue(bean: albumInstance, field: "description")}</td>

                                    <td><g:link controller="photo" action="list" id="${albumInstance.albumId}"><img src="${fieldValue(bean: albumInstance, field: "image")}" width="${fieldValue(bean: albumInstance, field: "width")}" height="${fieldValue(bean: albumInstance, field: "height")}" alt="${fieldValue(bean: albumInstance, field: "name")}" title="${fieldValue(bean: albumInstance, field: "name")}"></g:link></td>
								
									<td>${fieldValue(bean: albumInstance, field: "location")}</td>
								
									<td>${albumInstance.geoLocation.latitude}${(albumInstance.geoLocation.longitude) ? ', ' + albumInstance.geoLocation.longitude + '' : ''}</td>

                                    <td>${(albumInstance.dateCreated)?.format("yyyy-MM-dd")}</td>
								
								</tr>
							</g:each>
							</tbody>
						</table>
						<div id="pagination">
							<g:remotePaginate action="ajaxList" update="listAlbum" max="${(grailsApplication.config.picasa.max) ? grailsApplication.config.picasa.max : 10}" maxsteps="${(grailsApplication.config.picasa.maxsteps) ? grailsApplication.config.picasa.maxsteps : 10}" total="${albumInstanceTotal}" />
						</div>
					</div>
