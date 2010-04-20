
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Photo" %>
                    <div id="showPhoto">
					<g:if test="${flash.message}">
    					<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<tbody>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.photoId.label" default="Photo Id" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "photoId")}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.title.label" default="Title" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "title")}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.description.label" default="Description" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "description")}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.cameraModel.label" default="Camera Model" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "cameraModel")}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.geoLocation.label" default="Geo Location" /></td>

									<td valign="top" class="value">${photoInstance?.geoLocation?.latitude}${(photoInstance?.geoLocation?.longitude) ? ', ' + photoInstance.geoLocation.longitude + '' : ''}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.width.label" default="Width" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "width")}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.height.label" default="Height" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "height")}</td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.isPublic.label" default="Is Public" /></td>

									<td valign="top" class="value"><g:formatBoolean boolean="${photoInstance?.isPublic}" /></td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.dateCreated.label" default="Date Created" /></td>

									<td valign="top" class="value"><g:formatDate date="${photoInstance?.dateCreated}" /></td>

								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.tags.label" default="Tags" /></td>

									<td valign="top" style="text-align: left;" class="value">
										<ul>
										<g:each in="${photoInstance.tags}" var="t">
											<li><g:link controller="tag" action="show" id="${t.keyword}">${t?.keyword}</g:link></li>
										</g:each>
										</ul>
									</td>

								</tr>

                                <g:if test="${photoInstance.previousPhotoId}">
								<tr class="prop">
                                    <td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.previousPhotoId.label" default="Previous Photo" /></td>

									<td valign="top" class="value"><g:photoLink action="ajaxShow" update="showPhoto" albumId="${photoInstance.albumId}" photoId="${photoInstance.previousPhotoId}"><g:message code="photo.previousPhotoId.label" default="Previous Photo" /></g:photoLink></td>

								</tr>
                                </g:if>

                                <g:if test="${photoInstance.nextPhotoId}">
								<tr class="prop">
                                    <td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Photo.nextPhotoId.label" default="Next Photo" /></td>

									<td valign="top" class="value"><g:photoLink action="ajaxShow" update="showPhoto" albumId="${photoInstance.albumId}" photoId="${photoInstance.nextPhotoId}"><g:message code="photo.nextPhotoId.label" default="Next Photo" /></g:photoLink></td>
								</tr>
                                </g:if>

							</tbody>
						</table>

                        <div id="photo">
                            <p><img src="${fieldValue(bean: photoInstance, field: "image")}" width="${fieldValue(bean: photoInstance, field: "width")}" height="${fieldValue(bean: photoInstance, field: "height")}" alt="${fieldValue(bean: photoInstance, field: "title")}" title="${fieldValue(bean: photoInstance, field: "title")}" /></p>
                        </div>

                        <g:if test="${photoInstance.comments != null && photoInstance.comments.size() > 0}">
                        <div id="comments">
                            <ul>
                            <g:each in="${photoInstance.comments}" var="c">
                                <g:if test="${c.author?.email == null || c.author?.email == ''}">
                                    <li>${c.author?.name}
                                </g:if>
                                <g:else>
                                    <li><a href="mailto:${c.author?.email}">${c.author?.name}</a>
                                </g:else>
                                <g:if test="${c.dateCreated != null}"> (<g:formatDate date="${c.dateCreated}" format="MMM, dd yyyy hh:mm aa" />)</g:if><br /><em>${c.message}</em></li>
                            </g:each>
                            </ul>
                        </div>
                        </g:if>
					 </div>
