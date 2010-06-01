
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Comment" %>
                        <div id="listComment">
                        <g:if test="${commentInstanceList != null && commentInstanceList?.size() > 0}">
                            <table>
                            <g:each in="${commentInstanceList}" status="i" var="commentInstance">
                                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                <g:if test="${commentInstance.author?.email == null || commentInstance.author?.email == ''}">
                                    <td><strong>${commentInstance.author?.name}</strong>
                                </g:if>
                                <g:else>
                                    <td><a href="mailto:${commentInstance.author?.email}">${commentInstance.author?.name}</a>
                                </g:else>
                                <g:if test="${commentInstance.dateCreated != null}"> (<g:formatDate date="${commentInstance.dateCreated}" format="MMM, dd yyyy hh:mm aa" />)</g:if><br /><em>${commentInstance.message}</em></td>
                                </tr>
                            </g:each>
                            </table>

                            <div id="contentFooter">
                                <div id="pagination">
                                    <g:remotePaginate action="ajaxList" update="listComment" max="${(grailsApplication.config.picasa.max) ? grailsApplication.config.picasa.max : 10}" maxsteps="${(grailsApplication.config.picasa.maxSteps) ? grailsApplication.config.picasa.maxSteps : 10}" total="${commentInstanceTotal}" />
                                </div>
                                <div id="feeds">
                                    <a href="${createLink(action: 'list')}/feed/rss">RSS</a> |
                                    <a href="${createLink(action: 'list')}/feed/xml">XML</a> |
                                    <a href="${createLink(action: 'list')}/feed/json">JSON</a>
                                </div>
                                <div class="cleaner"></div>
                            </div>
                        </g:if>
                        </div>
