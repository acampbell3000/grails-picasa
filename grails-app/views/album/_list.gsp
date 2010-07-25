<%@ page import="uk.co.anthonycampbell.grails.picasa.Album" %>
                    <div id="listAlbum">
					<g:if test="${flash.message}">
						<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<thead>
								<tr>
                                    <th><g:message code="uk.co.anthonycampbell.grails.picasa.Album.albumId.label" default="Album ID" /></th>
								
									<g:remoteSortableColumn action="ajaxList" update="listAlbum" property="name" title="${message(code: 'uk.co.anthonycampbell.grails.picasa.Album.name.label', default: 'Name')}" />
								
									<g:remoteSortableColumn action="ajaxList" update="listAlbum" property="description" title="${message(code: 'uk.co.anthonycampbell.grails.picasa.Album.description.label', default: 'Description')}" />
                                    
                                    <th><g:message code="uk.co.anthonycampbell.grails.picasa.Album.image.label" default="Image" /></th>

									<g:remoteSortableColumn action="ajaxList" update="listAlbum" property="location" title="${message(code: 'uk.co.anthonycampbell.grails.picasa.Album.location.label', default: 'Location')}" />

									<th><g:message code="uk.co.anthonycampbell.grails.picasa.Album.geoLocation.label" default="Geo Location" /></th>

									<g:sortableColumn action="ajaxList" update="listAlbum" property="dateCreated" title="${message(code: 'uk.co.anthonycampbell.grails.picasa.Album.dateCareated.label', default: 'Date Created')}" />
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
                      
                        <div id="contentFooter">
                            <div id="pagination">
                                <g:remotePaginate action="ajaxList" update="listAlbum" max="${grailsApplication?.config?.picasa?.max ?: 10}" maxsteps="${grailsApplication?.config?.picasa?.maxSteps ?: 10}" total="${albumInstanceTotal}" />
                            </div>
                            <div id="feeds">
                                <a href="${createLink(action: 'list')}/feed/rss">RSS</a> |
                                <a href="${createLink(action: 'list')}/feed/xml">XML</a> |
                                <a href="${createLink(action: 'list')}/feed/json">JSON</a>
                            </div>
                            <div class="cleaner"></div>
                        </div>
					</div>
