
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Photo" %>
                    <div id="showPhoto">
					<g:if test="${flash.message}">
    					<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<tbody>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="photo.photoId.label" default="Photo Id" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "photoId")}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="photo.title.label" default="Title" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "title")}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="photo.description.label" default="Description" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "description")}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="photo.cameraModel.label" default="Camera Model" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "cameraModel")}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="photo.geoLocation.label" default="Geo Location" /></td>

									<td valign="top" class="value">${photoInstance?.geoLocation?.latitude}${(photoInstance?.geoLocation?.longitude) ? ', ' + photoInstance.geoLocation.longitude + '' : ''}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="photo.width.label" default="Width" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "width")}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="photo.height.label" default="Height" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "height")}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="photo.isPublic.label" default="Is Public" /></td>

									<td valign="top" class="value"><g:formatBoolean boolean="${photoInstance?.isPublic}" /></td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="photo.tags.label" default="Tags" /></td>

									<td valign="top" style="text-align: left;" class="value">
										<ul>
										<g:each in="${photoInstance.tags}" var="t">
											<li><g:link controller="tag" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></li>
										</g:each>
										</ul>
									</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="photo.dateCreated.label" default="Date Created" /></td>

									<td valign="top" class="value"><g:formatDate date="${photoInstance?.dateCreated}" /></td>

								</tr>

								<tr class="prop">
                                    <td valign="top" class="name"><g:message code="photo.previousPhotoId.label" default="Previous Photo" /></td>

									<td valign="top" class="value"><g:photoLink action="ajaxShow" update="showPhoto" albumId="${photoInstance.albumId}" photoId="${photoInstance.previousPhotoId}"><g:message code="photo.previousPhotoId.label" default="Previous Photo" /></g:photoLink></td>

								</tr>

								<tr class="prop">
                                    <td valign="top" class="name"><g:message code="photo.nextPhotoId.label" default="Next Photo" /></td>

									<td valign="top" class="value"><g:photoLink action="ajaxShow" update="showPhoto" albumId="${photoInstance.albumId}" photoId="${photoInstance.nextPhotoId}"><g:message code="photo.nextPhotoId.label" default="Next Photo" /></g:photoLink></td>
								</tr>

							</tbody>
						</table>

                        <div id="photo">
                            <p><img src="${fieldValue(bean: photoInstance, field: "image")}" width="${fieldValue(bean: photoInstance, field: "width")}" height="${fieldValue(bean: photoInstance, field: "height")}" alt="${fieldValue(bean: photoInstance, field: "title")}" title="${fieldValue(bean: photoInstance, field: "title")}" /></p>
                        </div>
					 </div>
