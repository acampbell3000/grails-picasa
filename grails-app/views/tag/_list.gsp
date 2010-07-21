<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Tag" %>
                    <div id="listTag">
					<g:if test="${flash.message}">
						<div id="flashMessage">${flash.message}</div>
					</g:if>
						<div id="tagListing">
							<g:each in="${tagInstanceList}" status="tagIndex" var="tagInstance">${(tagIndex > 0) ? ", " : ""}<g:link controller="tag" action="show" id="${tagInstance?.keyword}"><span class="weight${tagInstance?.displayWeight}">${tagInstance?.keyword}</span></g:link></g:each>
						</div>

                        <div id="contentFooter">
                            <div id="pagination">
                                <g:remotePaginate action="ajaxList" update="listTag" max="${grailsApplication?.config?..picasa?.maxKeywords ?: 10}" maxsteps="${grailsApplication?.config?..picasa?.maxSteps ?: 10}" total="${tagInstanceTotal}" />
                            </div>
                            <div id="feeds">
                                <a href="${createLink(action: 'list')}/feed/rss">RSS</a> |
                                <a href="${createLink(action: 'list')}/feed/xml">XML</a> |
                                <a href="${createLink(action: 'list')}/feed/json">JSON</a>
                            </div>
                            <div class="cleaner"></div>
                        </div>
					</div>
