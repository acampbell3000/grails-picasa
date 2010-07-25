package uk.co.anthonycampbell.grails.picasa

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
 * Interface which defines the expected methods for the Picasa Service.
 * 
 * @author Anthony Campbell (anthonycampbell.co.uk)
 */
interface PicasaServiceInterface {
	/*
     * Attempt to re-connect to the Picasa web service using the new provided
     * configuration details.
     *
     * @param picasaUsername the Picasa account's username.
     * @param picasaPassword the Picasa account's password.
     * @param picasaApplicationName the application's name.
     * @param picasaImgmax the photo size to provide in requests through the Google GData API.
     * @param picasaThumbsize the thumbnail size to provide in requests through the Google GData API.
     * @param picasaMaxResults the maximum number of results to return to the view.
     * @param allowCache whether the cache is enabled for the Picasa service.
     * @param cacheTimeout how long the cache is valid before a purge is made.
     * @return whether a new connection was successfully made.
     */
    boolean connect(String picasaUsername, String picasaPassword,
            String picasaApplicationName, String picasaImgmax,
            String picasaThumbsize, String picasaMaxResults,
            String allowCache, String cacheTimeout)

    /*
     * Attempt to re-connect to the Picasa web service using the provided
     * connection details available in the grails-app/conf/Config.groovy file.
     *
     * @return whether a new connection was successfully made.
     */
    boolean reset()

    /**
     * Get the Album for the provided ID through the Google Picasa web service.
     *
     * @param albumId the provided album ID.
     * @param showAll whether to include hidden / private albums.
     * @return the retrieved album from the Google Picasa web service.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the selected album album.
     */
    def Album getAlbum(String albumId, boolean showAll) throws PicasaServiceException

    /**
     * Get the Photo for the provided IDs through the Google Picasa web service.
     *
     * @param albumId the provided album ID.
     * @param photoId the provided photo ID.
     * @param showAll whether to include hidden / private photo.
     * @return the retrieved photo from the Google Picasa web service.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the selected photo.
     */
    def Photo getPhoto(String albumId, String photoId, boolean showAll)
        throws PicasaServiceException

    /**
     * List the available albums for the configured Google Picasa account.
     *
     * @param showAll whether to include hidden / private albums in the list.
     * @return list of albums from the Google Picasa web service.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available albums.
     */
    def List<Album> listAllAlbums(boolean showAll) throws PicasaServiceException

    /**
     * List the available tags used by the Google Picasa web album user.
     *
     * @return list of tags for the provided Google Picasa web service user.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available tags.
     */
    def List<Tag> listAllTags() throws PicasaServiceException

    /**
     * List the most recent comments for the Google Picasa web album user.
     *
     * @return list of most recent comments for the Google Picasa web album user.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available comments.
     */
    def List<Comment> listAllComments() throws PicasaServiceException

    /**
     * List the available photos for the provided Google Picasa web album.
     *
     * @param albumId the provided album ID.
     * @param showAll whether to include hidden / private photos in the list.
     * @return list of photos for the provided Google Picasa web service album.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available photos.
     */
    def List<Photo> listPhotosForAlbum(String albumId, boolean showAll)
            throws PicasaServiceException

    /**
     * List the available photos for the provided Google Picasa web album tag keyword.
     *
     * @param tagKeyword the provided tag keyword.
     * @param showAll whether to include hidden / private photos in the list.
     * @return list of photos for the provided Google Picasa web service album tag.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available photos.
     */
    def List<Photo> listPhotosForTag(String tagKeyword, boolean showAll)
            throws PicasaServiceException

    /**
     * List the available tags for the provided Google Picasa web album.
     *
     * @param albumId the provided album ID.
     * @return list of tags for the provided Google Picasa web service album.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available tags.
     */
    def List<Tag> listTagsForAlbum(String albumId) throws PicasaServiceException

    /**
     * List the available comments for the provided Google Picasa web album photo.
     *
     * @param albumId the provided album ID.
     * @param photoId the provided photo ID.
     * @return list of comments for the provided Google Picasa web album photo.
     * @Exception PicasaServiceException when there's been a problem retrieving
     *      the list of available comments.
     */
    def List<Comment> listCommentsForPhoto(String albumId, String photoId)
            throws PicasaServiceException
}

