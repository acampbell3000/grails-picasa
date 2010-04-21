
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Album" %>
                    <div id="showAlbum">
					<g:if test="${flash.message}">
    					<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<tbody>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.albumId.label" default="Album ID" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: albumInstance, field: "albumId")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.name.label" default="Name" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: albumInstance, field: "name")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.description.label" default="Description" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: albumInstance, field: "description")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.location.label" default="Location" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: albumInstance, field: "location")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.geoLocation.label" default="Geo Location" /></td>
									
									<td valign="top" class="value">${albumInstance?.geoLocation?.latitude}${(albumInstance?.geoLocation?.longitude) ? ', ' + albumInstance.geoLocation.longitude + '' : ''}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.photoCount.label" default="Photo Count" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: albumInstance, field: "photoCount")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.isPublic.label" default="Is Public" /></td>
									
									<td valign="top" class="value"><g:formatBoolean boolean="${albumInstance?.isPublic}" /></td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.dateCreated.label" default="Date Created" /></td>
									
									<td valign="top" class="value"><g:formatDate date="${albumInstance?.dateCreated}" /></td>
									
								</tr>

                                <g:if test="${photoInstance.tags != null && photoInstance.tags.size() > 0}">
								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.plugins.picasa.Album.tags.label" default="Tags" /></td>

									<td valign="top" style="text-align: left;" class="value">
										<ul>
										<g:each in="${albumInstance.tags}" var="t">
											<li><g:link controller="tag" action="show" id="${t.keyword}">${t?.keyword}</g:link></li>
										</g:each>
										</ul>
									</td>

								</tr>
                                </g:if>
							</tbody>
						</table>
					 </div>
