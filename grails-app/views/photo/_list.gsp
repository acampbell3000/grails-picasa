
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Album" %>
                    <div id="listPhoto">
					<g:if test="${flash.message}">
						<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<thead>
								<tr>
                                    <th><g:message code="photo.photoId.label" default="Photo Id" /></th>

                                    <g:sortableColumn property="title" title="${message(code: 'photo.title.label', default: 'Title')}" />

                                    <g:sortableColumn property="description" title="${message(code: 'photo.description.label', default: 'Description')}" />

                                    <th><g:message code="album.image.label" default="Image" /></th>

                                    <g:sortableColumn property="cameraModel" title="${message(code: 'photo.cameraModel.label', default: 'Camera Model')}" />

                                    <th><g:message code="photo.geoLocation.label" default="Geo Location" /></th>

                                    <g:sortableColumn property="dateCreated" title="${message(code: 'photo.dateCareated.label', default: 'Date Created')}" />
								</tr>
							</thead>
							<tbody>

							<g:each in="${photoInstanceList}" status="i" var="photoInstance">
								<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                    <td><a href="${createLink(controller: "photo", action: "show", id: photoInstance.albumId)}/${photoInstance.photoId}">${fieldValue(bean: photoInstance, field: "photoId")}</a></td>

                                    <td>${fieldValue(bean: photoInstance, field: "title")}</td>

                                    <td>${fieldValue(bean: photoInstance, field: "description")}</td>

                                    <td><a href="${createLink(controller: "photo", action: "show", id: photoInstance.albumId)}/${photoInstance.photoId}"><img src="${fieldValue(bean: photoInstance, field: "thumbnailImage")}" width="${fieldValue(bean: photoInstance, field: "thumbnailWidth")}" height="${fieldValue(bean: photoInstance, field: "thumbnailHeight")}" alt="${fieldValue(bean: photoInstance, field: "title")}" title="${fieldValue(bean: photoInstance, field: "title")}"></a></td>

                                    <td>${fieldValue(bean: photoInstance, field: "cameraModel")}</td>

                                    <td>${photoInstance?.geoLocation?.latitude}${(photoInstance?.geoLocation?.longitude) ? ', ' + photoInstance.geoLocation.longitude + '' : ''}</td>

                                    <td>${(photoInstance.dateCreated)?.format("yyyy-MM-dd")}</td>
								</tr>
							</g:each>
							</tbody>
						</table>
						<div id="pagination">
							<g:remotePaginate action="ajaxList" update="listPhoto" albumId="${albumId}" max="${(grailsApplication.config.picasa.max) ? grailsApplication.config.picasa.max : 10}" maxsteps="${(grailsApplication.config.picasa.maxsteps) ? grailsApplication.config.picasa.maxsteps : 10}" total="${photoInstanceTotal}" />
						</div>
					</div>
