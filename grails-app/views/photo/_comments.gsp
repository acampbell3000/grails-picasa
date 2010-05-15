
<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Photo" %>
                        <div id="comments">
                        <g:if test="${photoComments != null && photoComments?.size() > 0}">
                            <table>
                            <g:each in="${photoComments}" status="i" var="commentInstance">
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
                          
                            <div id="pagination">
                                <g:remotePaginate action="comments" update="comments" albumId="${albumId}" photoId="${photoId}" max="${(grailsApplication.config.picasa.maxComments) ? grailsApplication.config.picasa.maxComments : 10}" maxsteps="${(grailsApplication.config.picasa.maxCommentSteps) ? grailsApplication.config.picasa.maxCommentSteps : 10}" total="${photoCommentTotal}" />
                            </div>
                            <div class="cleaner"></div>
                        </g:if>
                        </div>
