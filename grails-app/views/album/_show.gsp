
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Album" %>
                    <div id="showAlbum">
					<g:if test="${flash.message}">
    					<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<tbody>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="album.albumId.label" default="Album Id" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: albumInstance, field: "albumId")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="album.name.label" default="Name" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: albumInstance, field: "name")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="album.description.label" default="Description" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: albumInstance, field: "description")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="album.location.label" default="Location" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: albumInstance, field: "location")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="album.geoLocation.label" default="Geo Location" /></td>
									
									<td valign="top" class="value">${albumInstance?.geoLocation?.latitude}, ${albumInstance?.geoLocation?.longitude}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="album.photoCount.label" default="Photo Count" /></td>
									
									<td valign="top" class="value">${fieldValue(bean: albumInstance, field: "photoCount")}</td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="album.isPublic.label" default="Is Public" /></td>
									
									<td valign="top" class="value"><g:formatBoolean boolean="${albumInstance?.isPublic}" /></td>
									
								</tr>
							
								<tr class="prop">
									<td valign="top" class="name"><g:message code="album.dateCreated.label" default="Date Created" /></td>
									
									<td valign="top" class="value"><g:formatDate date="${albumInstance?.dateCreated}" /></td>
									
								</tr>
							
							</tbody>
						</table>
					 </div>
