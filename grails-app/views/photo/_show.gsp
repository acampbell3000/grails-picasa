
<%@ page import="uk.co.anthonycampbell.grails.picasa.Photo" %>
                    <div id="showPhoto">
					<g:if test="${flash.message}">
    					<div id="flashMessage">${flash.message}</div>
					</g:if>
						<table>
							<tbody>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.photoId.label" default="Photo Id" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "photoId")}</td>
								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.title.label" default="Title" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "title")}</td>
								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.description.label" default="Description" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "description")}</td>
								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.cameraModel.label" default="Camera Model" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "cameraModel")}</td>
								</tr>
								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.geoLocation.label" default="Geo Location" /></td>

									<td valign="top" class="value">${photoInstance?.geoLocation?.latitude}${(photoInstance?.geoLocation?.longitude) ? ', ' + photoInstance.geoLocation.longitude + '' : ''}</td>
								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.width.label" default="Width" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "width")}</td>
								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.height.label" default="Height" /></td>

									<td valign="top" class="value">${fieldValue(bean: photoInstance, field: "height")}</td>
								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.isPublic.label" default="Is Public" /></td>

									<td valign="top" class="value"><g:formatBoolean boolean="${photoInstance?.isPublic}" /></td>
								</tr>

								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.dateCreated.label" default="Date Created" /></td>

									<td valign="top" class="value"><g:formatDate date="${photoInstance?.dateCreated}" /></td>
								</tr>
                                
                                <g:if test="${photoInstance.tags != null && photoInstance.tags?.size() > 0}">
								<tr class="prop">
									<td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.tags.label" default="Tags" /></td>

									<td valign="top" style="text-align: left;" class="value">
										<ul>
										<g:each in="${photoInstance.tags}" var="t">
											<li><g:link controller="tag" action="show" id="${t.keyword}"><span class="weight${t?.displayWeight}">${t?.keyword}</span></g:link></li>
										</g:each>
										</ul>
									</td>
								</tr>
                                </g:if>

                                <g:if test="${photoInstance.previousPhotoId}">
								<tr class="prop">
                                    <td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.previousPhotoId.label" default="Previous Photo" /></td>

									<td valign="top" class="value"><g:photoLink action="ajaxShow" update="showPhoto" albumId="${photoInstance.albumId}" photoId="${photoInstance.previousPhotoId}"><g:message code="photo.previousPhotoId.label" default="Previous Photo" /></g:photoLink></td>
								</tr>
                                </g:if>

                                <g:if test="${photoInstance.nextPhotoId}">
								<tr class="prop">
                                    <td valign="top" class="name"><g:message code="uk.co.anthonycampbell.grails.picasa.Photo.nextPhotoId.label" default="Next Photo" /></td>

									<td valign="top" class="value"><g:photoLink action="ajaxShow" update="showPhoto" albumId="${photoInstance.albumId}" photoId="${photoInstance.nextPhotoId}"><g:message code="photo.nextPhotoId.label" default="Next Photo" /></g:photoLink></td>
								</tr>
                                </g:if>

							</tbody>
						</table>

                        <g:if test="${photoInstance.image}">
                        <div id="photo">
                            <p><img src="${fieldValue(bean: photoInstance, field: "image")}" width="${fieldValue(bean: photoInstance, field: "width")}" height="${fieldValue(bean: photoInstance, field: "height")}" alt="${fieldValue(bean: photoInstance, field: "title")}" title="${fieldValue(bean: photoInstance, field: "title")}" /></p>
                        </div>
                        </g:if>

                        <g:if test="${photoInstance?.geoLocation?.latitude && photoInstance?.geoLocation?.longitude}">
                        <resource:map key="ABQIAAAA3yQx29uKg-gW8uSQ7UIUWxQgtWBfj3Ey6DKe-UJBP8jTKOFWThQiC3oWv1J1FLCRGIhhN0mbsjF9SA" zoomLevel="14" />

                        <div id="map">
                            <!-- <richui:map lat="${photoInstance?.geoLocation?.latitude}" lng="${photoInstance?.geoLocation?.longitude}" zoomLevel="14" /> -->

                            <script type='text/javascript'>//<![CDATA[
var gi;
function loadMap() {
	gi = new GoogleMapIntegration();
	gi.setDirectionsId("de47ee809fc9f27d3026273598b7280d5");
	gi.setDraggable(null);
	gi.setLatitudeId("latitude");
	gi.setLongitudeId("longitude");
	gi.setMapId("gimap");
	gi.setZoomLevel(14);
	gi.setShowStartMarker(true);
	gi.load(new GLatLng(41.385586, 2.169923));
}
addEvent(window, "load", loadMap(), false);
window.onunload = function () {
	GUnload();
}
//]]>
</script> 
<div class='' style='width: 500px; background: #F1F1F1; border: 1px solid #F1F1F1;"'> 
  <div id='gimap' style='width: 500px; height: 300px;' class=''></div> 
</div> 
                        </div>
                        </g:if>

                        <g:render template="../comment/comments" model="['albumId': albumId,
                                  'photoId': photoId,
                                  'commentInstanceList': commentInstanceList,
                                  'commentInstanceTotal': commentInstanceTotal,
                                  'commentInstance': commentInstance]" />
					 </div>
