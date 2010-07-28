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
interface PicasaCommentServiceInterface {
    /*
     * Attempt to re-connect to the Picasa web service using the new provided
     * configuration details.
     *
     * @param picasaApplicationName the application's name.
     * @param picasaConsumerKey the picasa OAuth consumer key.
     * @param picasaConsumerSecret the picasa OAuth consumer secret.
     * @param allowComments whether to allow comments to be posted.
     * @return whether a new connection was successfully made.
     */
    boolean connect(String picasaApplicationName, String picasaConsumerKey,
            String picasaConsumerSecret, String allowComments)

    /*
     * Attempt to re-connect to the Picasa web service using the provided
     * connection details available in the grails-app/conf/Config.groovy file.
     *
     * @return whether a new connection was successfully made.
     */
    boolean reset()
    
    /**
     * Apply the provided access token to the Picasa service.
     *
     * @param the access token.
     * @param the access token secret.
     * @throw PicasaCommentServiceException Unable to apply OAuth access.
     */
    def applyOAuthAccess(String token, String secret) throws PicasaCommentServiceException
    
    /**
     * Reset the picasa comments web service and update session.
     */
    def removeOAuthAccess()
    
    /**
     * Post the provided comment through the Google Picasa web service.
     *
     * @param comment the provided comment to post.
     * @throw PicasaCommentServiceException when there's been a problem posting
     *      the provided comment.
     */
    def postComment(Comment comment) throws PicasaCommentServiceException
}

