<%@ page import="uk.co.anthonycampbell.grails.picasa.Comment" %>
                        <div id="comments">
                        <g:render template="../comment/list" model="'commentInstanceList': commentInstanceList,
                                  'commentInstanceTotal': commentInstanceTotal]" />

                        <g:render template="../comment/create" model="['albumId': albumId,
                                  'photoId': photoId,
                                  'commentInstance': commentInstance]" />
                        </div>
