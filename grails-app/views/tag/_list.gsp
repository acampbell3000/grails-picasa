
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Tag" %>
                    <div id="listTag">
					<g:if test="${flash.message}">
						<div id="flashMessage">${flash.message}</div>
					</g:if>
						<div>
							<g:each in="${tagInstanceList}" status="tagIndex" var="tagInstance">${(tagIndex > 0) ? ", " : ""}<g:link controller="tag" action="show" id="${tagInstance?.keyword}">${tagInstance?.keyword}</g:link></g:each>
						</div>
						<div id="pagination">
							<g:remotePaginate action="ajaxList" update="listTag" max="${(grailsApplication.config.picasa.max) ? grailsApplication.config.picasa.max : 10}" maxsteps="${(grailsApplication.config.picasa.maxSteps) ? grailsApplication.config.picasa.maxSteps : 10}" total="${tagInstanceTotal}" />
						</div>
					</div>
