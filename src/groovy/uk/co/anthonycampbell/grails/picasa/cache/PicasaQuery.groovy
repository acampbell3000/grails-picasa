package uk.co.anthonycampbell.grails.picasa.cache

/**
 * Copyright 2010 Anthony Campbell (anthonycampbell.co.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Enum used as keys for the Picasa cache to store values for the
 * relevant query.
 *
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
public enum PicasaQuery {
    // Declare available methods
    GET_ALBUM("getAlbum"),
    GET_PHOTO("getPhoto"),
    LIST_ALL_ALBUMS("listAllAlbums"),
    LIST_ALL_TAGS("listAllTags"),
    LIST_ALL_COMMENTS("listAllComments"),
    LIST_PHOTOS_FOR_ALBUM("listPhotosForAlbum"),
    LIST_PHOTOS_FOR_TAG("listPhotosForTag"),
    LIST_TAGS_FOR_ALBUM("listTagsForAlbum"),
    LIST_COMMENTS_FOR_PHOTO("listCommentsForPhoto")

    private final String method

    /**
     * Constructor
     */
    PicasaQuery(final String method) {
        this.method = method
    }

    /**
     * Return the method name for this enum.
     */
    public String getMethod() {
        return this.method
    }
}
