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
                        <div id="map">

<script type="text/javascript">
    function initialize() {
        var myLatlng = new google.maps.LatLng("${photoInstance?.geoLocation?.latitude}",
            "${photoInstance?.geoLocation?.longitude}");
        var myOptions = {
            zoom: 14,
            center: myLatlng,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        }
        var map = new google.maps.Map(document.getElementById("gimap"), myOptions);

        var marker = new google.maps.Marker({
            position: myLatlng,
            map: map,
            title: "${fieldValue(bean: photoInstance, field: "description")}"
        });
    }

    function loadScript() {
        var script = document.createElement("script");
        script.type = "text/javascript";
        script.src = "http://maps.google.com/maps/api/js?sensor=false&callback=initialize";
        document.body.appendChild(script);
    }

    window.onload = loadScript;
</script>
<div class='' style='width: 250px; background: #F1F1F1; border: 1px solid #F1F1F1;"'>
    <div id='gimap' style='width: 250px; height: 250px;' class=''>
        <img src="http://maps.google.com/maps/api/staticmap?center=${photoInstance?.geoLocation?.latitude},${photoInstance?.geoLocation?.longitude}&zoom=14&size=250x250&markers=color:red|${photoInstance?.geoLocation?.latitude},${photoInstance?.geoLocation?.longitude}&sensor=false" onclick="initialize()">
    </div>
</div>

                        </div>
                        </g:if>

                        <g:render template="../comment/comments" model="['albumId': albumId,
                                  'photoId': photoId,
                                  'commentInstanceList': commentInstanceList,
                                  'commentInstanceTotal': commentInstanceTotal,
                                  'commentInstance': commentInstance]" />
					 </div>
