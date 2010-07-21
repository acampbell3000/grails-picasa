<%@ page import="uk.co.anthonycampbell.grails.plugins.picasa.Comment"
         import="org.apache.commons.lang.StringUtils" %>
                          <div id="listComment">
                          <g:if test="${commentInstanceList && commentInstanceList?.size() > 0}">
                              <table>
                              <g:each in="${commentInstanceList}" status="i" var="commentInstance">
                                  <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                  <g:if test="${!commentInstance.author?.email}">
                                      <td><strong>${commentInstance.author?.name}</strong>
                                  </g:if>
                                  <g:else>
                                      <td><a href="mailto:${commentInstance.author?.email}">${commentInstance.author?.name}</a>
                                  </g:else>
                                  <g:if test="${commentInstance.dateCreated}"> (<g:formatDate date="${commentInstance.dateCreated}" format="MMM, dd yyyy hh:mm aa" />)</g:if><br /><em>${commentInstance.message}</em></td>
                                  </tr>
                              </g:each>
                              </table>

                              <div id="contentFooter">
                                  <div id="pagination">
                                      <g:remotePaginate controller="comment" action="ajaxList" update="listComment" albumId="${albumId}" photoId="${photoId}" max="${grailsApplication?.config?.picasa?.maxComments ?: 10}" maxsteps="${grailsApplication?.config?.picasa?.maxCommentSteps ?: 10}" total="${commentInstanceTotal}" />
                                  </div>
                                  <div id="feeds">
                                      <a href="${createLink(controller: 'comment', action: 'list')}/${(StringUtils.isNotEmpty(albumId) && StringUtils.isNotEmpty(photoId)) ? "/$albumId/$photoId/" : "" }feed/rss">RSS</a> |
                                      <a href="${createLink(controller: 'comment', action: 'list')}/${(StringUtils.isNotEmpty(albumId) && StringUtils.isNotEmpty(photoId)) ? "/$albumId/$photoId/" : "" }feed/xml">XML</a> |
                                      <a href="${createLink(controller: 'comment', action: 'list')}/${(StringUtils.isNotEmpty(albumId) && StringUtils.isNotEmpty(photoId)) ? "/$albumId/$photoId/" : "" }feed/json">JSON</a>
                                  </div>
                                  <div class="cleaner"></div>
                              </div>
                          </g:if>
                          </div>
